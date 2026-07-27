package com.example.swinecore.repository;

import com.example.swinecore.entity.Farm;
import com.example.swinecore.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    List<Shift> findByFarmAndActiveTrue(Farm farm);

    List<Shift> findByFarm(Farm farm);
}
