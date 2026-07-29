package com.example.swinecore.controller;

import com.example.swinecore.entity.*;
import com.example.swinecore.entity.enums.Role;
import com.example.swinecore.entity.enums.PigGender;
import com.example.swinecore.entity.enums.PigStatus;
import com.example.swinecore.service.*;
import com.example.swinecore.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/manager")
@PreAuthorize("hasRole('MANAGER')")
@RequiredArgsConstructor
public class ManagerController {

    private final UserService userService;
    private final FarmService farmService;
    private final BuildingService buildingService;
    private final PigService pigService;
    private final RuleScheduleService ruleService;
    private final InventoryService inventoryService;
    private final FeedShipmentService feedShipmentService;
    private final AnalyticsService analyticsService;
    private final TaskService taskService;
    private final GeneticsService geneticsService;
    private final ShiftService shiftService;
    private final AttendanceService attendanceService;
    private final FileUploadUtil fileUploadUtil;

    private User currentManager(Principal p) {
        return userService.findByEmail(p.getName()).orElseThrow();
    }

    private Farm managerFarm(User manager) {
        if (manager == null) {
            return null;
        }

        if (manager.getFarm() != null) {
            return manager.getFarm();
        }

        if (manager.getBuilding() != null && manager.getBuilding().getFarm() != null) {
            return manager.getBuilding().getFarm();
        }

        return null;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal p, Model model) {
        User manager = currentManager(p);
        Farm farm = managerFarm(manager);

        model.addAttribute("farm", farm);
        model.addAttribute("manager", manager);

        if (farm != null) {
            model.addAttribute("buildings", buildingService.findByFarm(farm));
            model.addAttribute("pigs", pigService.findByFarm(farm));
            model.addAttribute("alerts", inventoryService.getAlertsForFarm(farm));
            model.addAttribute("financials", analyticsService.getMonthlyFinancials(farm));
        } else {
            model.addAttribute("buildings", List.of());
            model.addAttribute("pigs", List.of());
            model.addAttribute("alerts", List.of());
            model.addAttribute("financials", List.of());
        }

        return "manager/dashboard";
    }

    // =========================================================
    // Buildings
    // =========================================================

    @GetMapping("/buildings")
    public String listBuildings(Principal p, Model model) {
        User manager = currentManager(p);
        Farm farm = managerFarm(manager);

        model.addAttribute("farm", farm);
        model.addAttribute("buildings", farm == null ? List.of() : buildingService.findByFarm(farm));

        return "manager/buildings";
    }

