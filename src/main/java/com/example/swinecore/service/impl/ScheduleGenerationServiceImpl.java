package com.example.swinecore.service.impl;

import com.example.swinecore.entity.*;
import com.example.swinecore.entity.enums.PigStatus;
import com.example.swinecore.entity.enums.RuleType;
import com.example.swinecore.entity.enums.TaskStatus;
import com.example.swinecore.repository.AttendanceRepository;
import com.example.swinecore.repository.DailyTaskRepository;
import com.example.swinecore.repository.InventoryRepository;
import com.example.swinecore.repository.PigRepository;
import com.example.swinecore.repository.RuleScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Automated daily schedule engine.
 *
 * Triggered when staff clocks in.
 * Generates:
 * - Standard daily care tasks
 * - Rule-based vaccination tasks
 * - Rule-based medication tasks
 * - Rule-based feeding tasks
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleGenerationServiceImpl {

    private final DailyTaskRepository taskRepository;
    private final PigRepository pigRepository;
    private final RuleScheduleRepository ruleRepository;
    private final InventoryRepository inventoryRepository;
    private final AttendanceRepository attendanceRepository;

    /**
     * Generate today's task checklist for all attended staff in a building.
     */
    public void generateDailySchedule(Building building) {
        if (building == null || building.getFarm() == null) {
            return;
        }

        LocalDate today = LocalDate.now();

        /*
         * Skip if tasks already generated today.
         * This prevents duplicate daily tasks.
         */
        if (!taskRepository.findByBuildingAndTaskDate(building, today).isEmpty()) {
            return;
        }

        List<User> attendedStaff = attendanceRepository.findAttendedStaffByBuildingAndDate(
                building.getId(),
                today);

        if (attendedStaff.isEmpty()) {
            return;
        }

        List<Pig> activePigs = fetchActivePigs(building);

        List<RuleSchedule> rules = ruleRepository.findByFarmAndActiveTrue(building.getFarm());

        List<DailyTask> tasks = new ArrayList<>();
        int staffIndex = 0;

        /*
         * Standard daily task for every attended staff.
         */
        for (User staff : attendedStaff) {
            tasks.add(DailyTask.builder()
                    .building(building)
                    .assignedStaff(staff)
                    .taskDate(today)
                    .taskName("Daily Care Routine")
                    .taskDescription("Perform daily care routine for all assigned pigs in " + building.getName())
                    .isStandardTask(true)
                    .status(TaskStatus.PENDING)
                    .build());
        }

        /*
         * Rule-based tasks.
         */
        for (Pig pig : activePigs) {
            long ageInDays = pig.getAgeInDays();

            for (RuleSchedule rule : rules) {
                if (rule == null || rule.getRuleType() == null || !rule.isActive()) {
                    continue;
                }

                boolean triggered = false;

                if (rule.getRuleType() == RuleType.VACCINATION
                        || rule.getRuleType() == RuleType.MEDICATION) {

                    if (rule.getDayFromBirth() != null
                            && rule.getDayFromBirth() == ageInDays) {
                        triggered = true;
                    }

                } else if (rule.getRuleType() == RuleType.FEEDING) {

                    if (rule.getDayRangeStart() != null
                            && rule.getDayRangeEnd() != null) {
                        triggered = ageInDays >= rule.getDayRangeStart()
                                && ageInDays <= rule.getDayRangeEnd();
                    }
                }

                if (!triggered || !matchesCategory(pig, rule)) {
                    continue;
                }

                User assignedStaff = attendedStaff.get(staffIndex % attendedStaff.size());
                staffIndex++;

                String taskDescription = buildTaskDescription(rule, building, pig);

                if (rule.getRuleType() == RuleType.VACCINATION
                        || rule.getRuleType() == RuleType.MEDICATION) {

                    boolean stockShortage = checkStockShortage(building.getFarm(), rule);

                    if (stockShortage) {
                        taskDescription += " [STOCK_SHORTAGE_WARNING]";
                    }
                }

                tasks.add(DailyTask.builder()
                        .building(building)
                        .assignedStaff(assignedStaff)
                        .taskDate(today)
                        .taskName(buildTaskName(rule))
                        .taskDescription(taskDescription)
                        .isStandardTask(false)
                        .ruleSchedule(rule)
                        .pig(pig)
                        .status(TaskStatus.PENDING)
                        .build());
            }
        }

        if (!tasks.isEmpty()) {
            taskRepository.saveAll(tasks);
        }
    }

    /**
     * Fetch active pigs in a building.
     *
     * Current PigStatus:
     * PIGLET, GROWER, FINISHER, BREEDING_SOW, BREEDING_BOAR,
     * PENDING_SALE_APPROVAL, FOR_SALE, SOLD
     *
     * SOLD pigs are skipped.
     * PENDING_SALE_APPROVAL pigs are still in the farm, so they remain active.
     */
    private List<Pig> fetchActivePigs(Building building) {
        List<Pig> pigs = new ArrayList<>();

        for (PigStatus status : PigStatus.values()) {
            if (status == PigStatus.SOLD) {
                continue;
            }

            pigs.addAll(pigRepository.findByBuildingAndStatus(building, status));
        }

        return pigs;
    }

    /**
     * Check if inventory has sufficient stock for vaccine/medication.
     */
    private boolean checkStockShortage(Farm farm, RuleSchedule rule) {
        String stockName = rule.getMedication();

        if (stockName == null || stockName.isBlank()) {
            stockName = rule.getName();
        }

        if (stockName == null || stockName.isBlank()) {
            return false;
        }

        return inventoryRepository.findByFarmAndFeedType(farm, stockName)
                .map(inv -> inv.getQuantityKg() == null || inv.getQuantityKg() <= 0)
                .orElse(true);
    }

    private boolean matchesCategory(Pig pig, RuleSchedule rule) {
        if (pig == null || pig.getStatus() == null) {
            return false;
        }

        if (rule.getAppliesTo() == null || rule.getAppliesTo().isBlank()) {
            return false;
        }

        if ("ALL".equalsIgnoreCase(rule.getAppliesTo())) {
            return true;
        }

        return pig.getStatus().name().equalsIgnoreCase(rule.getAppliesTo());
    }

    private String buildTaskName(RuleSchedule rule) {
        return rule.getRuleType() + ": " + rule.getName();
    }

    private String buildTaskDescription(RuleSchedule rule, Building building, Pig pig) {
        StringBuilder sb = new StringBuilder();

        switch (rule.getRuleType()) {
            case VACCINATION:
                sb.append("Administer ").append(rule.getName());
                sb.append(" to pig in building ").append(building.getCode());

                if (rule.getDosage() != null && !rule.getDosage().isBlank()) {
                    sb.append(" (Dosage: ").append(rule.getDosage()).append(")");
                }

                if (rule.getAdministrationRoute() != null && !rule.getAdministrationRoute().isBlank()) {
                    sb.append(" via ").append(rule.getAdministrationRoute());
                }
                break;

            case MEDICATION:
                sb.append("Administer ");

                if (rule.getMedication() != null && !rule.getMedication().isBlank()) {
                    sb.append(rule.getMedication());
                } else {
                    sb.append(rule.getName());
                }

                sb.append(" to pig in building ").append(building.getCode());

                if (rule.getDosage() != null && !rule.getDosage().isBlank()) {
                    sb.append(" (Dosage: ").append(rule.getDosage()).append(")");
                }

                if (rule.getAdministrationRoute() != null && !rule.getAdministrationRoute().isBlank()) {
                    sb.append(" via ").append(rule.getAdministrationRoute());
                }
                break;

            case FEEDING:
                sb.append("Feed ");

                if (rule.getFeedType() != null && !rule.getFeedType().isBlank()) {
                    sb.append(rule.getFeedType());
                } else {
                    sb.append(rule.getName());
                }

                if (rule.getFeedAmountKg() != null) {
                    sb.append(" — ").append(rule.getFeedAmountKg()).append(" kg per pig");
                }
                break;

            default:
                if (rule.getDescription() != null && !rule.getDescription().isBlank()) {
                    sb.append(rule.getDescription());
                } else {
                    sb.append(rule.getName());
                }
        }

        if (pig != null) {
            sb.append(" | Target: ").append(pig.getCode());
        }

        return sb.toString();
    }
}