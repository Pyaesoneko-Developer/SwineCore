package com.example.swinecore.repository;

import com.example.swinecore.entity.StaffShift;
import com.example.swinecore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffShiftRepository extends JpaRepository<StaffShift, Long> {

    Optional<StaffShift> findByUserAndStatus(User user, StaffShift.ShiftStatus status);

    @Query("SELECT ss FROM StaffShift ss WHERE ss.user = :user AND ss.effectiveDate <= :date AND (ss.endDate IS NULL OR ss.endDate >= :date) ORDER BY ss.effectiveDate DESC")
    List<StaffShift> findActiveByUserOnDate(@Param("user") User user, @Param("date") LocalDate date);

    List<StaffShift> findByStatus(StaffShift.ShiftStatus status);
}
