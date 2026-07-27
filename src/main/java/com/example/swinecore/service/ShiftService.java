package com.example.swinecore.service;

import com.example.swinecore.entity.Farm;
import com.example.swinecore.entity.Shift;
import com.example.swinecore.entity.StaffShift;
import com.example.swinecore.entity.User;
import com.example.swinecore.repository.ShiftRepository;
import com.example.swinecore.repository.StaffShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final StaffShiftRepository staffShiftRepository;

    public Shift createShift(Shift shift) {
        return shiftRepository.save(shift);
    }

    public void saveShift(Shift shift) {
        shiftRepository.save(shift);
    }

    public void deleteShift(Long id) {
        shiftRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Shift> findByFarm(Farm farm) {
        return shiftRepository.findByFarmAndActiveTrue(farm);
    }

    @Transactional(readOnly = true)
    public Optional<Shift> findShiftById(Long id) {
        return shiftRepository.findById(id);
    }

    public StaffShift assignStaffToShift(User user, Shift shift, LocalDate effectiveDate) {
        Optional<StaffShift> existing = staffShiftRepository.findByUserAndStatus(user, StaffShift.ShiftStatus.ACTIVE);
        if (existing.isPresent()) {
            StaffShift ss = existing.get();
            ss.setStatus(StaffShift.ShiftStatus.REST);
            ss.setEndDate(effectiveDate.minusDays(1));
            staffShiftRepository.save(ss);
        }
        StaffShift staffShift = StaffShift.builder()
            .user(user)
            .shift(shift)
            .effectiveDate(effectiveDate)
            .status(StaffShift.ShiftStatus.ACTIVE)
            .build();
        return staffShiftRepository.save(staffShift);
    }

    public void markNightShiftRest(User user) {
        staffShiftRepository.findByUserAndStatus(user, StaffShift.ShiftStatus.ACTIVE)
            .ifPresent(ss -> {
                ss.setStatus(StaffShift.ShiftStatus.NIGHT_SHIFT_REST);
                staffShiftRepository.save(ss);
            });
    }

    @Transactional(readOnly = true)
    public Optional<StaffShift> findActiveShift(User user) {
        return staffShiftRepository.findByUserAndStatus(user, StaffShift.ShiftStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<User> findActiveUsersForShift(Shift shift) {
        return staffShiftRepository.findByStatus(StaffShift.ShiftStatus.ACTIVE).stream()
            .filter(ss -> ss.getShift() != null && ss.getShift().getId().equals(shift.getId()))
            .map(StaffShift::getUser)
            .toList();
    }

    public void checkEarlyDeparture(User user, java.time.LocalDateTime clockOutTime) {
        staffShiftRepository.findByUserAndStatus(user, StaffShift.ShiftStatus.ACTIVE)
            .ifPresent(ss -> {
                Shift shift = ss.getShift();
                if (shift == null) return;
                java.time.Duration worked = java.time.Duration.between(ss.getEffectiveDate().atStartOfDay(), clockOutTime.toLocalDate().atStartOfDay());
                if (worked.toHours() < shift.getMinimumRequiredHours()) {
                    throw new EarlyDepartureException("Early departure detected. 50% wage deduction applied.");
                }
            });
    }

    public static class EarlyDepartureException extends RuntimeException {
        public EarlyDepartureException(String message) { super(message); }
    }
}
