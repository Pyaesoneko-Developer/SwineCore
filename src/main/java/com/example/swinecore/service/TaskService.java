package com.example.swinecore.service;

import com.example.swinecore.entity.*;
import com.example.swinecore.entity.enums.PigStatus;
import com.example.swinecore.entity.enums.TaskStatus;
import com.example.swinecore.repository.DailyTaskRepository;
import com.example.swinecore.repository.PigRepository;
import com.example.swinecore.repository.RuleScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final DailyTaskRepository taskRepository;
    private final PigRepository pigRepository;
    private final RuleScheduleRepository ruleRepo;
    private final AttendanceService attendanceService;

    /**
     * Generate the day's task list for all attended staff in a building.
     * Called automatically when any staff clocks in.
     */
    public void generateDailyTasks(Building building) {
        LocalDate today = LocalDate.now();

        if (building == null) {
            return;
        }

        if (!taskRepository.findByBuildingAndTaskDate(building, today).isEmpty()) {
            return;
        }

        List<User> attendedStaff = attendanceService.getAttendedStaffForBuilding(building.getId());

        if (attendedStaff.isEmpty()) {
            return;
        }

        List<DailyTask> tasks = new ArrayList<>();

        for (User staff : attendedStaff) {
            tasks.add(DailyTask.builder()
                    .building(building)
                    .assignedStaff(staff)
                    .taskDate(today)
                    .taskName("Maintain and Care Pigs")
                    .taskDescription("Perform daily care routine for all assigned pigs in " + building.getName())
                    .isStandardTask(true)
                    .status(TaskStatus.PENDING)
                    .build());
        }

        List<Pig> activePigs = new ArrayList<>();

        activePigs.addAll(pigRepository.findByBuildingAndStatus(building, PigStatus.PIGLET));
        activePigs.addAll(pigRepository.findByBuildingAndStatus(building, PigStatus.GROWER));
        activePigs.addAll(pigRepository.findByBuildingAndStatus(building, PigStatus.FINISHER));
        activePigs.addAll(pigRepository.findByBuildingAndStatus(building, PigStatus.BREEDING_SOW));
        activePigs.addAll(pigRepository.findByBuildingAndStatus(building, PigStatus.BREEDING_BOAR));

        List<RuleSchedule> rules = ruleRepo.findByFarmAndActiveTrue(building.getFarm());

        int staffIdx = 0;

        for (Pig pig : activePigs) {
            long ageInDays = pig.getAgeInDays();

            for (RuleSchedule rule : rules) {
                boolean triggered = false;

                if (rule.getDayFromBirth() != null && rule.getDayFromBirth() == ageInDays) {
                    triggered = true;
                } else if (rule.getDayRangeStart() != null && rule.getDayRangeEnd() != null) {
                    triggered = ageInDays >= rule.getDayRangeStart()
                            && ageInDays <= rule.getDayRangeEnd();
                }

                if (triggered && matchesCategory(pig, rule)) {
                    User assignedStaff = attendedStaff.get(staffIdx % attendedStaff.size());
                    staffIdx++;

                    tasks.add(DailyTask.builder()
                            .building(building)
                            .assignedStaff(assignedStaff)
                            .taskDate(today)
                            .taskName(rule.getRuleType() + ": " + rule.getName())
                            .taskDescription(buildDescription(rule))
                            .isStandardTask(false)
                            .ruleSchedule(rule)
                            .pig(pig)
                            .status(TaskStatus.PENDING)
                            .build());
                }
            }
        }

        taskRepository.saveAll(tasks);
    }

    public void submitTask(Long taskId, String staffNotes, User submitter) {
        DailyTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (task.getAssignedStaff() == null
                || !task.getAssignedStaff().getId().equals(submitter.getId())) {
            throw new SecurityException("Task not assigned to this staff member.");
        }

        if (attendanceService.isClockedOut(submitter)) {
            throw new IllegalStateException("Clocked-out staff cannot submit reports.");
        }

        if (task.getTaskDate() == null || !task.getTaskDate().isEqual(LocalDate.now())) {
            throw new IllegalStateException("This task can be submitted only on its task date.");
        }

        if (task.getStatus() != TaskStatus.PENDING && task.getStatus() != TaskStatus.REJECTED) {
            throw new IllegalStateException("Only pending or rejected tasks can be submitted.");
        }

        task.setStatus(TaskStatus.SUBMITTED);
        task.setStaffNotes(staffNotes);
        task.setSubmittedAt(LocalDateTime.now());

        taskRepository.save(task);
    }

    public void approveTask(Long taskId, User supervisor) {
        DailyTask task = getTaskForBuilding(taskId, supervisor);

        task.setStatus(TaskStatus.APPROVED);
        task.setReviewedBy(supervisor);
        task.setReviewedAt(LocalDateTime.now());

        taskRepository.save(task);
    }

    public void rejectTask(Long taskId, String comments, User supervisor) {
        DailyTask task = getTaskForBuilding(taskId, supervisor);

        task.setStatus(TaskStatus.REJECTED);
        task.setSupervisorComments(comments);
        task.setReviewedBy(supervisor);
        task.setReviewedAt(LocalDateTime.now());

        taskRepository.save(task);
    }

    public List<DailyTask> getTasksForStaffToday(User staff) {
        if (staff == null) {
            return List.of();
        }

        return taskRepository.findByAssignedStaffAndTaskDate(staff, LocalDate.now());
    }

    /**
     * Staff tasks page:
     * Today + next N days.
     *
     * Example:
     * days = 2 means today, tomorrow, and the next day.
     */
    public List<DailyTask> getTasksForStaffNextDays(User staff, int days) {
        if (staff == null) {
            return List.of();
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        return taskRepository.findByAssignedStaffAndTaskDateBetweenOrderByTaskDateAscIdAsc(
                staff,
                today,
                endDate);
    }

    /**
     * Manager daily tasks page:
     * Farm tasks between start date and end date.
     *
     * Used for today + next 2 days.
     */
    public List<DailyTask> getTasksForFarmBetweenDates(Farm farm, LocalDate startDate, LocalDate endDate) {
        if (farm == null || startDate == null || endDate == null) {
            return List.of();
        }

        return taskRepository.findByAssignedStaff_Building_FarmAndTaskDateBetweenOrderByTaskDateAscIdAsc(
                farm,
                startDate,
                endDate);
    }

    /**
     * Staff task history:
     * tasks already submitted / approved / rejected.
     * Latest submitted task appears first.
     */
    public List<DailyTask> getCompletedTaskHistoryForStaff(User staff) {
        if (staff == null) {
            return List.of();
        }

        List<DailyTask> records = taskRepository.findByAssignedStaffAndStatusInOrderBySubmittedAtDescIdDesc(
                staff,
                List.of(
                        TaskStatus.SUBMITTED,
                        TaskStatus.APPROVED,
                        TaskStatus.REJECTED));

        return records.stream()
                .sorted(Comparator
                        .comparing(
                                (DailyTask task) -> task.getSubmittedAt() != null
                                        ? task.getSubmittedAt()
                                        : task.getReviewedAt() != null
                                                ? task.getReviewedAt()
                                                : LocalDateTime.MIN)
                        .reversed()
                        .thenComparing(DailyTask::getId, Comparator.reverseOrder()))
                .toList();
    }

    public List<DailyTask> getPendingReviewForBuilding(Building building) {
        if (building == null) {
            return List.of();
        }

        return taskRepository.findByBuildingAndTaskDateAndStatus(
                building,
                LocalDate.now(),
                TaskStatus.SUBMITTED);
    }

    public List<DailyTask> getTasksByBuilding(Building building, LocalDate date) {
        if (building == null || date == null) {
            return List.of();
        }

        return taskRepository.findByBuildingAndTaskDate(building, date);
    }

    private DailyTask getTaskForBuilding(Long taskId, User supervisor) {
        DailyTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (supervisor.getBuilding() == null) {
            throw new SecurityException("Supervisor has no assigned building.");
        }

        if (task.getBuilding() == null
                || !task.getBuilding().getId().equals(supervisor.getBuilding().getId())) {
            throw new SecurityException("Task not in supervisor's building.");
        }

        return task;
    }

    private boolean matchesCategory(Pig pig, RuleSchedule rule) {
        if (pig == null || rule == null || rule.getAppliesTo() == null) {
            return false;
        }

        if ("ALL".equals(rule.getAppliesTo())) {
            return true;
        }

        return pig.getStatus() != null
                && pig.getStatus().name().equals(rule.getAppliesTo());
    }

    private String buildDescription(RuleSchedule rule) {
        StringBuilder sb = new StringBuilder();

        if (rule.getMedication() != null && !rule.getMedication().isBlank()) {
            sb.append("Medication: ").append(rule.getMedication());
        }

        if (rule.getDosage() != null && !rule.getDosage().isBlank()) {
            sb.append(" | Dosage: ").append(rule.getDosage());
        }

        if (rule.getFeedAmountKg() != null) {
            sb.append(" | Feed: ").append(rule.getFeedAmountKg()).append("kg");
        }

        if (rule.getFeedType() != null && !rule.getFeedType().isBlank()) {
            sb.append(" (").append(rule.getFeedType()).append(")");
        }

        if (rule.getDescription() != null && !rule.getDescription().isBlank() && sb.length() == 0) {
            sb.append(rule.getDescription());
        }

        return sb.toString();
    }
}