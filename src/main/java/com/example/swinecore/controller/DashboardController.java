package com.example.swinecore.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping({"/", "/home"})
    public String home(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return "redirect:/marketplace";
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
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
}
