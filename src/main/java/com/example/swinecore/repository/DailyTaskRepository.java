package com.example.swinecore.repository;

import com.example.swinecore.entity.Building;
import com.example.swinecore.entity.DailyTask;
import com.example.swinecore.entity.Farm;
import com.example.swinecore.entity.User;
import com.example.swinecore.entity.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyTaskRepository extends JpaRepository<DailyTask, Long> {

        List<DailyTask> findByAssignedStaffAndTaskDate(User staff, LocalDate date);

        List<DailyTask> findByBuildingAndTaskDate(Building building, LocalDate date);

        List<DailyTask> findByBuildingAndTaskDateAndStatus(
                        Building building,
                        LocalDate date,
                        TaskStatus status);

        List<DailyTask> findByBuildingAndStatus(
                        Building building,
                        TaskStatus status);

        List<DailyTask> findByAssignedStaffAndTaskDateAndStatus(
                        User staff,
                        LocalDate date,
                        TaskStatus status);

        long countByBuildingAndTaskDateAndIsStandardTaskFalse(
                        Building building,
                        LocalDate date);

        /*
         * Staff tasks page:
         * Today + next 2 days upcoming tasks.
         */
        List<DailyTask> findByAssignedStaffAndTaskDateBetweenOrderByTaskDateAscIdAsc(
                        User assignedStaff,
                        LocalDate startDate,
                        LocalDate endDate);

        /*
         * Manager daily tasks page:
         * Farm tasks for today + next 2 days.
         *
         * Relation path:
         * DailyTask -> assignedStaff -> building -> farm
         */
        List<DailyTask> findByAssignedStaff_Building_FarmAndTaskDateBetweenOrderByTaskDateAscIdAsc(
                        Farm farm,
                        LocalDate startDate,
                        LocalDate endDate);

        /*
         * Staff task history:
         * SUBMITTED / APPROVED / REJECTED
         * Latest submitted task first.
         */
        List<DailyTask> findByAssignedStaffAndStatusInOrderBySubmittedAtDescIdDesc(
                        User assignedStaff,
                        List<TaskStatus> statuses);
}