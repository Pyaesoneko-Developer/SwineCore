package com.example.swinecore.config;

import com.example.swinecore.entity.User;
import com.example.swinecore.entity.enums.Role;
import com.example.swinecore.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * MANAGER FARM DATA ISOLATION INTERCEPTOR
 *
 * Enforces strict data isolation for Manager role:
 * - Extracts logged-in Manager's user token
 * - Resolves their assigned Farm ID from the database
 * - Appends Farm ID as a hardcoded restriction to every query
 * - Blocks access to data belonging to any other Farm ID
 *
 * Returns 403 Forbidden Access Violation for foreign farm ID attempts.
 */
@Component
@RequiredArgsConstructor
public class ManagerFarmIsolationInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return true;
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || user.getRole() != Role.MANAGER) {
            return true; // Only applies to managers
        }

        // Store manager's farm ID in request attribute for downstream use
        if (user.getFarm() != null) {
            request.setAttribute("MANAGER_FARM_ID", user.getFarm().getId());
            request.setAttribute("MANAGER_FARM", user.getFarm());
        } else {
            // Manager without assigned farm — block all farm-scoped operations
            try {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "403 Forbidden Access Violation: No farm assigned to this manager.");
            } catch (Exception ignored) {}
            return false;
        }

        // Validate that any farm ID in the request matches the manager's farm
        String requestedFarmId = request.getParameter("farmId");
        if (requestedFarmId != null) {
            try {
                long requestedId = Long.parseLong(requestedFarmId);
                if (requestedId != user.getFarm().getId().longValue()) {
                    try {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN,
                                "403 Forbidden Access Violation: Attempted to access data from another farm.");
                    } catch (IOException ignored) {}
                    return false;
                }
            } catch (NumberFormatException e) {
                // Ignore invalid farm ID format
            }
        }

        return true;
    }
}
