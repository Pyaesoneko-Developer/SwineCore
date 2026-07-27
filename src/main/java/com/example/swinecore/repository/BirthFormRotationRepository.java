package com.example.swinecore.repository;

import com.example.swinecore.entity.BirthFormRotation;
import com.example.swinecore.entity.Building;
import com.example.swinecore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BirthFormRotationRepository extends JpaRepository<BirthFormRotation, Long> {
    Optional<BirthFormRotation> findByBuilding(Building building);
    Optional<BirthFormRotation> findByCurrentStaff(User staff);
}
