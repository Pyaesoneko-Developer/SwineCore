package com.example.swinecore.controller;

import com.example.swinecore.entity.Attendance;
import com.example.swinecore.entity.Building;
import com.example.swinecore.entity.DailyTask;
import com.example.swinecore.entity.Genetics;
import com.example.swinecore.entity.Pig;
import com.example.swinecore.entity.User;
import com.example.swinecore.entity.enums.PigStatus;
import com.example.swinecore.service.AttendanceService;
import com.example.swinecore.service.GeneticsService;
import com.example.swinecore.service.PigService;
import com.example.swinecore.service.ShiftService;
import com.example.swinecore.service.TaskService;
import com.example.swinecore.service.UserService;
import com.example.swinecore.service.impl.ScheduleGenerationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/staff")
@PreAuthorize("hasRole('STAFF')")
@RequiredArgsConstructor
public class StaffController {

        private final UserService userService;
        private final AttendanceService attendanceService;
        private final TaskService taskService;
        private final PigService pigService;
        private final GeneticsService geneticsService;
        private final ScheduleGenerationServiceImpl scheduleGenerationService;
        private final ShiftService shiftService;

        private User currentStaff(Principal p) {
                return userService.findByEmail(p.getName()).orElseThrow();
        }

        // =========================================================
        // BREEDING / BIRTH FLOW CONTROL
        // =========================================================

        private LocalDate latestRecordDate(Pig pig, String marker) {
                if (pig == null || pig.getNotes() == null || pig.getNotes().isBlank()) {
                        return null;
                }

                Pattern pattern = Pattern.compile(
                                Pattern.quote(marker) + "\\s*\\RDate:\\s*(\\d{4}-\\d{2}-\\d{2})");

                Matcher matcher = pattern.matcher(pig.getNotes());

                LocalDate latest = null;

                while (matcher.find()) {
                        LocalDate date = LocalDate.parse(matcher.group(1));

                        if (latest == null || date.isAfter(latest)) {
                                latest = date;
                        }
                }

                return latest;
        }

        private LocalDate latestBreedingDate(Pig pig) {
                return latestRecordDate(pig, "Breeding Record:");
        }

        private LocalDate latestBirthDate(Pig pig) {
                return latestRecordDate(pig, "Birth Record:");
        }

        private String latestRecordType(Pig pig) {
                if (pig == null
                                || pig.getNotes() == null
                                || pig.getNotes().isBlank()) {
                        return null;
                }

                String notes = pig.getNotes();

                int breedingIndex = notes.lastIndexOf("Breeding Record:");

                int birthIndex = notes.lastIndexOf("Birth Record:");

                if (breedingIndex < 0 && birthIndex < 0) {
                        return null;
                }

                return breedingIndex > birthIndex
                                ? "BREEDING"
                                : "BIRTH";
        }

        private record BreedingInfo(
                        LocalDate date,
                        String fatherName,
                        String fatherCode) {
        }

        private BreedingInfo latestBreedingInfo(Pig pig) {
                if (pig == null
                                || pig.getNotes() == null
                                || pig.getNotes().isBlank()) {
                        return null;
                }

                /*
                 * Breeding record note format:
                 *
                 * Breeding Record:
                 * Date: 2026-07-24
                 * Father Genetics: Yorkshire [Y]
                 */
                Pattern pattern = Pattern.compile(
                                "(?m)^Breeding Record:\\s*\\R"
                                                + "Date:\\s*(\\d{4}-\\d{2}-\\d{2})\\s*\\R"
                                                + "Father Genetics:\\s*(.*?)\\s*\\[([^\\]]+)]");

                Matcher matcher = pattern.matcher(pig.getNotes());

                BreedingInfo latest = null;

                while (matcher.find()) {
                        LocalDate date = LocalDate.parse(matcher.group(1));

                        BreedingInfo current = new BreedingInfo(
                                        date,
                                        matcher.group(2).trim(),
                                        matcher.group(3).trim());

                        if (latest == null
                                        || date.isAfter(latest.date())) {
                                latest = current;
                        }
                }

                return latest;
        }

