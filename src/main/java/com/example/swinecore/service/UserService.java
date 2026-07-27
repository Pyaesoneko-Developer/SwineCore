package com.example.swinecore.service;

import com.example.swinecore.entity.Building;
import com.example.swinecore.entity.Farm;
import com.example.swinecore.entity.User;
import com.example.swinecore.entity.enums.Role;
import com.example.swinecore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** Only the seeded admin (admin@swinecore.com) may hold the ADMIN role. */
    public static final String ADMIN_SEEDED_EMAIL = "admin@swinecore.com";

    /** Strong password pattern: min 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special char. */
    private static final java.util.regex.Pattern STRONG_PASSWORD =
        java.util.regex.Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$");

    public static void validateStrongPassword(String password) {
        if (password == null || !STRONG_PASSWORD.matcher(password).matches())
            throw new IllegalArgumentException(
                "Password must be at least 8 characters and contain at least 1 uppercase letter, " +
                "1 lowercase letter, 1 number, and 1 special character (@$!%*?&#).");
    }

    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new IllegalArgumentException("Email already in use: " + user.getEmail());
        if (user.getRole() == Role.ADMIN)
            throw new IllegalArgumentException("Admin accounts cannot be created through this form.");
        if (user.getRole() == Role.CUSTOMER)
            throw new IllegalArgumentException("Customer accounts are managed through self-registration only.");
        if (user.getPassword() == null || user.getPassword().isBlank())
            throw new IllegalArgumentException("Password is required.");
        if (user.getRole() == Role.MANAGER) {
            if (user.getFarm() == null || user.getFarm().getId() == null)
                throw new IllegalArgumentException("A farm assignment is required for a manager.");
            if (farmHasActiveManager(user.getFarm()))
                throw new IllegalStateException("This farm already has an active manager.");
            user.setBuilding(null);
        }
        if (user.getRole() == Role.STAFF || user.getRole() == Role.SUPERVISOR) {
            if (user.getFarm() == null || user.getBuilding() == null)
                throw new IllegalArgumentException("Farm and building assignments are required.");
            if (!user.getBuilding().getFarm().getId().equals(user.getFarm().getId()))
                throw new IllegalArgumentException("The selected building does not belong to the selected farm.");
            if (user.getRole() == Role.SUPERVISOR && buildingHasActiveSupervisor(user.getBuilding()))
                throw new IllegalStateException("This building already has an active supervisor.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setMustChangePassword(true);
        user.setStartDate(LocalDate.now());
        clearFarmScopingForGlobalRoles(user);
        return userRepository.save(user);
    }

    public User save(User user) {
        if (user.getRole() == Role.ADMIN && !ADMIN_SEEDED_EMAIL.equalsIgnoreCase(user.getEmail()))
            throw new IllegalArgumentException("Only the system admin account may have the ADMIN role.");
        clearFarmScopingForGlobalRoles(user);
        return userRepository.save(user);
    }

    /** Returns all roles except ADMIN and CUSTOMER — used when rendering user forms. */
    public List<Role> getAssignableRoles() {
        return java.util.Arrays.stream(Role.values())
                .filter(r -> r != Role.ADMIN && r != Role.CUSTOMER)
                .toList();
    }

    /** Returns all users except the seeded admin — used in the user list. */
    @Transactional(readOnly = true)
    public List<User> findAllNonAdmin() {
        return userRepository.findAllWithAssociations().stream()
                .filter(u -> u.getRole() != Role.ADMIN)
                .toList();
    }

    /** Returns users by role (excluding ADMIN) — used in filtered user list. */
    @Transactional(readOnly = true)
    public List<User> findByRoleNonAdmin(Role role) {
        if (role == Role.ADMIN) return List.of();
        return userRepository.findByRoleWithAssociations(role);
    }

    /**
     * ADMIN and HR span all farms; CUSTOMER accounts are not farm-based.
     * Enforce this invariant at the service layer so no controller or
     * tampered HTTP request can leave these roles in a farm-scoped state.
     */
    private void clearFarmScopingForGlobalRoles(User user) {
        if (user.getRole() == Role.ADMIN
                || user.getRole() == Role.HR
                || user.getRole() == Role.CUSTOMER) {
            user.setFarm(null);
            user.setBuilding(null);
        }
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Returns all users with farm and building eagerly loaded.
     * Use this for controller/template rendering to avoid LazyInitializationException.
     */
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAllWithAssociations();
    }

    /**
     * Returns users filtered by role with associations eagerly loaded.
     */
    @Transactional(readOnly = true)
    public List<User> findByRole(Role role) {
        return userRepository.findByRoleWithAssociations(role);
    }

    @Transactional(readOnly = true)
    public List<User> findByFarm(Farm farm) {
        return userRepository.findByFarmWithAssociations(farm);
    }

    @Transactional(readOnly = true)
    public List<User> findByFarmAndRole(Farm farm, Role role) {
        return userRepository.findByFarmAndRole(farm, role);
    }

    @Transactional(readOnly = true)
    public List<User> findByBuilding(Building building) {
        return userRepository.findByBuildingWithAssociations(building);
    }

    @Transactional(readOnly = true)
    public boolean farmHasActiveManager(Farm farm) {
        return userRepository.findByFarmAndRole(farm, Role.MANAGER).stream().anyMatch(User::isEnabled);
    }

    @Transactional(readOnly = true)
    public boolean buildingHasActiveSupervisor(Building building) {
        return userRepository.findByBuildingAndRole(building, Role.SUPERVISOR).stream().anyMatch(User::isEnabled);
    }

    /** Soft-delete: disable account but preserve data */
    public void disable(Long id) {
        userRepository.findById(id).ifPresent(u -> {
            u.setEnabled(false);
            u.setBuilding(null);
            u.setFarm(null);
            userRepository.save(u);
        });
    }

    public void hardDelete(Long id) {
        userRepository.deleteById(id);
    }

    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);
        userRepository.save(user);
    }

    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public void assignToFarm(Long userId, Farm farm) {
        userRepository.findById(userId).ifPresent(u -> {
            // Enforce single-manager-per-farm rule
            if (u.getRole() == Role.MANAGER) {
                boolean farmHasManager = userRepository.findByFarmAndRole(farm, Role.MANAGER)
                    .stream().anyMatch(m -> !m.getId().equals(userId));
                if (farmHasManager)
                    throw new IllegalStateException("Farm already has an active manager.");
            }
            u.setFarm(farm);
            u.setStartDate(LocalDate.now());
            userRepository.save(u);
        });
    }

    public void assignToBuilding(Long userId, Building building) {
        userRepository.findById(userId).ifPresent(u -> {
            // Enforce single-supervisor-per-building rule
            if (u.getRole() == Role.SUPERVISOR) {
                boolean bldHasSupervisor = userRepository.findByBuildingAndRole(building, Role.SUPERVISOR)
                    .stream().anyMatch(s -> !s.getId().equals(userId));
                if (bldHasSupervisor)
                    throw new IllegalStateException("Building already has an active supervisor.");
            }
            u.setBuilding(building);
            u.setFarm(building.getFarm());
            u.setStartDate(LocalDate.now());
            userRepository.save(u);
        });
    }
}
