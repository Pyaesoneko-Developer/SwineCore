package com.example.swinecore.controller;

import com.example.swinecore.entity.User;
import com.example.swinecore.service.UserService;
import com.example.swinecore.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final FileUploadUtil fileUploadUtil;

    // @GetMapping
    // public String viewProfile(Principal p, Model model) {
    // model.addAttribute("user",
    // userService.findByEmail(p.getName()).orElseThrow());
    // return "shared/profile";
    // }
    @GetMapping
    public String viewProfile(Principal p, Model model) {
        User user = userService.findByEmail(p.getName()).orElseThrow();

        model.addAttribute("user", user);

        switch (user.getRole()) {
            case ADMIN -> model.addAttribute("dashboardPath", "/admin/dashboard");
            case MANAGER -> model.addAttribute("dashboardPath", "/manager/dashboard");
            case HR -> model.addAttribute("dashboardPath", "/hr/dashboard");
            case SUPERVISOR -> model.addAttribute("dashboardPath", "/supervisor/dashboard");
            case STAFF -> model.addAttribute("dashboardPath", "/staff/dashboard");
            default -> model.addAttribute("dashboardPath", "/dashboard");
        }

        return "shared/profile";
    }

    /**
     * Update name + phone only (no photo — photo has its own endpoint for instant
     * preview).
     */
    @PostMapping("/update")
    public String updateProfile(@RequestParam String name,
            @RequestParam(required = false) String phone,
            Principal p, RedirectAttributes ra) {
        User user = userService.findByEmail(p.getName()).orElseThrow();
        user.setName(name.trim().isEmpty() ? user.getName() : name.trim());
        user.setPhone(phone);
        userService.save(user);
        ra.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/profile";
    }

    /**
     * Dedicated endpoint for photo upload — called by the avatar form with instant
     * preview.
     */
    @PostMapping("/update-photo")
    public String updatePhoto(@RequestParam("profileImage") MultipartFile profileImage,
            Principal p, RedirectAttributes ra) {
        if (profileImage == null || profileImage.isEmpty()) {
            ra.addFlashAttribute("error", "No image selected.");
            return "redirect:/profile";
        }
        User user = userService.findByEmail(p.getName()).orElseThrow();
        try {
            // Delete old photo if present
            fileUploadUtil.deleteFile(user.getProfileImagePath());
            String path = fileUploadUtil.saveFile(profileImage, "users");
            user.setProfileImagePath(path);
            userService.save(user);
            ra.addFlashAttribute("success", "Profile photo updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to upload image: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    /** Remove the current profile photo. */
    @PostMapping("/remove-photo")
    public String removePhoto(Principal p, RedirectAttributes ra) {
        User user = userService.findByEmail(p.getName()).orElseThrow();
        fileUploadUtil.deleteFile(user.getProfileImagePath());
        user.setProfileImagePath(null);
        userService.save(user);
        ra.addFlashAttribute("success", "Profile photo removed.");
        return "redirect:/profile";
    }
}
