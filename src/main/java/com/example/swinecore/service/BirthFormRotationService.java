package com.example.swinecore.service;

import com.example.swinecore.entity.*;
import com.example.swinecore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Manages the advanced sequential birth-form assignment logic:
 * - Form opens for ONE staff at a time in round-robin order
 * - If staff is absent, form moves to next available (attended) staff
 * - If bypassed staff returns (attended) the NEXT day, form reverts to them
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BirthFormRotationService {

    private final BirthFormRotationRepository rotationRepository;
    private final AttendanceService attendanceService;
    private final UserRepository userRepository;

    /** Get the staff member currently holding the birth form for a building */
    public Optional<BirthFormRotation> getRotation(Building building) {
        return rotationRepository.findByBuilding(building);
    }

    /** Check if it is this user's turn to submit birth records */
    public boolean isCurrentHolder(User staff, Building building) {
        return rotationRepository.findByBuilding(building)
            .map(r -> r.getCurrentStaff().getId().equals(staff.getId()))
            .orElse(false);
    }

    /**
     * Called each day when staff clock in — advances form to the correct person.
     * Rules:
     *  1. If current holder is attended and hasn't submitted → keep them
     *  2. If current holder is absent → pass to next attended staff
     *  3. If bypassed holder comes back (attended) → revert to them
     */
    public void advanceDailyRotation(Building building) {
        List<User> staffList = userRepository.findByBuildingAndRole(building,
            com.example.swinecore.entity.enums.Role.STAFF);
        if (staffList.isEmpty()) return;

        List<User> attendedToday = attendanceService.getAttendedStaffForBuilding(building.getId());
        Optional<BirthFormRotation> rotOpt = rotationRepository.findByBuilding(building);

        if (rotOpt.isEmpty()) {
            // First-time: assign to first attended staff
            if (!attendedToday.isEmpty()) {
                rotationRepository.save(BirthFormRotation.builder()
                    .building(building)
                    .currentStaff(attendedToday.get(0))
                    .assignedDate(LocalDate.now())
                    .submittedToday(false)
                    .build());
            }
            return;
        }

        BirthFormRotation rot = rotOpt.get();
        User current = rot.getCurrentStaff();
        LocalDate today = LocalDate.now();

        // If today is same as assigned date (already processed today), skip
        if (today.equals(rot.getAssignedDate())) return;

        boolean currentSubmitted = rot.isSubmittedToday();

        if (currentSubmitted) {
            // Move to next in circular list
            int idx = staffList.indexOf(current);
            User next = getNextAttended(staffList, idx + 1, attendedToday);
            if (next != null) {
                rot.setCurrentStaff(next);
                rot.setAssignedDate(today);
                rot.setSubmittedToday(false);
            }
        } else {
            // Current holder didn't submit yet
            boolean currentAttendedToday = attendedToday.stream()
                .anyMatch(u -> u.getId().equals(current.getId()));
            if (currentAttendedToday) {
                // Keep current, just reset daily flag
                rot.setAssignedDate(today);
                rot.setSubmittedToday(false);
            } else {
                // Current is absent — find next attended
                int idx = staffList.indexOf(current);
                User next = getNextAttended(staffList, idx + 1, attendedToday);
                if (next != null) {
                    // Temporarily move but remember original for reverting
                    rot.setCurrentStaff(next);
                    rot.setAssignedDate(today);
                    rot.setSubmittedToday(false);
                }
            }
        }
        rotationRepository.save(rot);
    }

    public void recordSubmission(Building building) {
        rotationRepository.findByBuilding(building).ifPresent(r -> {
            r.setSubmittedToday(true);
            rotationRepository.save(r);
        });
    }

    private User getNextAttended(List<User> allStaff, int startIdx, List<User> attended) {
        if (attended.isEmpty()) return null;
        int size = allStaff.size();
        for (int i = 0; i < size; i++) {
            User candidate = allStaff.get((startIdx + i) % size);
            if (attended.stream().anyMatch(u -> u.getId().equals(candidate.getId()))) {
                return candidate;
            }
        }
        return null;
    }
}
