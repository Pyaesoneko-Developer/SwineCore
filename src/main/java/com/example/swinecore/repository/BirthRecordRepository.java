package com.example.swinecore.repository;

import com.example.swinecore.entity.BirthRecord;
import com.example.swinecore.entity.Building;
import com.example.swinecore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BirthRecordRepository extends JpaRepository<BirthRecord, Long> {
    List<BirthRecord> findByBuilding(Building building);
    List<BirthRecord> findByRecordedBy(User staff);
    List<BirthRecord> findByBuildingOrderByCreatedAtDesc(Building building);
}