        private Genetics latestBreedingFather(Pig pig) {
                BreedingInfo info = latestBreedingInfo(pig);

                if (info == null
                                || info.fatherCode() == null
                                || info.fatherCode().isBlank()) {
                        return null;
                }

                /*
                 * Historical breeding record မှာ ရှိခဲ့တဲ့ genetics ဖြစ်နိုင်တဲ့အတွက်
                 * findActive() မသုံးဘဲ findAll() သုံးထားပါတယ်။
                 */
                return geneticsService.findAll()
                                .stream()
                                .filter(genetics -> genetics.getCode() != null
                                                && genetics.getCode()
                                                                .equalsIgnoreCase(info.fatherCode()))
                                .findFirst()
                                .orElse(null);
        }

        /*
         * Birth Record form မှာပေါ်မယ့် sow.
         *
         * Breeding Record ရှိပြီး latest Birth Record မရှိသေးရင်
         * Birth Record form မှာပေါ်မယ်။
         */
        private boolean readyForBirth(Pig pig) {
                if (pig == null
                                || pig.getStatus() != PigStatus.BREEDING_SOW) {
                        return false;
                }

                LocalDate breedingDate = latestBreedingDate(pig);

                if (breedingDate == null) {
                        return false;
                }

                /*
                 * နောက်ဆုံး record က Breeding Record ဖြစ်ရင်
                 * Birth Record မှာ ပေါ်မယ်။
                 */
                return "BREEDING".equals(latestRecordType(pig));
        }

        /*
         * Breeding Record form မှာပေါ်မယ့် sow.
         *
         * 1. တစ်ခါမှ breeding မလုပ်ဖူးသေးရင်
         * pig.birthDate + 150 days ပြည့်မှ ပေါ်မယ်။
         *
         * 2. Breeding Record လုပ်ပြီး Birth Record မလုပ်ရသေးရင်
         * Breeding Record form မှာ မပေါ်ဘူး။
         *
         * 3. Birth Record လုပ်ပြီးသားဆိုရင်
         * Breeding Record form မှာ ချက်ချင်းပြန်ပေါ်မယ်။
         */
        private boolean availableForBreeding(Pig pig) {
                if (pig == null
                                || pig.getStatus() != PigStatus.BREEDING_SOW) {
                        return false;
                }

                LocalDate breedingDate = latestBreedingDate(pig);

                /*
                 * တစ်ခါမှ Breeding Record မရှိသေးရင်
                 * မွေးနေ့ကနေ 150 ရက်ပြည့်မှ ပေါ်မယ်။
                 */
                if (breedingDate == null) {
                        LocalDate sowBirthDate = pig.getBirthDate();

                        if (sowBirthDate == null) {
                                return false;
                        }

                        return !sowBirthDate
                                        .plusDays(150)
                                        .isAfter(LocalDate.now());
                }

                /*
                 * နောက်ဆုံး record က Birth Record ဖြစ်ရင်
                 * Breeding Record ရဲ့ sow select list ထဲမှာ ပြန်ပေါ်မယ်။
                 *
                 * Date တူတာ/မတူတာ မစစ်တော့ပါ။
                 */
                return "BIRTH".equals(latestRecordType(pig));
        }

        // =========================================================
        // DASHBOARD
        // =========================================================

