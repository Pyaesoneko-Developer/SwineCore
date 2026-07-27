package com.example.swinecore.repository;

import com.example.swinecore.entity.Building;
import com.example.swinecore.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByBuilding(Building building);

    @Query("SELECT r FROM Room r LEFT JOIN FETCH r.building WHERE r.building = :building AND r.active = true ORDER BY r.name")
    List<Room> findActiveByBuildingWithBuilding(@Param("building") Building building);

    Optional<Room> findByBuildingAndCode(Building building, String code);

    boolean existsByBuildingAndCode(Building building, String code);
    boolean existsByCode(String code);
}
