package com.example.swinecore.repository;

import com.example.swinecore.entity.Farm;
import com.example.swinecore.entity.FarmAd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmAdRepository extends JpaRepository<FarmAd, Long> {
    List<FarmAd> findByActiveTrue();
    List<FarmAd> findByFarm(Farm farm);
}
