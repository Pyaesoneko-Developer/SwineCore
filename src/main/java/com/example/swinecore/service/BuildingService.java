package com.example.swinecore.service;

import com.example.swinecore.entity.Building;
import com.example.swinecore.entity.Farm;
import com.example.swinecore.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BuildingService {

    private final BuildingRepository buildingRepository;

    public Building create(Building building) {
        building.setCode(nextBuildingCode());
        return buildingRepository.save(building);
    }

    private synchronized String nextBuildingCode() {
        for (int number = 1; number <= 999; number++) {
            String code = "B" + String.format("%03d", number);
            if (!buildingRepository.existsByCode(code)) return code;
        }
        throw new IllegalStateException("Building code capacity has been reached.");
    }

    public Building save(Building building) {
        return buildingRepository.save(building);
    }

    public Optional<Building> findById(Long id) {
        return buildingRepository.findById(id);
    }

    /**
     * Returns buildings in a farm with their farm association eagerly loaded.
     * Staff and pig collections remain lazy — they are safely loaded by
     * open-in-view=true during Thymeleaf rendering.
     */
    @Transactional(readOnly = true)
    public List<Building> findByFarm(Farm farm) {
        return buildingRepository.findByFarmWithFarm(farm);
    }

    @Transactional(readOnly = true)
    public List<Building> findAllWithFarm() {
        return buildingRepository.findAllWithFarm();
    }

    public void delete(Long id) {
        buildingRepository.deleteById(id);
    }
}
