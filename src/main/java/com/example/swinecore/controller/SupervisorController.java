package com.example.swinecore.controller;

import com.example.swinecore.entity.Attendance;
import com.example.swinecore.entity.Building;
import com.example.swinecore.entity.FeedShipment;
import com.example.swinecore.entity.FinanceTransaction;
import com.example.swinecore.entity.Genetics;
import com.example.swinecore.entity.Pig;
import com.example.swinecore.entity.User;
import com.example.swinecore.entity.enums.PigGender;
import com.example.swinecore.entity.enums.PigStatus;
import com.example.swinecore.repository.FeedShipmentRepository;
import com.example.swinecore.repository.FinanceTransactionRepository;
import com.example.swinecore.service.AttendanceService;
import com.example.swinecore.service.BuildingService;
import com.example.swinecore.service.FeedShipmentService;
import com.example.swinecore.service.GeneticsService;
import com.example.swinecore.service.PigService;
import com.example.swinecore.service.TaskService;
import com.example.swinecore.service.UserService;
import com.example.swinecore.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/supervisor")
@PreAuthorize("hasRole('SUPERVISOR')")
@RequiredArgsConstructor
public class SupervisorController {

    private final UserService userService;
    private final TaskService taskService;
    private final PigService pigService;
    private final BuildingService buildingService;
    private final AttendanceService attendanceService;
    private final FeedShipmentService feedShipmentService;
    private final FeedShipmentRepository feedShipmentRepository;
    private final FinanceTransactionRepository financeTransactionRepository;
    private final GeneticsService geneticsService;
    private final FileUploadUtil fileUploadUtil;

    private User currentSupervisor(Principal p) {
        return userService.findByEmail(p.getName()).orElseThrow();
    }

    // ---- Dashboard ----

    @GetMapping("/dashboard")
    public String dashboard(Principal p, Model model) {
        User supervisor = currentSupervisor(p);
        Building building = supervisor.getBuilding();

        model.addAttribute("supervisor", supervisor);
        model.addAttribute("building", building);

        boolean attended = attendanceService.isClockedIn(supervisor);
        boolean clockedOut = attendanceService.isClockedOut(supervisor);

        model.addAttribute("attended", attended);
        model.addAttribute("clockedOut", clockedOut);
        model.addAttribute("clockOutPending", attendanceService.hasPendingClockOut(supervisor));
        model.addAttribute("earlyClockOut", attendanceService.requiresEarlyApproval(supervisor));

        if (building != null) {
            model.addAttribute("pendingTasks", taskService.getPendingReviewForBuilding(building));
            model.addAttribute("staff", userService.findByBuilding(building));
        } else {
            model.addAttribute("pendingTasks", List.of());
            model.addAttribute("staff", List.of());
        }

        return "supervisor/dashboard";
    }

    // ---- Attendance Clock ----

