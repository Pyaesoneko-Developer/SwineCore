package com.example.swinecore.service;

import com.example.swinecore.entity.Attendance;
import com.example.swinecore.entity.User;
import com.example.swinecore.entity.enums.Role;

import com.example.swinecore.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final com.example.swinecore.repository.StaffShiftRepository staffShiftRepository;

    /** Staff / Supervisor clicks "Attended" */
    public Attendance clockIn(User user) {
        LocalDate today = LocalDate.now();
        Optional<Attendance> existing = attendanceRepository.findByUserAndWorkDate(user, today);
        if (existing.isPresent() && existing.get().isAttended()) {
            throw new IllegalStateException("Already clocked in for today.");
        }
        Attendance att = existing.orElse(Attendance.builder()
            .user(user).workDate(today).build());
        att.setAttended(true);
        att.setClockInTime(LocalDateTime.now());
        att.setClockedOut(false);
        att.setStatus(Attendance.AttendanceStatus.NORMAL);
        return attendanceRepository.save(att);
    }

    /** Staff / Supervisor clicks "Clock Out" — detects early departure. */
    public Attendance requestClockOut(User user, String reason) {
        LocalDate today = LocalDate.now();
        Attendance att = attendanceRepository.findByUserAndWorkDate(user, today)
            .orElseThrow(() -> new IllegalStateException("Not clocked in today."));
        if (!att.isAttended())
            throw new IllegalStateException("Must clock in before clocking out.");

        LocalDateTime now = LocalDateTime.now();
        att.setClockedOut(true);
        att.setClockOutTime(now);

        // Check for early departure against assigned shift
        staffShiftRepository.findByUserAndStatus(
                user, com.example.swinecore.entity.StaffShift.ShiftStatus.ACTIVE)
            .ifPresent(staffShift -> {
                if (staffShift.getShift() == null) return;
                var shift = staffShift.getShift();
                LocalTime shiftEnd = shift.getStandardEnd();
                double minimumHours = shift.getMinimumRequiredHours();

                // Calculate hours worked
                Duration worked = Duration.between(att.getClockInTime(), now);
                double hoursWorked = worked.toMinutes() / 60.0;

                // Check if clocked out before shift end or before minimum hours
                boolean beforeShiftEnd = now.toLocalTime().isBefore(shiftEnd);
                boolean belowMinimum = hoursWorked < minimumHours;

                if (beforeShiftEnd || belowMinimum) {
                    att.setStatus(Attendance.AttendanceStatus.EARLY_DEPARTURE);
                    // 50% wage deduction — store the deduction percentage
                    att.setWageDeductionAmount(hoursWorked); // hours worked for calculation
                }
            });

        if (att.getStatus() == Attendance.AttendanceStatus.EARLY_DEPARTURE) {
            if (reason == null || reason.isBlank())
                throw new IllegalArgumentException("A reason is required for early clock-out.");
            if (reason.trim().length() > 240)
                throw new IllegalArgumentException("Reason must be 240 characters or fewer.");
            att.setEarlyDepartureReason(reason.trim());
            att.setClockedOut(false);
            att.setClockOutTime(null);
        }
        return attendanceRepository.save(att);
    }

    public Attendance clockOut(User user) { return requestClockOut(user, ""); }

    public boolean requiresEarlyApproval(User user) {
        Attendance att = attendanceRepository.findByUserAndWorkDate(user, LocalDate.now()).orElse(null);
        if (att == null || !att.isAttended() || att.isClockedOut()) return false;
        LocalDateTime now = LocalDateTime.now();
        return staffShiftRepository.findByUserAndStatus(user, com.example.swinecore.entity.StaffShift.ShiftStatus.ACTIVE)
            .filter(ss -> ss.getShift() != null)
            .map(ss -> now.toLocalTime().isBefore(ss.getShift().getStandardEnd()) ||
                Duration.between(att.getClockInTime(), now).toMinutes() / 60.0 < ss.getShift().getMinimumRequiredHours())
            .orElse(false);
    }

    public boolean hasPendingClockOut(User user) {
        return attendanceRepository.findByUserAndWorkDate(user, LocalDate.now())
            .map(a -> a.getStatus() == Attendance.AttendanceStatus.EARLY_DEPARTURE && !a.isClockedOut())
            .orElse(false);
    }

    public List<Attendance> pendingForManager(User manager) {
        if (manager.getRole() != Role.MANAGER || manager.getFarm() == null) return List.of();
        return attendanceRepository.findPendingSupervisorLeaveByFarm(manager.getFarm().getId(), LocalDate.now());
    }

    public List<Attendance> pendingForSupervisor(User supervisor) {
        if (supervisor.getRole() != Role.SUPERVISOR || supervisor.getBuilding() == null) return List.of();
        return attendanceRepository.findPendingStaffLeaveByBuilding(supervisor.getBuilding().getId(), LocalDate.now());
    }

    public Attendance approveClockOut(Long attendanceId, User approver) {
        Attendance att = attendanceRepository.findById(attendanceId).orElseThrow();
        if (att.getStatus() != Attendance.AttendanceStatus.EARLY_DEPARTURE || att.isClockedOut())
            throw new IllegalStateException("This request is no longer pending.");
        User requester = att.getUser();
        boolean allowed = requester.getRole() == Role.STAFF && approver.getRole() == Role.SUPERVISOR &&
            requester.getBuilding() != null && approver.getBuilding() != null && requester.getBuilding().getId().equals(approver.getBuilding().getId());
        Long requesterFarmId = requester.getFarm() != null ? requester.getFarm().getId() :
            requester.getBuilding() != null && requester.getBuilding().getFarm() != null ? requester.getBuilding().getFarm().getId() : null;
        allowed = allowed || requester.getRole() == Role.SUPERVISOR && approver.getRole() == Role.MANAGER &&
            requesterFarmId != null && approver.getFarm() != null && requesterFarmId.equals(approver.getFarm().getId());
        if (!allowed) throw new SecurityException("Clock-out request is outside your approval scope.");
        att.setClockedOut(true);
        att.setClockOutTime(LocalDateTime.now());
        att.setStatus(Attendance.AttendanceStatus.EXCUSED_EARLY_LEAVE);
        return attendanceRepository.save(att);
    }

    public boolean isClockedIn(User user) {
        return attendanceRepository.findByUserAndWorkDate(user, LocalDate.now())
            .map(a -> a.isAttended() && !a.isClockedOut())
            .orElse(false);
    }

    public boolean isClockedOut(User user) {
        return attendanceRepository.findByUserAndWorkDate(user, LocalDate.now())
            .map(Attendance::isClockedOut)
            .orElse(false);
    }

    public Optional<Attendance> getTodayAttendance(User user) {
        return attendanceRepository.findByUserAndWorkDate(user, LocalDate.now());
    }

    public Attendance save(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public List<User> getAttendedStaffForBuilding(Long buildingId) {
        return attendanceRepository.findAttendedStaffByBuildingAndDate(buildingId, LocalDate.now());
    }

    public List<Attendance> getUserHistory(User user, LocalDate from, LocalDate to) {
        return attendanceRepository.findByUserAndWorkDateBetween(user, from, to);
    }
}
