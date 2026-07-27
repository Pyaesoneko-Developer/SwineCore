package com.example.swinecore.repository;

import com.example.swinecore.entity.Attendance;
import com.example.swinecore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByUserAndWorkDate(User user, LocalDate workDate);

    @Query("SELECT a FROM Attendance a WHERE a.user.building.id = :buildingId AND a.workDate = :date AND a.attended = true AND a.clockedOut = false")
    List<Attendance> findActiveAttendanceByBuildingAndDate(@Param("buildingId") Long buildingId,
                                                           @Param("date") LocalDate date);

    List<Attendance> findByUserAndWorkDateBetween(User user, LocalDate start, LocalDate end);

    @Query("SELECT a FROM Attendance a WHERE (a.user.farm.id = :farmId OR a.user.building.farm.id = :farmId) AND a.user.role = 'SUPERVISOR' AND a.workDate = :date AND a.status = 'EARLY_DEPARTURE' AND a.clockedOut = false")
    List<Attendance> findPendingSupervisorLeaveByFarm(@Param("farmId") Long farmId, @Param("date") LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.user.building.id = :buildingId AND a.user.role = 'STAFF' AND a.workDate = :date AND a.status = 'EARLY_DEPARTURE' AND a.clockedOut = false")
    List<Attendance> findPendingStaffLeaveByBuilding(@Param("buildingId") Long buildingId, @Param("date") LocalDate date);

    @Query("SELECT a.user FROM Attendance a WHERE a.user.building.id = :buildingId AND a.workDate = :date AND a.attended = true AND a.user.role = 'STAFF'")
    List<User> findAttendedStaffByBuildingAndDate(@Param("buildingId") Long buildingId,
                                                   @Param("date") LocalDate date);
}
