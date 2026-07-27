package com.example.swinecore.config;

import com.example.swinecore.service.CustomerService;
import com.example.swinecore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

/**
 * Injects the current user's profile image path and display initials into every view model.
 * Works for both internal User accounts and CustomerAccount logins.
 *
 * Single DB lookup per request: tries User table first; falls back to CustomerAccount only
 * when no matching User is found (e.g. customer sessions). At most 2 queries total.
 *
 * Templates may reference:
 *   ${currentProfileImage}  — relative path to profile photo, or null if none set
 *   ${currentUserInitials}  — 1–2 uppercase letter abbreviation (e.g. "KZ" for Kyaw Zin)
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final UserService userService;
    private final CustomerService customerService;

    @ModelAttribute
    public void injectCurrentUserAttributes(Principal principal, Model model) {
        if (principal == null) {
            model.addAttribute("currentProfileImage", null);
            model.addAttribute("currentUserInitials", "?");
            return;
        }

        String email = principal.getName();

        // Single lookup — try User first, then Customer
        String[] attrs = userService.findByEmail(email)
            .map(u -> new String[]{
                u.getProfileImagePath(),
                u.getInitials()
            })
            .orElseGet(() -> customerService.findByEmail(email)
                .map(c -> new String[]{
                    c.getProfileImagePath(),
                    c.getInitials()
                })
                .orElse(new String[]{null, "?"}));

        model.addAttribute("currentProfileImage", attrs[0]);
        model.addAttribute("currentUserInitials", attrs[1]);
    }
}