        @GetMapping("/dashboard")
        public String dashboard(Principal p, Model model) {
                User staff = currentStaff(p);

                boolean attended = attendanceService.isClockedIn(staff);
                boolean clockedOut = attendanceService.isClockedOut(staff);

                model.addAttribute("staff", staff);
                model.addAttribute("attended", attended);
                model.addAttribute("clockedOut", clockedOut);
                model.addAttribute("earlyClockOut", attendanceService.requiresEarlyApproval(staff));
                model.addAttribute("clockOutPending", attendanceService.hasPendingClockOut(staff));

                boolean nightShift = false;

                var activeShift = shiftService.findActiveShift(staff);

                if (activeShift.isPresent() && activeShift.get().getShift() != null) {
                        String name = activeShift.get().getShift().getName().toLowerCase();
                        nightShift = name.contains("night");
                }

                model.addAttribute("isNightShift", nightShift);

                if (attended) {
                        model.addAttribute("tasks", taskService.getTasksForStaffToday(staff));
                } else {
                        model.addAttribute("tasks", List.of());
                }

                Building building = staff.getBuilding();

                if (building != null) {
                        model.addAttribute("building", building);
                        model.addAttribute("buildingPigs", pigService.findByBuilding(building));
                        model.addAttribute("hasBirthForm", true);
                } else {
                        model.addAttribute("building", null);
                        model.addAttribute("buildingPigs", List.of());
                        model.addAttribute("hasBirthForm", false);
                }

                return "staff/dashboard";
        }

        // =========================================================
        // CLOCK IN / CLOCK OUT
        // =========================================================

        @PostMapping("/clock-in")
        public String clockIn(Principal p, RedirectAttributes ra) {
                User staff = currentStaff(p);

                try {
                        attendanceService.clockIn(staff);

                        if (staff.getBuilding() != null) {
                                scheduleGenerationService.generateDailySchedule(staff.getBuilding());
                        }

                        ra.addFlashAttribute("success", "Clocked in successfully.");
                } catch (Exception e) {
                        ra.addFlashAttribute("error", e.getMessage());
                }

                return "redirect:/staff/dashboard";
        }

        @PostMapping("/clock-out")
        public String clockOut(
                        @RequestParam(required = false, defaultValue = "") String earlyDepartureReason,
                        @RequestParam(required = false, defaultValue = "") String reason,
                        Principal p,
                        RedirectAttributes ra) {

                User staff = currentStaff(p);

                try {
                        String finalReason = !reason.isBlank()
                                        ? reason
                                        : earlyDepartureReason;

                        Attendance attendance = attendanceService.requestClockOut(staff, finalReason);

                        ra.addFlashAttribute(
                                        "success",
                                        attendance.isClockedOut()
                                                        ? "Clocked out successfully."
                                                        : "Early clock out request sent.");
                } catch (Exception e) {
                        ra.addFlashAttribute("error", e.getMessage());
                }

                return "redirect:/staff/dashboard";
        }

        // =========================================================
        // STAFF TASKS
        // =========================================================

        @GetMapping("/tasks")
        public String myTasks(Principal p, Model model) {
                User staff = currentStaff(p);

                if (staff.getBuilding() != null) {
                        scheduleGenerationService.generateDailySchedule(staff.getBuilding());
                }

                LocalDate today = LocalDate.now();

                List<DailyTask> allTasks = taskService.getTasksForStaffNextDays(staff, 2);

                List<DailyTask> todayTasks = allTasks.stream()
                                .filter(t -> t.getTaskDate() != null)
                                .filter(t -> t.getTaskDate().isEqual(today))
                                .toList();

                List<DailyTask> nextTasks = allTasks.stream()
                                .filter(t -> t.getTaskDate() != null)
                                .filter(t -> t.getTaskDate().isAfter(today))
                                .toList();

                model.addAttribute("staff", staff);
                model.addAttribute("building", staff.getBuilding());
                model.addAttribute("clockedOut", attendanceService.isClockedOut(staff));
                model.addAttribute("tasks", todayTasks);
                model.addAttribute("todayTasks", todayTasks);
                model.addAttribute("nextTasks", nextTasks);
                model.addAttribute("upcomingTasks", allTasks);
                model.addAttribute("taskHistory", taskService.getCompletedTaskHistoryForStaff(staff));

                return "staff/tasks";
        }

