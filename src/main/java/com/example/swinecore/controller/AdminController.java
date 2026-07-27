package com.example.swinecore.controller;

import com.example.swinecore.entity.*;
import com.example.swinecore.entity.enums.Role;
import com.example.swinecore.service.*;
import com.example.swinecore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final FarmService farmService;
    private final BuildingService buildingService;
    private final AnalyticsService analyticsService;
    private final GeneticsService geneticsService;
    private final CustomerService customerService;
    private final RoomService roomService;
    private final PigOrderRepository pigOrderRepository;
    private final SemenOrderRepository semenOrderRepository;
    private final FinanceTransactionRepository financeTransactionRepository;
    private final AttendanceRepository attendanceRepository;
    private final FeedShipmentRepository feedShipmentRepository;
    private final FeedShipmentService feedShipmentService;

    // ---- Dashboard ----

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("summary", analyticsService.getGlobalSummary());
        model.addAttribute("pigDist", analyticsService.getPigDistributionByFarm());
        model.addAttribute("farms", farmService.findAll());
        return "admin/dashboard";
    }

    // ---- Users ----

    @GetMapping("/users")
    public String listUsers(@RequestParam(required = false) String role, Model model) {
        List<User> users;
        if ("CUSTOMER".equalsIgnoreCase(role)) {
            users = List.of();
        } else if (role != null && !role.isEmpty()) {
            users = userService.findByRoleNonAdmin(Role.valueOf(role));
        } else {
            users = userService.findAllNonAdmin();
        }
        model.addAttribute("users", users);
        model.addAttribute("roles", userService.getAssignableRoles());
        model.addAttribute("selectedRole", role);
        model.addAttribute("customers", role == null || role.isEmpty() || "CUSTOMER".equalsIgnoreCase(role)
            ? customerService.findAll() : List.of());
        model.addAttribute("customerService", customerService);
        return "admin/users";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", java.util.List.of(Role.HR, Role.MANAGER));
        model.addAttribute("farms", farmService.findAll());
        model.addAttribute("managerOccupiedFarmIds", occupiedManagerFarmIds());
        return "admin/user-form";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute User user,
                             @RequestParam(required = false) Long farmId,
                             RedirectAttributes ra) {
        try {
            if (farmId != null) user.setFarm(farmService.findById(farmId).orElseThrow());
            userService.create(user);
            ra.addFlashAttribute("success", "User created successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("roles", userService.getAssignableRoles());
        model.addAttribute("farms", farmService.findAll());
        model.addAttribute("managerOccupiedFarmIds", occupiedManagerFarmIds());
        return "admin/user-form";
    }

    @PostMapping("/users/{id}/update")
    public String updateUser(@PathVariable Long id, @ModelAttribute User form, RedirectAttributes ra) {
        User user = userService.findById(id).orElseThrow();
        if (form.getName() != null && !form.getName().isBlank()) user.setName(form.getName().trim());
        if (form.getRole() == Role.ADMIN && !UserService.ADMIN_SEEDED_EMAIL.equalsIgnoreCase(user.getEmail())) {
            ra.addFlashAttribute("error", "Cannot assign ADMIN role to this account.");
            return "redirect:/admin/users";
        }
        user.setRole(form.getRole());
        user.setPhone(form.getPhone());
        userService.save(user);
        ra.addFlashAttribute("success", "User updated.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                              @RequestParam String confirmName,
                              RedirectAttributes ra) {
        User user = userService.findById(id).orElseThrow();
        if (UserService.ADMIN_SEEDED_EMAIL.equalsIgnoreCase(user.getEmail())) {
            ra.addFlashAttribute("error", "The system admin account cannot be deleted.");
            return "redirect:/admin/users";
        }
        String fullName = user.getFullName();
        if (!fullName.equalsIgnoreCase(confirmName)) {
            ra.addFlashAttribute("error", "Name confirmation does not match. Deletion aborted.");
            return "redirect:/admin/users";
        }
        userService.disable(id);
        ra.addFlashAttribute("success", "User account disabled.");
        return "redirect:/admin/users";
    }

    // ---- Farms ----

    @GetMapping("/customers")
    public String customers(Model model) {
        List<CustomerAccount> customers = customerService.findAll();
        model.addAttribute("customers", customers);
        model.addAttribute("customerService", customerService);
        return "admin/customers";
    }

    @PostMapping("/customers/{id}/restrict")
    public String restrictCustomer(@PathVariable Long id, @RequestParam String status,
                                   @RequestParam String reason, RedirectAttributes ra) {
        try { customerService.restrict(id, status, reason); ra.addFlashAttribute("success", "Customer account " + status.toLowerCase() + "."); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/customers";
    }

    @PostMapping("/customers/{id}/activate")
    public String activateCustomer(@PathVariable Long id, RedirectAttributes ra) {
        customerService.activate(id); ra.addFlashAttribute("success", "Customer account activated.");
        return "redirect:/admin/customers";
    }

    // ---- Farms ----

    @GetMapping("/farms")
    public String listFarms(Model model) {
        model.addAttribute("farms", farmService.findAll());
        return "admin/farms";
    }

    @GetMapping("/farms/new")
    public String newFarmForm(Model model) {
        model.addAttribute("farm", new Farm());
        return "admin/farm-form";
    }

    @PostMapping("/farms/create")
    @org.springframework.transaction.annotation.Transactional
    public String createFarm(@ModelAttribute Farm farm,
                             @RequestParam java.util.Map<String,String> fields,
                             RedirectAttributes ra) {
        try {
            applyMyanmarLocation(farm);
            Farm savedFarm = farmService.create(farm);
            java.util.List<String> buildingKeys = fields.keySet().stream()
                .filter(k -> k.startsWith("buildingName_")).sorted().toList();
            if (buildingKeys.isEmpty()) throw new IllegalArgumentException("At least one building is required.");
            for (String key : buildingKeys) {
                String index = key.substring("buildingName_".length());
                String buildingName = fields.getOrDefault(key, "").trim();
                String rooms = fields.getOrDefault("rooms_" + index, "").trim();
                if (buildingName.isBlank() || rooms.isBlank())
                    throw new IllegalArgumentException("Every building needs a name and at least one room name.");
                Building building = buildingService.create(Building.builder().name(buildingName).farm(savedFarm).build());
                for (String roomNameRaw : rooms.split(",")) {
                    String roomName = roomNameRaw.trim();
                    if (!roomName.isBlank()) roomService.create(Room.builder().name(roomName).building(building).build());
                }
            }
            ra.addFlashAttribute("success", "Farm and buildings created successfully.");
        } catch (Exception e) {
            org.springframework.transaction.interceptor.TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/farms";
    }

    @GetMapping("/farms/{id}")
    public String viewFarm(@PathVariable Long id, Model model) {
        Farm farm = farmService.findById(id).orElseThrow();
        model.addAttribute("farm", farm);
        model.addAttribute("buildings", buildingService.findByFarm(farm));
        model.addAttribute("staff", userService.findByFarm(farm));
        model.addAttribute("hasMinimumArchitecture", farmService.hasMinimumArchitecture(farm));
        return "admin/farm-detail";
    }

    @GetMapping("/farms/{id}/edit")
    public String editFarm(@PathVariable Long id, Model model) {
        model.addAttribute("farm", farmService.findById(id).orElseThrow());
        return "admin/farm-form";
    }

    @PostMapping("/farms/{id}/update")
    public String updateFarm(@PathVariable Long id, @ModelAttribute Farm form, RedirectAttributes ra) {
        Farm farm = farmService.findById(id).orElseThrow();
        farm.setName(form.getName());
        farm.setLocation(form.getLocation());
        farm.setDescription(form.getDescription());
        applyMyanmarLocation(farm);
        farmService.save(farm);
        ra.addFlashAttribute("success", "Farm updated.");
        return "redirect:/admin/farms";
    }

    private void applyMyanmarLocation(Farm farm) {
        String location = farm.getLocation() == null ? "" : farm.getLocation().trim();
        if (location.isBlank()) throw new IllegalArgumentException("Please enter a Myanmar city.");
        farm.setLocation(location);
        double[] coordinates = switch (location.toLowerCase(java.util.Locale.ROOT)) {
            case "yangon" -> new double[]{16.8409, 96.1735};
            case "mandalay" -> new double[]{21.9588, 96.0891};
            case "nay pyi taw", "naypyidaw" -> new double[]{19.7633, 96.0785};
            case "bago" -> new double[]{17.3367, 96.4817};
            case "pathein" -> new double[]{16.7792, 94.7321};
            case "mawlamyine" -> new double[]{16.4905, 97.6283};
            case "hpa-an" -> new double[]{16.8895, 97.6348};
            case "taunggyi" -> new double[]{20.7892, 97.0378};
            case "meiktila" -> new double[]{20.8778, 95.8584};
            case "magway" -> new double[]{20.1496, 94.9325};
            case "monywa" -> new double[]{22.1086, 95.1358};
            case "myitkyina" -> new double[]{25.3833, 97.4000};
            case "sittwe" -> new double[]{20.1462, 92.8984};
            case "loikaw" -> new double[]{19.6742, 97.2094};
            case "hakha" -> new double[]{22.6440, 93.6108};
            default -> null;
        };
        farm.setLatitude(coordinates == null ? null : coordinates[0]);
        farm.setLongitude(coordinates == null ? null : coordinates[1]);
    }

    @PostMapping("/farms/{id}/delete")
    public String deleteFarm(@PathVariable Long id,
                              @RequestParam String confirmName,
                              RedirectAttributes ra) {
        try {
            farmService.delete(id, confirmName);
            ra.addFlashAttribute("success", "Farm deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/farms";
    }

    @PostMapping("/farms/{farmId}/assign-manager")
    public String assignManager(@PathVariable Long farmId,
                                 @RequestParam Long userId,
                                 RedirectAttributes ra) {
        try {
            Farm farm = farmService.findById(farmId).orElseThrow();
            userService.assignToFarm(userId, farm);
            ra.addFlashAttribute("success", "Manager assigned.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/farms/" + farmId;
    }

    @PostMapping("/farms/{farmId}/buildings/create")
    public String createBuilding(@PathVariable Long farmId,
                                 @RequestParam String name,
                                 RedirectAttributes ra) {
        try {
            Farm farm = farmService.findById(farmId).orElseThrow();
            buildingService.create(Building.builder().farm(farm).name(name.trim()).build());
            ra.addFlashAttribute("success", "Building created successfully.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/farms/" + farmId;
    }

    private java.util.Set<Long> occupiedManagerFarmIds() {
        return userService.findByRole(Role.MANAGER).stream().filter(User::isEnabled)
            .map(User::getFarm).filter(java.util.Objects::nonNull).map(Farm::getId)
            .collect(java.util.stream.Collectors.toSet());
    }

    // ---- Genetics ----

    @GetMapping("/genetics")
    public String listGenetics(Model model) {
        model.addAttribute("geneticsList", geneticsService.findAll());
        return "admin/genetics";
    }

    @PostMapping("/genetics/create")
    public String createGenetics(@ModelAttribute Genetics genetics, RedirectAttributes ra) {
        try {
            geneticsService.create(genetics);
            ra.addFlashAttribute("success", "Genetics category created.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/genetics";
    }

    @PostMapping("/genetics/{id}/delete")
    public String deleteGenetics(@PathVariable Long id, @RequestParam String confirmName, RedirectAttributes ra) {
        Genetics g = geneticsService.findById(id).orElseThrow();
        if (!g.getName().equalsIgnoreCase(confirmName)) {
            ra.addFlashAttribute("error", "Name mismatch. Deletion aborted.");
        } else {
            geneticsService.delete(id);
            ra.addFlashAttribute("success", "Genetics deleted.");
        }
        return "redirect:/admin/genetics";
    }

    // ---- Analytics ----

    @GetMapping("/analytics")
    public String analytics(Model model) {
        model.addAttribute("summary", analyticsService.getGlobalSummary());
        model.addAttribute("pigDist", analyticsService.getPigDistributionByFarm());
        List<Farm> farms = farmService.findAll();
        model.addAttribute("farms", farms);
        model.addAttribute("farmIds", farms.stream().collect(java.util.stream.Collectors.toMap(
            Farm::getName, Farm::getId, (a, b) -> a, java.util.LinkedHashMap::new)));
        return "admin/analytics";
    }

    @GetMapping("/operations")
    public String operations(Model model) {
        List<FinanceTransaction> finance = financeTransactionRepository.findAll().stream()
            .sorted(java.util.Comparator.comparing(FinanceTransaction::getCreatedAt,
                java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder()))).toList();
        double income = finance.stream().filter(f -> "INCOME".equalsIgnoreCase(f.getType()))
            .mapToDouble(f -> f.getAmount() == null ? 0 : f.getAmount()).sum();
        double expense = finance.stream().filter(f -> "EXPENSE".equalsIgnoreCase(f.getType()))
            .mapToDouble(f -> f.getAmount() == null ? 0 : f.getAmount()).sum();
        model.addAttribute("pigOrders", pigOrderRepository.findAll());
        model.addAttribute("semenOrders", semenOrderRepository.findAll());
        model.addAttribute("finance", finance);
        model.addAttribute("attendance", attendanceRepository.findAll().stream()
            .filter(a -> a.getUser().getRole() == Role.STAFF || a.getUser().getRole() == Role.SUPERVISOR)
            .sorted(java.util.Comparator.comparing(Attendance::getWorkDate).reversed()).toList());
        model.addAttribute("shipments", feedShipmentRepository.findAll());
        model.addAttribute("totalIncome", income);
        model.addAttribute("totalExpense", expense);
        model.addAttribute("netFinance", income - expense);
        return "admin/operations";
    }

    @PostMapping("/operations/shipments/{id}/confirm")
    public String confirmManagerReport(@PathVariable Long id,
                                       @RequestParam(defaultValue = "Approved by administrator") String reason,
                                       RedirectAttributes ra) {
        try { feedShipmentService.managerConfirm(id, reason); ra.addFlashAttribute("success", "Manager report confirmed."); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/operations#reports";
    }

    @PostMapping("/operations/shipments/{id}/reject")
    public String rejectManagerReport(@PathVariable Long id, @RequestParam String reason,
                                      RedirectAttributes ra) {
        try { feedShipmentService.managerReject(id, reason); ra.addFlashAttribute("success", "Manager report rejected."); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/operations#reports";
    }

    // ---- Myanmar Interactive Map ----

    @GetMapping("/map")
    public String myanmarMap(Model model) {
        return "redirect:/admin/farms";
    }

    @GetMapping("/farms/{id}/map-detail")
    @ResponseBody
    public java.util.Map<String, Object> farmMapDetail(@PathVariable Long id) {
        Farm farm = farmService.findById(id).orElseThrow();
        long buildingCount = buildingService.findByFarm(farm).size();
        long staffCount = userService.findByFarm(farm).size();
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("id", farm.getId());
        data.put("name", farm.getName());
        data.put("code", farm.getCode());
        data.put("location", farm.getLocation());
        data.put("description", farm.getDescription());
        data.put("buildingCount", buildingCount);
        data.put("staffCount", staffCount);
        data.put("latitude", farm.getLatitude());
        data.put("longitude", farm.getLongitude());
        return data;
    }
}
