package com.example.swinecore.controller;

import com.example.swinecore.entity.Building;
import com.example.swinecore.entity.Farm;
import com.example.swinecore.entity.FarmAd;
import com.example.swinecore.entity.User;
import com.example.swinecore.entity.enums.Role;
import com.example.swinecore.repository.FarmAdRepository;
import com.example.swinecore.service.*;
import com.example.swinecore.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/hr")
@PreAuthorize("hasRole('HR')")
@RequiredArgsConstructor
public class HrController {

    private final UserService userService;
    private final FarmService farmService;
    private final BuildingService buildingService;
    private final FarmAdRepository farmAdRepository;
    private final FileUploadUtil fileUploadUtil;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("supervisors", userService.findByRole(Role.SUPERVISOR));
        model.addAttribute("staff", userService.findByRole(Role.STAFF));
        model.addAttribute("managers", userService.findByRole(Role.MANAGER));
        model.addAttribute("farms", farmService.findAll());
        return "hr/dashboard";
    }

    @GetMapping("/staff")
    public String listStaff(@RequestParam(required = false) String role, Model model) {
        List<User> users;
        if ("SUPERVISOR".equals(role)) {
            users = userService.findByRole(Role.SUPERVISOR);
        } else if ("STAFF".equals(role)) {
            users = userService.findByRole(Role.STAFF);
        } else {
            users = userService.findByRole(Role.STAFF);
            users.addAll(userService.findByRole(Role.SUPERVISOR));
        }
        model.addAttribute("users", users);
        model.addAttribute("farms", farmService.findAll());
        return "hr/staff";
    }

    @GetMapping("/staff/new")
    public String newStaffForm(@RequestParam(required = false, defaultValue = "STAFF") String role, Model model) {
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("defaultRole", role);
        model.addAttribute("farms", farmService.findAll());
        model.addAttribute("buildingsByFarm", groupBuildingsByFarm());
        model.addAttribute("occupiedSupervisorBuildingIds", occupiedSupervisorBuildingIds());
        return "hr/staff-form";
    }

    @PostMapping("/staff/create")
    public String createStaff(@ModelAttribute User user,
                              @RequestParam Long farmId,
                              @RequestParam Long buildingId,
                              RedirectAttributes ra) {
        try {
            Farm farm = farmService.findById(farmId).orElseThrow();
            Building building = buildingService.findById(buildingId).orElseThrow();
            user.setFarm(farm);
            user.setBuilding(building);
            userService.create(user);
            ra.addFlashAttribute("success", "Account created. User must login and change their password.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/hr/staff";
    }

    @GetMapping("/staff/{id}/edit")
    public String editStaff(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id).orElseThrow());
        model.addAttribute("farms", farmService.findAll());
        model.addAttribute("buildingsByFarm", groupBuildingsByFarm());
        model.addAttribute("occupiedSupervisorBuildingIds", occupiedSupervisorBuildingIds());
        return "hr/staff-form";
    }

    @PostMapping("/staff/{id}/update")
    public String updateStaff(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam(required = false) String phone,
                               RedirectAttributes ra) {
        User user = userService.findById(id).orElseThrow();
        user.setName(name.trim().isEmpty() ? user.getName() : name.trim());
        user.setPhone(phone);
        userService.save(user);
        ra.addFlashAttribute("success", "User updated.");
        return "redirect:/hr/staff";
    }

    @PostMapping("/staff/{id}/assign-building")
    public String assignToBuilding(@PathVariable Long id,
                                    @RequestParam Long buildingId,
                                    RedirectAttributes ra) {
        Building building = buildingService.findById(buildingId).orElseThrow();
        try {
            userService.assignToBuilding(id, building);
            ra.addFlashAttribute("success", "User assigned to building.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/hr/staff";
    }

    @PostMapping("/staff/{id}/delete")
    public String deleteStaff(@PathVariable Long id, RedirectAttributes ra) {
        userService.disable(id);
        ra.addFlashAttribute("success", "Account disabled.");
        return "redirect:/hr/staff";
    }

    // ---- Farm Ads ----

    @GetMapping("/ads")
    public String listAds(Principal principal, Model model) {
        User currentHr = userService.findByEmail(principal.getName()).orElseThrow();
        model.addAttribute("ads", farmAdRepository.findAll());
        model.addAttribute("farms", farmService.findAll());
        model.addAttribute("currentHrId", currentHr.getId());
        return "hr/ads";
    }

    @PostMapping("/ads/{id}/edit")
    public String editAd(@PathVariable Long id,
                         @RequestParam Long farmId,
                         @RequestParam String title,
                         @RequestParam String description,
                         @RequestParam String category,
                         @RequestParam(required = false) String contactInfo,
                         @RequestParam(required = false) MultipartFile image,
                         Principal principal, RedirectAttributes ra) {
        try {
            User currentHr = userService.findByEmail(principal.getName()).orElseThrow();
            FarmAd ad = ownedAd(id, currentHr);
            ad.setFarm(farmService.findById(farmId).orElseThrow());
            ad.setTitle(title.trim());
            ad.setDescription(description.trim());
            ad.setCategory(FarmAd.AdCategory.valueOf(category));
            ad.setContactInfo(contactInfo);
            String replacement = fileUploadUtil.saveFile(image, "ads");
            if (replacement != null) {
                fileUploadUtil.deleteFile(ad.getImagePath());
                ad.setImagePath(replacement);
            }
            farmAdRepository.save(ad);
            ra.addFlashAttribute("success", "Advertisement updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/hr/ads";
    }

    @PostMapping("/ads/create")
    public String createAd(@RequestParam Long farmId,
                            @RequestParam String title,
                            @RequestParam String description,
                            @RequestParam(required = false) String category,
                            @RequestParam(required = false) String contactInfo,
                            @RequestParam(required = false) MultipartFile image,
                            Principal principal,
                            RedirectAttributes ra) {
        try {
            Farm farm = farmService.findById(farmId).orElseThrow();
            User createdBy = userService.findByEmail(principal.getName()).orElseThrow();
            FarmAd.AdCategory adCategory = FarmAd.AdCategory.LIVE_LIVESTOCK;
            if (category != null && !category.isEmpty()) {
                try { adCategory = FarmAd.AdCategory.valueOf(category); } catch (Exception ignored) {}
            }
            String imagePath = fileUploadUtil.saveFile(image, "ads");
            FarmAd ad = FarmAd.builder().farm(farm).title(title)
                .description(description).category(adCategory).contactInfo(contactInfo)
                .imagePath(imagePath)
                .active(true).createdBy(createdBy).build();
            farmAdRepository.save(ad);
            ra.addFlashAttribute("success", "Ad published to unauthenticated landing repository.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to publish ad: " + e.getMessage());
        }
        return "redirect:/hr/ads";
    }

    @PostMapping("/ads/{id}/toggle")
    public String toggleAd(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        try {
            User currentHr = userService.findByEmail(principal.getName()).orElseThrow();
            FarmAd ad = ownedAd(id, currentHr);
            ad.setActive(!ad.isActive());
            farmAdRepository.save(ad);
            ra.addFlashAttribute("success", "Ad status toggled.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/hr/ads";
    }

    @PostMapping("/ads/{id}/delete")
    public String deleteAd(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        try {
            User currentHr = userService.findByEmail(principal.getName()).orElseThrow();
            FarmAd ad = ownedAd(id, currentHr);
            fileUploadUtil.deleteFile(ad.getImagePath());
            farmAdRepository.delete(ad);
            ra.addFlashAttribute("success", "Ad deleted.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/hr/ads";
    }

    private FarmAd ownedAd(Long id, User currentHr) {
        FarmAd ad = farmAdRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Advertisement not found."));
        if (ad.getCreatedBy() == null || !ad.getCreatedBy().getId().equals(currentHr.getId()))
            throw new org.springframework.security.access.AccessDeniedException(
                "You can edit only advertisements that you created.");
        return ad;
    }

    private Map<Farm, List<Building>> groupBuildingsByFarm() {
        return buildingService.findAllWithFarm().stream()
            .collect(Collectors.groupingBy(
                Building::getFarm,
                LinkedHashMap::new,
                Collectors.toList()
            ));
    }

    private java.util.Set<Long> occupiedSupervisorBuildingIds() {
        return userService.findByRole(Role.SUPERVISOR).stream()
            .filter(User::isEnabled).map(User::getBuilding).filter(java.util.Objects::nonNull)
            .map(Building::getId).collect(java.util.stream.Collectors.toSet());
    }
}