        @PostMapping("/tasks/{id}/submit")
        public String submitTask(
                        @PathVariable Long id,
                        @RequestParam(required = false, defaultValue = "") String notes,
                        Principal p,
                        RedirectAttributes ra) {

                try {
                        taskService.submitTask(id, notes, currentStaff(p));
                        ra.addFlashAttribute("success", "Task submitted for supervisor review.");
                } catch (Exception e) {
                        ra.addFlashAttribute("error", e.getMessage());
                }

                return "redirect:/staff/tasks";
        }

        // =========================================================
        // STAFF PIGS
        // =========================================================

        @GetMapping("/pigs")
        public String myBuildingPigs(Principal p, Model model) {
                User staff = currentStaff(p);
                Building building = staff.getBuilding();

                model.addAttribute("staff", staff);
                model.addAttribute("building", building);

                if (building == null) {
                        model.addAttribute("pigs", List.of());
                        model.addAttribute("error", "No building assigned.");
                        return "staff/pigs";
                }

                List<Pig> pigs = pigService.findByBuilding(building);

                model.addAttribute("pigs", pigs);

                model.addAttribute(
                                "smallPigs",
                                pigs.stream()
                                                .filter(pig -> pig.getStatus() == PigStatus.PIGLET)
                                                .toList());

                model.addAttribute(
                                "mediumPigs",
                                pigs.stream()
                                                .filter(pig -> pig.getStatus() == PigStatus.GROWER)
                                                .toList());

                model.addAttribute(
                                "largePigs",
                                pigs.stream()
                                                .filter(pig -> pig.getStatus() == PigStatus.FINISHER)
                                                .toList());

                List<Pig> breedingSows = pigs.stream()
                                .filter(pig -> pig.getStatus() == PigStatus.BREEDING_SOW)
                                .toList();

                model.addAttribute("breedingSows", breedingSows);

                model.addAttribute(
                                "breedingBoars",
                                pigs.stream()
                                                .filter(pig -> pig.getStatus() == PigStatus.BREEDING_BOAR)
                                                .toList());

                model.addAttribute(
                                "matedSows",
                                breedingSows.stream()
                                                .filter(this::readyForBirth)
                                                .toList());

                model.addAttribute(
                                "unmatedSows",
                                breedingSows.stream()
                                                .filter(this::availableForBreeding)
                                                .toList());

                model.addAttribute(
                                "forSalePigs",
                                pigs.stream()
                                                .filter(pig -> pig.getStatus() == PigStatus.FOR_SALE)
                                                .toList());

                model.addAttribute(
                                "soldPigs",
                                pigs.stream()
                                                .filter(pig -> pig.getStatus() == PigStatus.SOLD)
                                                .toList());

                return "staff/pigs";
        }

        // =========================================================
        // BIRTH RECORD FORM
        // =========================================================

        @GetMapping("/birth-record")
        public String birthRecordForm(Principal p, Model model) {

                User staff = currentStaff(p);
                Building building = staff.getBuilding();

                if (building == null) {
                        model.addAttribute("error", "No building assigned.");
                        return "staff/birth-record-locked";
                }

                if (!attendanceService.isClockedIn(staff)
                                || attendanceService.isClockedOut(staff)) {

                        model.addAttribute("error", "Clock in before submitting birth record.");
                        return "staff/birth-record-locked";
                }

                List<Pig> birthReadySows = pigService.findSowsByBuilding(building)
                                .stream()
                                .filter(this::readyForBirth)
                                .toList();

                Map<Long, String> breedingFathers = new HashMap<>();
                Map<Long, String> breedingDates = new HashMap<>();

                for (Pig sow : birthReadySows) {
                        BreedingInfo info = latestBreedingInfo(sow);

                        if (info != null) {
                                breedingFathers.put(
                                                sow.getId(),
                                                info.fatherName()
                                                                + " ["
                                                                + info.fatherCode()
                                                                + "]");

                                breedingDates.put(
                                                sow.getId(),
                                                info.date().toString());
                        }
                }

                model.addAttribute("building", building);
                model.addAttribute("sows", birthReadySows);
                model.addAttribute("breedingFathers", breedingFathers);
                model.addAttribute("breedingDates", breedingDates);
                model.addAttribute("today", LocalDate.now());

                return "staff/birth-record";
        }

