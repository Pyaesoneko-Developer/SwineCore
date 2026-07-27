package com.example.swinecore.controller;

import com.example.swinecore.entity.User;
import com.example.swinecore.service.PasswordResetService;
import com.example.swinecore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                               @RequestParam(required = false) String expired,
                               @RequestParam(required = false) String accountStatus,
                               @RequestParam(required = false) String reason,
                               @RequestParam(required = false) String registered,
                               Model model) {
        if (error != null) model.addAttribute("errorMsg", "Invalid email or password.");
        if (logout != null) model.addAttribute("successMsg", "You have been logged out.");
        if (expired != null) model.addAttribute("errorMsg", "Session expired. Please login again.");
        if (accountStatus != null) model.addAttribute("errorMsg",
            "Your account is " + accountStatus.toLowerCase() + ". Reason: " + (reason == null ? "Contact support." : reason));
        if (registered != null) model.addAttribute("successMsg", "Customer account created successfully. You can now sign in.");
        return "auth/login";
    }

    @GetMapping("/dashboard-redirect")
    public String dashboardRedirect(Authentication auth) {
        if (auth == null) return "redirect:/auth/login";
        for (var a : auth.getAuthorities()) {
            return switch (a.getAuthority()) {
                case "ROLE_ADMIN"      -> "redirect:/admin/dashboard";
                case "ROLE_MANAGER"    -> "redirect:/manager/dashboard";
                case "ROLE_HR"         -> "redirect:/hr/dashboard";
                case "ROLE_SUPERVISOR" -> "redirect:/supervisor/dashboard";
                case "ROLE_STAFF"      -> "redirect:/staff/dashboard";
                case "ROLE_CUSTOMER"   -> "redirect:/customer/dashboard";
                default                -> "redirect:/auth/login";
            };
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/change-password")
    public String changePasswordPage() {
        return "auth/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                  @RequestParam String newPassword,
                                  @RequestParam String confirmPassword,
                                  Principal principal,
                                  RedirectAttributes ra) {
        Optional<User> userOpt = userService.findByEmail(principal.getName());
        if (userOpt.isEmpty()) return "redirect:/auth/login";
        User user = userOpt.get();

        if (!userService.checkPassword(user, currentPassword)) {
            ra.addFlashAttribute("error", "Current password is incorrect.");
            return "redirect:/auth/change-password";
        }
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/auth/change-password";
        }
        userService.changePassword(user, newPassword);
        ra.addFlashAttribute("success", "Password changed successfully.");
        return "redirect:/dashboard";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, RedirectAttributes ra) {
        passwordResetService.initiateReset(email);
        ra.addFlashAttribute("success", "If that email exists, a reset link has been sent.");
        return "redirect:/auth/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes ra) {
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/auth/reset-password?token=" + token;
        }
        boolean ok = passwordResetService.resetPassword(token, newPassword);
        if (!ok) {
            ra.addFlashAttribute("error", "Invalid or expired token.");
            return "redirect:/auth/forgot-password";
        }
        ra.addFlashAttribute("success", "Password reset successfully. Please login.");
        return "redirect:/auth/login";
    }
}