    @PostMapping("/clock-in")
    public String clockIn(Principal p, RedirectAttributes ra) {
        User supervisor = currentSupervisor(p);

        try {
            attendanceService.clockIn(supervisor);
            ra.addFlashAttribute("success", "Clocked in successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/supervisor/dashboard";
    }

    @PostMapping("/clock-out")
    public String clockOut(
            @RequestParam(required = false, name = "reason", defaultValue = "") String earlyDepartureReason,
            Principal p,
            RedirectAttributes ra) {

        User supervisor = currentSupervisor(p);

        try {
            Attendance att = attendanceService.requestClockOut(supervisor, earlyDepartureReason);

            ra.addFlashAttribute("success", att.isClockedOut()
                    ? "Clocked out successfully."
                    : "Early clock-out request sent to your farm manager. You remain clocked in until approval.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/supervisor/dashboard";
    }

    // ---- Task Review ----

    @GetMapping("/tasks")
    public String tasks(
            Principal p,
            @RequestParam(required = false) String date,
            Model model) {

        User supervisor = currentSupervisor(p);
        Building building = supervisor.getBuilding();

        LocalDate d = date != null && !date.isBlank()
                ? LocalDate.parse(date)
                : LocalDate.now();

        if (building == null) {
            model.addAttribute("tasks", List.of());
            model.addAttribute("selectedDate", d);
            model.addAttribute("error", "No building assigned.");
            return "supervisor/tasks";
        }

        model.addAttribute("tasks", taskService.getTasksByBuilding(building, d));
        model.addAttribute("selectedDate", d);

        return "supervisor/tasks";
    }

    @PostMapping("/tasks/{id}/approve")
    public String approveTask(
            @PathVariable Long id,
            Principal p,
            RedirectAttributes ra) {

        try {
            taskService.approveTask(id, currentSupervisor(p));
            ra.addFlashAttribute("success", "Task approved.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/supervisor/tasks";
    }

    @PostMapping("/tasks/{id}/reject")
    public String rejectTask(
            @PathVariable Long id,
            @RequestParam String comments,
            Principal p,
            RedirectAttributes ra) {

        try {
            taskService.rejectTask(id, comments, currentSupervisor(p));
            ra.addFlashAttribute("success", "Task rejected with comments.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/supervisor/tasks";
    }

    // ---- Pig Ready For Sell Request ----

    @GetMapping("/pigs")
    public String listPigs(Principal p, Model model) {
        Building building = currentSupervisor(p).getBuilding();

        model.addAttribute("building", building);

        if (building == null) {
            model.addAttribute("pigs", List.of());
            model.addAttribute("genetics", geneticsService.findActive());
            model.addAttribute("pigStatuses", List.of(
                    PigStatus.PIGLET,
                    PigStatus.GROWER,
                    PigStatus.FINISHER,
                    PigStatus.BREEDING_SOW,
                    PigStatus.BREEDING_BOAR));
            model.addAttribute("error", "No building assigned.");
            return "supervisor/pigs";
        }

        model.addAttribute("pigs", pigService.findByBuilding(building));
        model.addAttribute("genetics", geneticsService.findActive());
        model.addAttribute("pigStatuses", List.of(
                PigStatus.PIGLET,
                PigStatus.GROWER,
                PigStatus.FINISHER,
                PigStatus.BREEDING_SOW,
                PigStatus.BREEDING_BOAR));

        return "supervisor/pigs";
    }

    @PostMapping("/pigs/purchased")
    public String registerPurchasedPig(
            @RequestParam String code,
            @RequestParam Long geneticsId,
            @RequestParam PigGender gender,
            @RequestParam PigStatus status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam Double currentWeight,
            @RequestParam(required = false) String notes,
            Principal p,
            RedirectAttributes ra) {

        Building building = currentSupervisor(p).getBuilding();

        try {
            if (building == null) {
                throw new IllegalStateException("No building is assigned to this supervisor.");
            }

            if (status == PigStatus.PENDING_SALE_APPROVAL
                    || status == PigStatus.FOR_SALE
                    || status == PigStatus.SOLD) {
                throw new IllegalArgumentException("Sale status cannot be selected from Add Purchased Pig form.");
            }

            Genetics genetics = geneticsService.findById(geneticsId).orElseThrow(
                    () -> new IllegalArgumentException("Selected genetics was not found."));

            Pig pig = pigService.registerPurchasedPig(
                    code,
                    building.getFarm(),
                    building,
                    genetics,
                    gender,
                    status,
                    birthDate,
                    currentWeight,
                    notes,
                    null);

            ra.addFlashAttribute("success", "Purchased pig " + pig.getCode() + " was registered.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/supervisor/pigs";
    }

    @PostMapping("/pigs/{id}/list-for-sale")
    public String requestReadyForSale(
            @PathVariable Long id,
            @RequestParam(required = false) Double salePrice,
            @RequestParam(required = false) Double weight,
            @RequestParam(required = false, name = "condition") String physicalCondition,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) MultipartFile pigImage,
            Principal p,
            RedirectAttributes ra) {

        User supervisor = currentSupervisor(p);

        try {
            if (supervisor.getBuilding() == null) {
                throw new IllegalStateException("No building assigned.");
            }

            if (salePrice == null || salePrice <= 0) {
                throw new IllegalArgumentException("Sale price is required.");
            }

            if (weight == null || weight <= 0) {
                throw new IllegalArgumentException("Weight is required.");
            }

            Pig pig = pigService.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Pig was not found."));

            if (pig.getBuilding() == null
                    || !pig.getBuilding().getId().equals(supervisor.getBuilding().getId())) {
                throw new SecurityException("This pig does not belong to your building.");
            }

            if (pig.getStatus() == PigStatus.SOLD) {
                throw new IllegalStateException("Sold pig cannot be requested for sale.");
            }

            if (pig.getStatus() == PigStatus.FOR_SALE || pig.isListedForSale()) {
                throw new IllegalStateException("This pig is already approved for marketplace.");
            }

            if (pig.getStatus() == PigStatus.PENDING_SALE_APPROVAL) {
                throw new IllegalStateException("This pig is already waiting for manager approval.");
            }

            if (pigImage != null && !pigImage.isEmpty()) {
                String photoPath = fileUploadUtil.saveFile(pigImage, "pigs");
                pig.setPhotoPath(photoPath);
            }

            StringBuilder saleNotes = new StringBuilder();

            if (pig.getNotes() != null && !pig.getNotes().isBlank()) {
                saleNotes.append(pig.getNotes().trim()).append("\n\n");
            }

            saleNotes.append("Ready for Sell Request:");
            saleNotes.append("\nWeight: ").append(weight).append(" kg");

            if (physicalCondition != null && !physicalCondition.isBlank()) {
                saleNotes.append("\nPhysical Condition: ").append(physicalCondition.trim());
            }

            if (notes != null && !notes.isBlank()) {
                saleNotes.append("\nSupervisor Notes: ").append(notes.trim());
            }

            pig.setCurrentWeight(weight);
            pig.setNotes(saleNotes.toString());

            /*
             * Supervisor does NOT publish directly to marketplace.
             * Supervisor only sends Ready for Sell request to manager.
             */
            pig.setStatus(PigStatus.PENDING_SALE_APPROVAL);
            pig.setListedForSale(false);
            pig.setSalePrice(salePrice);
            pig.setListedForSaleDate(null);

            pigService.save(pig);

            ra.addFlashAttribute("success", "Ready for sell request sent to manager.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/supervisor/pigs";
    }

    // ---- Feed Shipments ----

    @GetMapping("/shipments")
    public String shipments(Principal p, Model model) {
        Building building = currentSupervisor(p).getBuilding();

        model.addAttribute("building", building);

        if (building == null) {
            model.addAttribute("shipments", List.of());
            model.addAttribute("error", "No building assigned.");
            return "supervisor/shipments";
        }

        model.addAttribute("shipments", feedShipmentService.findByFarm(building.getFarm()).stream()
                .filter(s -> s.getTargetBuildingId() == null || s.getTargetBuildingId().equals(building.getId()))
                .toList());

        return "supervisor/shipments";
    }

    /**
     * Old compare endpoint.
     * Keep this for older shipment.html forms.
     */
    @PostMapping("/shipments/{id}/verify")
    public String verifyShipment(
            @PathVariable Long id,
            Principal p,
            @RequestParam String receivedFeedType,
            @RequestParam double receivedQuantityKg,
            RedirectAttributes ra) {

        User supervisor = currentSupervisor(p);

        try {
            if (supervisor.getBuilding() == null) {
                throw new IllegalStateException("No building assigned.");
            }

            FeedShipment shipment = feedShipmentService.supervisorVerify(
                    id,
                    supervisor.getBuilding(),
                    receivedFeedType,
                    receivedQuantityKg);

            shipment.setReceivedBy(supervisor);
            feedShipmentService.save(shipment);

            if (shipment.isVerified()) {
                releaseFeedExpenseIfNeeded(shipment);
                ra.addFlashAttribute("success", "Reports match. Expense released to factory.");
            } else {
                ra.addFlashAttribute("error", "Report mismatch. Sent to the manager for review.");
            }

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/supervisor/shipments";
    }

    /**
     * New supervisor confirm endpoint.
     * Confirm = expense released.
     */
    @PostMapping("/shipments/{id}/confirm")
    public String confirmFactoryShipment(
            @PathVariable Long id,
            Principal p,
            RedirectAttributes ra) {

        User supervisor = currentSupervisor(p);

        try {
            if (supervisor.getBuilding() == null) {
                throw new IllegalStateException("No building assigned.");
            }

            FeedShipment shipment = feedShipmentRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Shipment report was not found."));

            validateShipmentBelongsToSupervisorBuilding(shipment, supervisor.getBuilding());

            if (shipment.getOverrideStatus() != FeedShipment.OverrideStatus.PENDING_VERIFICATION) {
                throw new IllegalStateException("Only pending factory reports can be confirmed.");
            }

            shipment.setReceivedBy(supervisor);
            shipment.setQuantityKg(shipment.getDispatchQuantityKg());
            shipment.setOverrideStatus(FeedShipment.OverrideStatus.AUTO_APPROVED);
            shipment.setManagerOverrideReason(null);

            feedShipmentRepository.save(shipment);

            releaseFeedExpenseIfNeeded(shipment);

            ra.addFlashAttribute("success", "Factory feed report confirmed. Expense released.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/supervisor/shipments";
    }

    /**
     * New supervisor reject endpoint.
     * Reject = no expense, goes to manager review.
     */
    @PostMapping("/shipments/{id}/reject")
    public String rejectFactoryShipment(
            @PathVariable Long id,
            @RequestParam String reason,
            Principal p,
            RedirectAttributes ra) {

        User supervisor = currentSupervisor(p);

        try {
            if (supervisor.getBuilding() == null) {
                throw new IllegalStateException("No building assigned.");
            }

            if (reason == null || reason.isBlank()) {
                throw new IllegalArgumentException("Reject reason is required.");
            }

            FeedShipment shipment = feedShipmentRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Shipment report was not found."));

            validateShipmentBelongsToSupervisorBuilding(shipment, supervisor.getBuilding());

            if (shipment.getOverrideStatus() != FeedShipment.OverrideStatus.PENDING_VERIFICATION) {
                throw new IllegalStateException("Only pending factory reports can be rejected.");
            }

            shipment.setReceivedBy(supervisor);
            shipment.setOverrideStatus(FeedShipment.OverrideStatus.PENDING_MANAGER_OVERRIDE);
            shipment.setManagerOverrideReason(reason.trim());

            feedShipmentRepository.save(shipment);

            ra.addFlashAttribute("success", "Factory feed report rejected. Sent to manager review.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/supervisor/shipments";
    }

    private void validateShipmentBelongsToSupervisorBuilding(FeedShipment shipment, Building building) {
        if (shipment.getFarm() == null || building.getFarm() == null) {
            throw new IllegalArgumentException("Invalid shipment farm.");
        }

        if (!shipment.getFarm().getId().equals(building.getFarm().getId())) {
            throw new IllegalArgumentException("You can manage only your farm shipments.");
        }

        if (shipment.getTargetBuildingId() != null && !shipment.getTargetBuildingId().equals(building.getId())) {
            throw new IllegalArgumentException("This shipment does not belong to your building.");
        }
    }

    private void releaseFeedExpenseIfNeeded(FeedShipment shipment) {
        financeTransactionRepository.save(FinanceTransaction.builder()
                .farm(shipment.getFarm())
                .type("EXPENSE")
                .category("FEED")
                .amount(shipment.getTotalAmount())
                .referenceId(shipment.getInvoiceNumber())
                .description("Factory feed shipment expense released. Invoice: "
                        + shipment.getInvoiceNumber())
                .build());
    }

    // ---- Staff Attendance View ----

    @GetMapping("/attendance")
    public String attendance(Principal p, Model model) {
        Building building = currentSupervisor(p).getBuilding();

        if (building == null) {
            model.addAttribute("staff", List.of());
            model.addAttribute("attendedStaff", List.of());
            model.addAttribute("pendingClockOuts", List.of());
            model.addAttribute("error", "No building assigned.");
            return "supervisor/attendance";
        }

        List<User> staff = userService.findByBuilding(building);

        model.addAttribute("staff", staff);
        model.addAttribute("attendedStaff",
                attendanceService.getAttendedStaffForBuilding(building.getId()));
        model.addAttribute("pendingClockOuts", attendanceService.pendingForSupervisor(currentSupervisor(p)));

        return "supervisor/attendance";
    }

    @PostMapping("/attendance/{id}/approve-clock-out")
    public String approveStaffClockOut(
            @PathVariable Long id,
            Principal p,
            RedirectAttributes ra) {

        try {
            attendanceService.approveClockOut(id, currentSupervisor(p));
            ra.addFlashAttribute("success", "Staff early clock-out approved.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/supervisor/attendance";
    }
}