        // =========================================================
        // SUBMIT BIRTH RECORD
        // =========================================================

        @PostMapping("/birth-record/submit")
        public String submitBirthRecord(
                        @RequestParam Long motherId,
                        // @RequestParam Long geneticsId,
                        @RequestParam int alive,
                        @RequestParam int dead,
                        @RequestParam String birthDate,
                        @RequestParam(required = false, defaultValue = "") String notes,
                        Principal p,
                        RedirectAttributes ra) {

                User staff = currentStaff(p);

                try {
                        Building building = staff.getBuilding();

                        if (building == null) {
                                throw new IllegalStateException("No building assigned.");
                        }

                        if (!attendanceService.isClockedIn(staff) || attendanceService.isClockedOut(staff)) {
                                throw new IllegalStateException("Clock in before submitting birth record.");
                        }

                        Pig mother = pigService.findById(motherId)
                                        .orElseThrow(() -> new IllegalArgumentException("Mother sow not found."));

                        if (mother.getBuilding() == null
                                        || !mother.getBuilding().getId().equals(building.getId())) {
                                throw new IllegalArgumentException("Mother sow does not belong to this building.");
                        }

                        if (mother.getStatus() != PigStatus.BREEDING_SOW) {
                                throw new IllegalArgumentException("Selected pig is not a breeding sow.");
                        }

                        if (!readyForBirth(mother)) {
                                throw new IllegalStateException("This sow is not ready for birth record.");
                        }

                        // Genetics father = geneticsService.findById(geneticsId).orElseThrow(() -> new
                        // IllegalArgumentException("Genetics not found."));
                        Genetics father = latestBreedingFather(mother);

                        if (father == null) {
                                throw new IllegalStateException(
                                                "Father genetics was not found in the latest breeding record.");
                        }

                        LocalDate date = LocalDate.parse(birthDate);

                        if (date.isAfter(LocalDate.now())) {
                                throw new IllegalArgumentException("Birth date cannot be future date.");
                        }

                        LocalDate latestBreedingDate = latestBreedingDate(mother);

                        if (latestBreedingDate != null && date.isBefore(latestBreedingDate)) {
                                throw new IllegalArgumentException("Birth date cannot be before breeding date.");
                        }

                        if (alive < 0 || dead < 0) {
                                throw new IllegalArgumentException("Piglet count cannot be negative.");
                        }

                        if (alive + dead <= 0) {
                                throw new IllegalArgumentException("At least one piglet required.");
                        }

                        int totalPiglets = alive + dead;

                        List<Pig> piglets = pigService.recordLitter(
                                        building.getFarm(),
                                        building,
                                        father,
                                        mother,
                                        date,
                                        totalPiglets,
                                        alive,
                                        staff);

                        ra.addFlashAttribute(
                                        "success",
                                        "Birth record saved successfully. "
                                                        + piglets.size()
                                                        + " piglets generated. Sow is available for breeding again.");

                        return "redirect:/staff/dashboard";

                } catch (Exception e) {
                        ra.addFlashAttribute("error", e.getMessage());
                        return "redirect:/staff/birth-record";
                }
        }

        // =========================================================
        // BREEDING RECORD FORM
        // =========================================================

        @GetMapping("/breeding-record")
        public String breedingRecordForm(Principal p, Model model) {

                User staff = currentStaff(p);
                Building building = staff.getBuilding();

                if (building == null) {
                        model.addAttribute("error", "No building assigned.");
                        return "staff/breeding-record";
                }

                List<Pig> availableSows = pigService.findSowsByBuilding(building)
                                .stream()
                                .filter(this::availableForBreeding) // <-- Unmated
                                .toList();

                model.addAttribute("building", building);
                model.addAttribute("sows", availableSows);
                model.addAttribute("genetics", geneticsService.findActive());
                model.addAttribute("today", LocalDate.now());

                return "staff/breeding-record";
        }