    @GetMapping("/buildings/{id}")
    public String viewBuilding(
            @PathVariable Long id,
            Principal p,
            Model model,
            RedirectAttributes ra) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null) {
                throw new IllegalStateException("Manager is not assigned to a farm.");
            }

            Building building = buildingService.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Building was not found."));

            if (building.getFarm() == null || !building.getFarm().getId().equals(farm.getId())) {
                throw new SecurityException("This building is outside your farm.");
            }

            model.addAttribute("farm", farm);
            model.addAttribute("building", building);
            model.addAttribute("pigs", pigService.findByBuilding(building));
            model.addAttribute("staff", building.getStaff() == null ? List.of() : building.getStaff());

            return "manager/building-detail";

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/manager/buildings";
        }
    }

    @PostMapping("/buildings/create")
    public String createBuilding(
            Principal p,
            @RequestParam String name,
            RedirectAttributes ra) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null) {
                throw new IllegalStateException("Manager is not assigned to a farm.");
            }

            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Building name is required.");
            }

            Building building = new Building();
            building.setName(name.trim());
            building.setFarm(farm);

            buildingService.create(building);

            ra.addFlashAttribute("success", "Building created.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/buildings";
    }

    @PostMapping("/buildings/{id}/update")
    public String updateBuilding(
            @PathVariable Long id,
            @RequestParam String name,
            Principal p,
            RedirectAttributes ra) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null) {
                throw new IllegalStateException("Manager is not assigned to a farm.");
            }

            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Building name is required.");
            }

            Building building = buildingService.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Building was not found."));

            if (building.getFarm() == null || !building.getFarm().getId().equals(farm.getId())) {
                throw new SecurityException("This building is outside your farm.");
            }

            building.setName(name.trim());
            buildingService.save(building);

            ra.addFlashAttribute("success", "Building updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/buildings";
    }

    @PostMapping("/buildings/{id}/delete")
    public String deleteBuilding(
            @PathVariable Long id,
            @RequestParam String confirmName,
            Principal p,
            RedirectAttributes ra) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null) {
                throw new IllegalStateException("Manager is not assigned to a farm.");
            }

            Building building = buildingService.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Building was not found."));

            if (building.getFarm() == null || !building.getFarm().getId().equals(farm.getId())) {
                throw new SecurityException("This building is outside your farm.");
            }

            if (confirmName == null || !confirmName.trim().equals(building.getName())) {
                throw new IllegalArgumentException("Building name confirmation does not match.");
            }

            if (building.getPigs() != null && !building.getPigs().isEmpty()) {
                throw new IllegalStateException("Cannot delete this building because it still has pigs.");
            }

            if (building.getStaff() != null && !building.getStaff().isEmpty()) {
                throw new IllegalStateException("Cannot delete this building because it still has assigned staff.");
            }

            if (building.getCurrentSupervisor() != null) {
                throw new IllegalStateException(
                        "Cannot delete this building because it still has an assigned supervisor.");
            }

            buildingService.delete(id);

            ra.addFlashAttribute("success", "Building deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/buildings";
    }

    @PostMapping("/buildings/{id}/assign-supervisor")
    public String assignSupervisor(
            @PathVariable Long id,
            @RequestParam Long userId,
            Principal p,
            RedirectAttributes ra) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null) {
                throw new IllegalStateException("Manager is not assigned to a farm.");
            }

            Building building = buildingService.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Building was not found."));

            if (building.getFarm() == null || !building.getFarm().getId().equals(farm.getId())) {
                throw new SecurityException("This building is outside your farm.");
            }

            User supervisor = userService.findById(userId).orElseThrow(
                    () -> new IllegalArgumentException("Supervisor was not found."));

            if (supervisor.getRole() != Role.SUPERVISOR) {
                throw new IllegalArgumentException("Selected user is not a supervisor.");
            }

            userService.assignToBuilding(userId, building);

            ra.addFlashAttribute("success", "Supervisor assigned.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/buildings";
    }

    // =========================================================
    // Staff
    // =========================================================

    @GetMapping("/staff")
    public String listStaff(Principal p, Model model) {
        User manager = currentManager(p);
        Farm farm = managerFarm(manager);

        model.addAttribute("farm", farm);
        model.addAttribute("staff", farm == null ? List.of() : userService.findByFarmAndRole(farm, Role.STAFF));
        model.addAttribute("supervisors",
                farm == null ? List.of() : userService.findByFarmAndRole(farm, Role.SUPERVISOR));

        return "manager/staff";
    }

    // =========================================================
    // Rules + Daily Tasks
    // =========================================================

    @GetMapping("/rules")
    public String listRules(Principal p, Model model) {
        User manager = currentManager(p);
        Farm farm = managerFarm(manager);

        model.addAttribute("manager", manager);
        model.addAttribute("farm", farm);
        model.addAttribute("rules", farm == null ? List.of() : ruleService.findByFarm(farm));
        model.addAttribute("semenPrice", farm == null ? 0 : ruleService.getSemenPrice(farm));

        return "manager/rules";
    }

    @GetMapping("/rules/daily-tasks")
    public String dailyTasks(Principal p, Model model) {
        User manager = currentManager(p);
        Farm farm = managerFarm(manager);
        LocalDate today = LocalDate.now();

        model.addAttribute("manager", manager);
        model.addAttribute("farm", farm);
        model.addAttribute("today", today);

        if (farm == null) {
            model.addAttribute("upcomingTasks", List.of());
            return "manager/dailyTask";
        }

        List<DailyTask> upcomingTasks = taskService.getTasksForFarmBetweenDates(
                farm,
                today,
                today.plusDays(2));

        model.addAttribute("upcomingTasks", upcomingTasks);

        return "manager/dailyTask";
    }

    @PostMapping("/rules/semen-price")
    public String updateSemenPrice(
            Principal p,
            @RequestParam double semenPrice,
            RedirectAttributes ra) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null) {
                throw new IllegalStateException("Manager is not assigned to a farm.");
            }

            ruleService.setSemenPrice(farm, manager, semenPrice);
            ra.addFlashAttribute("success", "Semen price updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/rules";
    }

    @GetMapping("/rules/new")
    public String newRuleForm(Model model) {
        model.addAttribute("rule", new RuleSchedule());
        model.addAttribute("ruleTypes", com.example.swinecore.entity.enums.RuleType.values());

        return "manager/rule-form";
    }

    @PostMapping("/rules/create")
    public String createRule(
            Principal p,
            @ModelAttribute RuleSchedule rule,
            RedirectAttributes ra) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null) {
                throw new IllegalStateException("Manager is not assigned to a farm.");
            }

            prepareSupportedRule(rule);

            rule.setId(null);
            rule.setFarm(farm);
            rule.setCreatedBy(manager);
            rule.setActive(true);

            ruleService.save(rule);
            buildingService.findByFarm(farm).forEach(inventoryService::refreshBuilding);

            ra.addFlashAttribute("success", "Rule created.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/rules";
    }

    @PostMapping("/rules/update")
    public String updateRule(
            Principal p,
            @ModelAttribute RuleSchedule rule,
            RedirectAttributes ra) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null) {
                throw new IllegalStateException("Manager is not assigned to a farm.");
            }

            RuleSchedule existing = ruleService.findById(rule.getId()).orElseThrow();

            existing.setName(rule.getName());
            existing.setRuleType(rule.getRuleType());
            existing.setAppliesTo(rule.getAppliesTo());
            existing.setActive(true);
            existing.setDayFromBirth(rule.getDayFromBirth());
            existing.setDayRangeStart(rule.getDayRangeStart());
            existing.setDayRangeEnd(rule.getDayRangeEnd());
            existing.setMedication(rule.getMedication());
            existing.setDosage(rule.getDosage());
            existing.setFeedType(rule.getFeedType());
            existing.setFeedAmountKg(rule.getFeedAmountKg());
            existing.setFixedTargetDay(rule.getFixedTargetDay());
            existing.setAdministrationRoute(rule.getAdministrationRoute());
            existing.setBreedingMonth(rule.getBreedingMonth());
            existing.setDescription(rule.getDescription());

            prepareSupportedRule(existing);

            ruleService.save(existing);
            buildingService.findByFarm(farm).forEach(inventoryService::refreshBuilding);

            ra.addFlashAttribute("success", "Rule updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/rules";
    }

    private void prepareSupportedRule(RuleSchedule rule) {
        if (rule.getRuleType() == null ||
                (rule.getRuleType() != com.example.swinecore.entity.enums.RuleType.VACCINATION &&
                        rule.getRuleType() != com.example.swinecore.entity.enums.RuleType.MEDICATION &&
                        rule.getRuleType() != com.example.swinecore.entity.enums.RuleType.FEEDING)) {
            throw new IllegalArgumentException("Please choose Vaccine, Medicine or Feed rule type.");
        }

        if (rule.getRuleType() == com.example.swinecore.entity.enums.RuleType.FEEDING) {
            rule.setMedication(null);
            rule.setDosage(null);
            rule.setAdministrationRoute(null);
        } else {
            rule.setFeedType(null);
            rule.setFeedAmountKg(null);
            rule.setDayRangeStart(null);
            rule.setDayRangeEnd(null);
        }

        rule.setBreedingMonth(null);
    }

    @PostMapping("/rules/{id}/delete")
    public String deleteRule(
            @PathVariable Long id,
            RedirectAttributes ra) {

        try {
            Farm farm = ruleService.findById(id).orElseThrow().getFarm();

            ruleService.delete(id);

            if (farm != null) {
                buildingService.findByFarm(farm).forEach(inventoryService::refreshBuilding);
            }

            ra.addFlashAttribute("success", "Rule deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/rules";
    }

    // =========================================================
    // Pigs
    // =========================================================

    @GetMapping("/pigs")
    public String listPigs(Principal p, Model model) {
        User manager = currentManager(p);
        Farm farm = managerFarm(manager);

        if (farm == null) {
            model.addAttribute("farm", null);
            model.addAttribute("pigs", List.of());

            model.addAttribute("smallPigs", List.of());
            model.addAttribute("mediumPigs", List.of());
            model.addAttribute("largePigs", List.of());

            model.addAttribute("breedingStock", List.of());
            model.addAttribute("breedingBoars", List.of());
            model.addAttribute("breedingSows", List.of());
            model.addAttribute("matedSows", List.of());
            model.addAttribute("unmatedSows", List.of());

            model.addAttribute("pendingSalePigs", List.of());
            model.addAttribute("forSalePigs", List.of());
            model.addAttribute("soldPigs", List.of());

            model.addAttribute("buildings", List.of());
            model.addAttribute("genetics", geneticsService.findActive());
            model.addAttribute("pigStatuses", pigStatuses());

            model.addAttribute("error", "Manager is not assigned to a farm.");
            return "manager/pigs";
        }

        List<Pig> pigs = pigService.findByFarm(farm);

        List<Pig> smallPigs = pigs.stream()
                .filter(pig -> pig.getStatus() == PigStatus.PIGLET
                        || (pig.getStatus() != PigStatus.BREEDING_SOW
                                && pig.getStatus() != PigStatus.BREEDING_BOAR
                                && pig.getStatus() != PigStatus.PENDING_SALE_APPROVAL
                                && pig.getStatus() != PigStatus.FOR_SALE
                                && pig.getStatus() != PigStatus.SOLD
                                && pig.getAgeInDays() >= 0
                                && pig.getAgeInDays() < 60))
                .toList();

        List<Pig> mediumPigs = pigs.stream()
                .filter(pig -> pig.getStatus() == PigStatus.GROWER
                        || (pig.getStatus() != PigStatus.BREEDING_SOW
                                && pig.getStatus() != PigStatus.BREEDING_BOAR
                                && pig.getStatus() != PigStatus.PENDING_SALE_APPROVAL
                                && pig.getStatus() != PigStatus.FOR_SALE
                                && pig.getStatus() != PigStatus.SOLD
                                && pig.getAgeInDays() >= 60
                                && pig.getAgeInDays() < 120))
                .toList();

        List<Pig> largePigs = pigs.stream()
                .filter(pig -> pig.getStatus() == PigStatus.FINISHER
                        || (pig.getStatus() != PigStatus.BREEDING_SOW
                                && pig.getStatus() != PigStatus.BREEDING_BOAR
                                && pig.getStatus() != PigStatus.PENDING_SALE_APPROVAL
                                && pig.getStatus() != PigStatus.FOR_SALE
                                && pig.getStatus() != PigStatus.SOLD
                                && pig.getAgeInDays() >= 120))
                .toList();

        List<Pig> breedingStock = pigs.stream()
                .filter(pig -> pig.getStatus() == PigStatus.BREEDING_SOW
                        || pig.getStatus() == PigStatus.BREEDING_BOAR)
                .toList();

        List<Pig> breedingBoars = pigs.stream()
                .filter(pig -> pig.getStatus() == PigStatus.BREEDING_BOAR)
                .toList();

        List<Pig> breedingSows = pigs.stream()
                .filter(pig -> pig.getStatus() == PigStatus.BREEDING_SOW)
                .toList();

        List<Pig> matedSows = pigs.stream()
                .filter(pig -> pig.getStatus() == PigStatus.BREEDING_SOW)
                .filter(pig -> pig.getNotes() != null && pig.getNotes().contains("Breeding Record:"))
                .toList();

        List<Pig> unmatedSows = pigs.stream()
                .filter(pig -> pig.getStatus() == PigStatus.BREEDING_SOW)
                .filter(pig -> pig.getNotes() == null || !pig.getNotes().contains("Breeding Record:"))
                .toList();

        List<Pig> pendingSalePigs = pigs.stream()
                .filter(pig -> pig.getStatus() == PigStatus.PENDING_SALE_APPROVAL)
                .toList();

        List<Pig> forSalePigs = pigs.stream()
                .filter(pig -> pig.getStatus() == PigStatus.FOR_SALE)
                .toList();

        List<Pig> soldPigs = pigs.stream()
                .filter(pig -> pig.getStatus() == PigStatus.SOLD)
                .toList();

        model.addAttribute("farm", farm);
        model.addAttribute("pigs", pigs);

        model.addAttribute("smallPigs", smallPigs);
        model.addAttribute("mediumPigs", mediumPigs);
        model.addAttribute("largePigs", largePigs);

        model.addAttribute("breedingStock", breedingStock);
        model.addAttribute("breedingBoars", breedingBoars);
        model.addAttribute("breedingSows", breedingSows);
        model.addAttribute("matedSows", matedSows);
        model.addAttribute("unmatedSows", unmatedSows);

        model.addAttribute("pendingSalePigs", pendingSalePigs);
        model.addAttribute("forSalePigs", forSalePigs);
        model.addAttribute("soldPigs", soldPigs);

        model.addAttribute("buildings", buildingService.findByFarm(farm));
        model.addAttribute("genetics", geneticsService.findActive());
        model.addAttribute("pigStatuses", pigStatuses());

        return "manager/pigs";
    }

    private List<PigStatus> pigStatuses() {
        return List.of(
                PigStatus.PIGLET,
                PigStatus.GROWER,
                PigStatus.FINISHER,
                PigStatus.BREEDING_SOW,
                PigStatus.BREEDING_BOAR,
                PigStatus.PENDING_SALE_APPROVAL,
                PigStatus.FOR_SALE,
                PigStatus.SOLD);
    }

    @PostMapping("/pigs/purchased")
    public String registerPurchasedPig(
            @RequestParam String code,
            @RequestParam Long buildingId,
            @RequestParam Long geneticsId,
            @RequestParam PigGender gender,
            @RequestParam PigStatus status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam Double currentWeight,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) MultipartFile photo,
            Principal p,
            RedirectAttributes ra) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null) {
                throw new IllegalArgumentException("Manager is not assigned to a farm.");
            }

            if (status == PigStatus.PENDING_SALE_APPROVAL
                    || status == PigStatus.FOR_SALE
                    || status == PigStatus.SOLD) {
                throw new IllegalArgumentException("Sale status cannot be selected from Add Pig form.");
            }

            Building building = buildingService.findById(buildingId).orElseThrow();
            Genetics genetics = geneticsService.findById(geneticsId).orElseThrow();

            if (building.getFarm() == null || !building.getFarm().getId().equals(farm.getId())) {
                throw new IllegalArgumentException("Selected building is outside your farm.");
            }

            String photoPath = null;

            if (photo != null && !photo.isEmpty()) {
                photoPath = fileUploadUtil.saveFile(photo, "pigs");
            }

            Pig pig = pigService.registerPurchasedPig(
                    code,
                    farm,
                    building,
                    genetics,
                    gender,
                    status,
                    birthDate,
                    currentWeight,
                    notes,
                    photoPath);

            ra.addFlashAttribute("success", "Purchased pig " + pig.getCode() + " was registered.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/pigs";
    }

    @PostMapping("/pigs/{id}/approve-sale")
    public String approveSaleRequest(
            @PathVariable Long id,
            Principal p,
            RedirectAttributes ra) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null) {
                throw new IllegalStateException("Manager is not assigned to a farm.");
            }

            Pig pig = pigService.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Pig was not found."));

            if (pig.getBuilding() == null
                    || pig.getBuilding().getFarm() == null
                    || !pig.getBuilding().getFarm().getId().equals(farm.getId())) {
                throw new SecurityException("This pig does not belong to your farm.");
            }

            if (pig.getStatus() != PigStatus.PENDING_SALE_APPROVAL) {
                throw new IllegalStateException("Only supervisor Ready for Sell requests can be approved.");
            }

            if (pig.getSalePrice() == null || pig.getSalePrice() <= 0) {
                throw new IllegalStateException("Sale price is required.");
            }

            pig.setStatus(PigStatus.FOR_SALE);
            pig.setListedForSale(true);
            pig.setListedForSaleDate(LocalDate.now());

            pigService.save(pig);

            ra.addFlashAttribute("success", "Sale request approved. Pig is now visible in marketplace.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/pigs";
    }

    @PostMapping("/pigs/{id}/reject-sale")
    public String rejectSaleRequest(
            @PathVariable Long id,
            Principal p,
            RedirectAttributes ra) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null) {
                throw new IllegalStateException("Manager is not assigned to a farm.");
            }

            Pig pig = pigService.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Pig was not found."));

            if (pig.getBuilding() == null
                    || pig.getBuilding().getFarm() == null
                    || !pig.getBuilding().getFarm().getId().equals(farm.getId())) {
                throw new SecurityException("This pig does not belong to your farm.");
            }

            if (pig.getStatus() != PigStatus.PENDING_SALE_APPROVAL) {
                throw new IllegalStateException("Only pending sale request can be rejected.");
            }

            pig.setStatus(PigStatus.FINISHER);
            pig.setListedForSale(false);
            // pig.setSalePrice(null);
            pig.setListedForSaleDate(null);

            pigService.save(pig);

            ra.addFlashAttribute("success", "Sale request rejected.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/pigs";
    }

    @PostMapping("/pigs/{id}/unlist-sale")
    public String unlistFromSale(
            @PathVariable Long id,
            Principal p,
            RedirectAttributes ra) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null) {
                throw new IllegalStateException("Manager is not assigned to a farm.");
            }

            Pig pig = pigService.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Pig was not found."));

            if (pig.getBuilding() == null
                    || pig.getBuilding().getFarm() == null
                    || !pig.getBuilding().getFarm().getId().equals(farm.getId())) {
                throw new SecurityException("This pig does not belong to your farm.");
            }

            if (pig.getStatus() != PigStatus.FOR_SALE) {
                throw new IllegalStateException("Only for-sale pig can be removed from marketplace.");
            }

            pig.setStatus(PigStatus.FINISHER);
            pig.setListedForSale(false);
            // pig.setSalePrice(null);
            pig.setListedForSaleDate(null);

            pigService.save(pig);

            ra.addFlashAttribute("success", "Pig removed from marketplace.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/pigs";
    }

    @PostMapping("/pigs/{id}/list-sale")
    public String listAgain(
            @PathVariable Long id,
            Principal p,
            RedirectAttributes ra) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            Pig pig = pigService.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Pig was not found."));

            if (pig.getBuilding() == null
                    || pig.getBuilding().getFarm() == null
                    || !pig.getBuilding().getFarm().getId().equals(farm.getId())) {

                throw new SecurityException("This pig does not belong to your farm.");
            }

            if (pig.getStatus() != PigStatus.FINISHER) {
                throw new IllegalStateException("Only finisher pigs can be listed.");
            }

            if (pig.getSalePrice() == null || pig.getSalePrice() <= 0) {
                throw new IllegalStateException("Please set sale price before listing.");
            }

            pig.setStatus(PigStatus.FOR_SALE);
            pig.setListedForSale(true);
            pig.setListedForSaleDate(LocalDate.now());

            pigService.save(pig);

            ra.addFlashAttribute("success",
                    "Pig listed back to marketplace.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/pigs";
    }

    @PostMapping("/pigs/{id}/update")
    public String updatePig(
            @PathVariable Long id,
            @RequestParam Long buildingId,
            @RequestParam Long geneticsId,
            @RequestParam PigGender gender,
            @RequestParam PigStatus status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam Double currentWeight,
            @RequestParam(required = false, defaultValue = "0") Double price,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) MultipartFile photo,
            RedirectAttributes ra,
            Principal p) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null) {
                throw new IllegalArgumentException("Manager is not assigned to a farm.");
            }

            Pig pig = pigService.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Pig was not found."));

            if (pig.getBuilding() == null
                    || pig.getBuilding().getFarm() == null
                    || !pig.getBuilding().getFarm().getId().equals(farm.getId())) {
                throw new SecurityException("This pig does not belong to your farm.");
            }

            PigStatus oldStatus = pig.getStatus();

            Building building = buildingService.findById(buildingId).orElseThrow(
                    () -> new IllegalArgumentException("Selected building was not found."));

            Genetics genetics = geneticsService.findById(geneticsId).orElseThrow(
                    () -> new IllegalArgumentException("Selected genetics was not found."));

            if (building.getFarm() == null || !building.getFarm().getId().equals(farm.getId())) {
                throw new IllegalArgumentException("Selected building is outside your farm.");
            }

            if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Enter a valid birth date.");
            }

            if (currentWeight == null || currentWeight <= 0) {
                throw new IllegalArgumentException("Current weight must be greater than zero.");
            }

            pig.setBuilding(building);
            pig.setGenetics(genetics);
            pig.setGender(gender);
            pig.setBirthDate(birthDate);
            pig.setCurrentWeight(currentWeight);
            pig.setNotes(notes);

            /*
             * IMPORTANT:
             * If pig is already on marketplace, editing must NOT unlink/unlist it.
             * Only Unlist button should remove pig from marketplace.
             */
            if (oldStatus == PigStatus.FOR_SALE) {
                if (price == null || price <= 0) {
                    throw new IllegalArgumentException("Sale price is required for marketplace pigs.");
                }

                pig.setStatus(PigStatus.FOR_SALE);
                pig.setSalePrice(price);
                pig.setListedForSale(true);

                if (pig.getListedForSaleDate() == null) {
                    pig.setListedForSaleDate(LocalDate.now());
                }

            } else if (oldStatus == PigStatus.PENDING_SALE_APPROVAL) {
                if (price == null || price <= 0) {
                    throw new IllegalArgumentException("Sale price is required for pending sale pigs.");
                }

                pig.setStatus(PigStatus.PENDING_SALE_APPROVAL);
                pig.setSalePrice(price);
                pig.setListedForSale(false);

            } else if (oldStatus == PigStatus.SOLD) {
                pig.setStatus(PigStatus.SOLD);
                pig.setListedForSale(false);

            } else {
                pig.setStatus(status);

                if (status == PigStatus.FOR_SALE) {
                    if (price == null || price <= 0) {
                        throw new IllegalArgumentException("Sale price is required for marketplace pigs.");
                    }

                    pig.setSalePrice(price);
                    pig.setListedForSale(true);

                    if (pig.getListedForSaleDate() == null) {
                        pig.setListedForSaleDate(LocalDate.now());
                    }

                } else if (status == PigStatus.PENDING_SALE_APPROVAL) {
                    if (price == null || price <= 0) {
                        throw new IllegalArgumentException("Sale price is required for pending sale pigs.");
                    }

                    pig.setSalePrice(price);
                    pig.setListedForSale(false);

                } else if (status == PigStatus.SOLD) {
                    pig.setListedForSale(false);

                } else {
                    pig.setSalePrice(null);
                    pig.setListedForSale(false);
                    pig.setListedForSaleDate(null);
                }
            }

            if (photo != null && !photo.isEmpty()) {
                pig.setPhotoPath(fileUploadUtil.saveFile(photo, "pigs"));
            }

            pigService.save(pig);

            ra.addFlashAttribute("success", "Pig information updated.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/pigs";
    }

    // =========================================================
    // Inventory
    // =========================================================

    @GetMapping("/inventory")
    public String inventory(Principal p, Model model) {
        User manager = currentManager(p);
        Farm farm = managerFarm(manager);

        model.addAttribute("inventory", farm == null ? List.of() : inventoryService.getViews(farm));
        model.addAttribute("alerts", farm == null ? List.of()
                : inventoryService.getViews(farm).stream()
                        .filter(InventoryService.InventoryView::alertTriggered)
                        .toList());
        model.addAttribute("shipments", farm == null ? List.of() : feedShipmentService.findByFarm(farm));

        model.addAttribute("buildingsById",
                farm == null ? java.util.Map.of()
                        : buildingService.findByFarm(farm).stream()
                                .collect(java.util.stream.Collectors.toMap(Building::getId, Building::getName)));

        return "manager/inventory";
    }

    @PostMapping("/shipments/{id}/confirm")
    public String confirmShipmentMismatch(
            @PathVariable Long id,
            Principal p,
            @RequestParam(required = false, defaultValue = "Manager override authorized") String reason,
            RedirectAttributes ra) {

        try {
            FeedShipment shipment = feedShipmentService.findById(id).orElseThrow();
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null || shipment.getFarm() == null || !shipment.getFarm().getId().equals(farm.getId())) {
                throw new SecurityException("Shipment is outside your farm.");
            }

            feedShipmentService.managerConfirm(id, reason);
            ra.addFlashAttribute("success", "Shipment mismatch confirmed. Payment released.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/inventory";
    }

    @PostMapping("/shipments/{id}/reject")
    public String rejectShipment(
            @PathVariable Long id,
            Principal p,
            @RequestParam String reason,
            RedirectAttributes ra) {

        try {
            FeedShipment shipment = feedShipmentService.findById(id).orElseThrow();
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null || shipment.getFarm() == null || !shipment.getFarm().getId().equals(farm.getId())) {
                throw new SecurityException("Shipment is outside your farm.");
            }

            feedShipmentService.managerReject(id, reason);
            ra.addFlashAttribute("error", "Factory report rejected and removed. The factory must resubmit it.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/inventory";
    }

    // =========================================================
    // Reports
    // =========================================================

    @GetMapping("/reports")
    public String reports(
            Principal p,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        User manager = currentManager(p);
        Farm farm = managerFarm(manager);
        LocalDate selectedDate = date != null ? date : LocalDate.now();

        List<Building> buildings = farm == null ? List.of() : buildingService.findByFarm(farm);
        List<DailyTask> tasks = new java.util.ArrayList<>();

        if (farm != null && buildingId != null) {
            Building building = buildingService.findById(buildingId).orElseThrow();

            if (building.getFarm() == null || !building.getFarm().getId().equals(farm.getId())) {
                throw new SecurityException("Building is outside your farm.");
            }

            tasks = taskService.getTasksByBuilding(building, selectedDate);
        } else if (farm != null) {
            for (Building building : buildings) {
                tasks.addAll(taskService.getTasksByBuilding(building, selectedDate));
            }
        }

        model.addAttribute("farm", farm);
        model.addAttribute("buildings", buildings);
        model.addAttribute("tasks", tasks);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("selectedBuildingId", buildingId);

        return "manager/reports";
    }

    // =========================================================
    // Analytics
    // =========================================================

    @GetMapping("/analytics")
    public String analytics(Principal p, Model model) {
        User manager = currentManager(p);
        Farm farm = managerFarm(manager);

        model.addAttribute("financials", farm == null ? List.of() : analyticsService.getMonthlyFinancials(farm));
        model.addAttribute("attendance", farm == null ? null : analyticsService.getAttendanceStats(farm));
        model.addAttribute("farm", farm);

        return "manager/analytics";
    }

    // =========================================================
    // Shifts
    // =========================================================

    @GetMapping("/shifts")
    public String listShifts(Principal p, Model model) {
        User manager = currentManager(p);
        Farm farm = managerFarm(manager);

        var shifts = farm == null ? List.<Shift>of() : shiftService.findByFarm(farm);

        model.addAttribute("shifts", shifts);
        model.addAttribute("assignedUsersByShift", shifts.stream()
                .collect(java.util.stream.Collectors.toMap(
                        Shift::getId,
                        shiftService::findActiveUsersForShift)));

        model.addAttribute("allStaff", farm == null ? List.of() : userService.findByFarm(farm));
        model.addAttribute("farm", farm);
        model.addAttribute("pendingClockOuts", attendanceService.pendingForManager(manager));

        return "manager/shifts";
    }

    @PostMapping("/attendance/{id}/approve-clock-out")
    public String approveSupervisorClockOut(
            @PathVariable Long id,
            Principal p,
            RedirectAttributes ra) {

        try {
            attendanceService.approveClockOut(id, currentManager(p));
            ra.addFlashAttribute("success", "Supervisor early clock-out approved.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/shifts";
    }

    @PostMapping("/shifts/create")
    public String createShift(
            Principal p,
            @ModelAttribute Shift shift,
            RedirectAttributes ra) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null) {
                throw new IllegalStateException("Manager is not assigned to a farm.");
            }

            shift.setFarm(farm);
            shift.setActive(true);

            shiftService.createShift(shift);

            ra.addFlashAttribute("success", "Shift configuration saved.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/shifts";
    }

    @PostMapping("/shifts/{id}/delete")
    public String deleteShift(
            @PathVariable Long id,
            RedirectAttributes ra) {

        try {
            shiftService.deleteShift(id);
            ra.addFlashAttribute("success", "Shift deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/shifts";
    }

    @PostMapping("/shifts/{id}/assign")
    public String assignStaffToShift(
            @PathVariable Long id,
            @RequestParam Long userId,
            RedirectAttributes ra) {

        try {
            User staff = userService.findById(userId).orElseThrow();
            Shift shift = shiftService.findShiftById(id).orElseThrow();

            shiftService.assignStaffToShift(staff, shift, LocalDate.now());

            ra.addFlashAttribute("success", "Staff assigned to shift.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/shifts";
    }

    // =========================================================
    // Semen
    // =========================================================

    @GetMapping("/semen")
    public String manageSemen(Principal p, Model model) {
        User manager = currentManager(p);
        Farm farm = managerFarm(manager);

        if (farm == null) {
            model.addAttribute("farm", null);
            model.addAttribute("boars", List.of());
            model.addAttribute("buildings", List.of());
            model.addAttribute("genetics", geneticsService.findActive());
            model.addAttribute("semenPrice", 0);
            model.addAttribute("error", "Manager is not assigned to a farm.");
            return "manager/semen";
        }

        List<Pig> boars = pigService.findByFarm(farm).stream()
                .filter(pig -> pig.getStatus() == PigStatus.BREEDING_BOAR)
                .filter(pig -> pig.getGender() == PigGender.MALE)
                .toList();

        model.addAttribute("farm", farm);
        model.addAttribute("boars", boars);
        model.addAttribute("buildings", buildingService.findByFarm(farm));
        model.addAttribute("genetics", geneticsService.findActive());
        model.addAttribute("semenPrice", ruleService.getSemenPrice(farm));

        return "manager/semen";
    }

    @PostMapping("/semen/price")
    public String updateManagerSemenPrice(
            Principal p,
            @RequestParam double semenPrice,
            RedirectAttributes ra) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null) {
                throw new IllegalArgumentException("Manager is not assigned to a farm.");
            }

            if (semenPrice <= 0) {
                throw new IllegalArgumentException("Semen price must be greater than zero.");
            }

            ruleService.setSemenPrice(farm, manager, semenPrice);
            ra.addFlashAttribute("success", "Semen marketplace price updated.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/semen";
    }

    @PostMapping("/semen/create")
    public String createSemenBoar(
            @RequestParam String code,
            @RequestParam Long buildingId,
            @RequestParam Long geneticsId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam Double currentWeight,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) MultipartFile photo,
            Principal p,
            RedirectAttributes ra) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null) {
                throw new IllegalArgumentException("Manager is not assigned to a farm.");
            }

            Building building = buildingService.findById(buildingId).orElseThrow(
                    () -> new IllegalArgumentException("Selected building was not found."));

            Genetics genetics = geneticsService.findById(geneticsId).orElseThrow(
                    () -> new IllegalArgumentException("Selected genetics was not found."));

            if (building.getFarm() == null || !building.getFarm().getId().equals(farm.getId())) {
                throw new IllegalArgumentException("Selected building is outside your farm.");
            }

            String photoPath = null;

            if (photo != null && !photo.isEmpty()) {
                photoPath = fileUploadUtil.saveFile(photo, "pigs");
            }

            Pig boar = pigService.registerPurchasedPig(
                    code,
                    farm,
                    building,
                    genetics,
                    PigGender.MALE,
                    PigStatus.BREEDING_BOAR,
                    birthDate,
                    currentWeight,
                    notes,
                    photoPath);

            ra.addFlashAttribute("success", "Semen boar " + boar.getCode() + " created.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/semen";
    }

    @PostMapping("/semen/{id}/update")
    public String updateSemenBoar(
            @PathVariable Long id,
            @RequestParam Long buildingId,
            @RequestParam Long geneticsId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam Double currentWeight,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) MultipartFile photo,
            Principal p,
            RedirectAttributes ra) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null) {
                throw new IllegalArgumentException("Manager is not assigned to a farm.");
            }

            Pig boar = pigService.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Selected semen boar was not found."));

            if (boar.getBuilding() == null
                    || boar.getBuilding().getFarm() == null
                    || !boar.getBuilding().getFarm().getId().equals(farm.getId())) {
                throw new IllegalArgumentException("You can update only your farm's semen boars.");
            }

            Building building = buildingService.findById(buildingId).orElseThrow(
                    () -> new IllegalArgumentException("Selected building was not found."));

            Genetics genetics = geneticsService.findById(geneticsId).orElseThrow(
                    () -> new IllegalArgumentException("Selected genetics was not found."));

            if (building.getFarm() == null || !building.getFarm().getId().equals(farm.getId())) {
                throw new IllegalArgumentException("Selected building is outside your farm.");
            }

            if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Enter a valid birth date.");
            }

            if (currentWeight == null || currentWeight <= 0) {
                throw new IllegalArgumentException("Weight must be greater than zero.");
            }

            boar.setBuilding(building);
            boar.setGenetics(genetics);
            boar.setGender(PigGender.MALE);
            boar.setStatus(PigStatus.BREEDING_BOAR);
            boar.setBirthDate(birthDate);
            boar.setCurrentWeight(currentWeight);
            boar.setNotes(notes);

            if (photo != null && !photo.isEmpty()) {
                boar.setPhotoPath(fileUploadUtil.saveFile(photo, "pigs"));
            }

            pigService.save(boar);

            ra.addFlashAttribute("success", "Semen boar updated.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/semen";
    }

    @PostMapping("/semen/{id}/delete")
    public String deleteSemenBoar(
            @PathVariable Long id,
            Principal p,
            RedirectAttributes ra) {

        try {
            User manager = currentManager(p);
            Farm farm = managerFarm(manager);

            if (farm == null) {
                throw new IllegalArgumentException("Manager is not assigned to a farm.");
            }

            Pig boar = pigService.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Selected semen boar was not found."));

            if (boar.getBuilding() == null
                    || boar.getBuilding().getFarm() == null
                    || !boar.getBuilding().getFarm().getId().equals(farm.getId())) {
                throw new IllegalArgumentException("You can delete only your farm's semen boars.");
            }

            pigService.delete(id);
            ra.addFlashAttribute("success", "Semen boar deleted from marketplace.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/manager/semen";
    }
}