        // =========================================================
        // SUBMIT BREEDING RECORD
        // =========================================================

        @PostMapping("/breeding-record")
        public String submitBreedingRecord(
                        @RequestParam Long sowId,
                        @RequestParam Long geneticsId,
                        @RequestParam String breedingDate,
                        @RequestParam(required = false, defaultValue = "") String notes,
                        Principal p,
                        RedirectAttributes ra) {

                User staff = currentStaff(p);

                try {
                        Building building = staff.getBuilding();

                        if (building == null) {
                                throw new IllegalStateException("No building assigned.");
                        }

                        Pig sow = pigService.findById(sowId)
                                        .orElseThrow(() -> new IllegalArgumentException("Sow not found."));

                        if (sow.getBuilding() == null
                                        || !sow.getBuilding().getId().equals(building.getId())) {
                                throw new IllegalArgumentException("Invalid sow building.");
                        }

                        if (sow.getStatus() != PigStatus.BREEDING_SOW) {
                                throw new IllegalArgumentException("Pig is not breeding sow.");
                        }

                        if (!availableForBreeding(sow)) {
                                LocalDate breedingDateRecord = latestBreedingDate(sow);
                                LocalDate birthDateRecord = latestBirthDate(sow);

                                if (breedingDateRecord == null) {
                                        LocalDate sowBirthDate = sow.getBirthDate();

                                        throw new IllegalStateException(
                                                        "This sow is not old enough for breeding yet. Available after "
                                                                        + (sowBirthDate == null
                                                                                        ? "150 days old"
                                                                                        : sowBirthDate.plusDays(150))
                                                                        + ".");
                                }

                                if (birthDateRecord == null || birthDateRecord.isBefore(breedingDateRecord)) {
                                        throw new IllegalStateException(
                                                        "This sow already has breeding record. Submit birth record first.");
                                }

                                throw new IllegalStateException("This sow is not available for breeding.");
                        }

                        Genetics genetics = geneticsService.findById(geneticsId)
                                        .orElseThrow(() -> new IllegalArgumentException("Genetics not found."));

                        LocalDate date = LocalDate.parse(breedingDate);

                        if (date.isAfter(LocalDate.now())) {
                                throw new IllegalArgumentException("Breeding date cannot be future.");
                        }

                        LocalDate latestBirthDate = latestBirthDate(sow);

                        if (latestBirthDate == null && sow.getBirthDate() != null) {
                                LocalDate firstAvailableDate = sow.getBirthDate().plusDays(150);

                                if (date.isBefore(firstAvailableDate)) {
                                        throw new IllegalArgumentException(
                                                        "Breeding date must be after sow is 150 days old. Available after "
                                                                        + firstAvailableDate
                                                                        + ".");
                                }
                        }

                        String oldNotes = sow.getNotes() == null ? "" : sow.getNotes();

                        String breedingNote = "\n\nBreeding Record:"
                                        + "\nDate: " + date
                                        + "\nFather Genetics: " + genetics.getName() + " [" + genetics.getCode() + "]"
                                        + "\nRecorded By: " + staff.getFullName()
                                        + (notes == null || notes.isBlank() ? "" : "\nNotes: " + notes);

                        sow.setStatus(PigStatus.BREEDING_SOW);
                        sow.setNotes(oldNotes + breedingNote);
                        sow.setRecordedDate(LocalDate.now());

                        pigService.save(sow);

                        ra.addFlashAttribute(
                                        "success",
                                        "Breeding record saved successfully. Sow moved to birth record.");

                        return "redirect:/staff/dashboard";

                } catch (Exception e) {
                        ra.addFlashAttribute("error", e.getMessage());
                        return "redirect:/staff/breeding-record";
                }
        }
}