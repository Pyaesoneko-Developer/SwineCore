package com.example.swinecore.service;

import com.example.swinecore.entity.Farm;
import com.example.swinecore.repository.BuildingRepository;
import com.example.swinecore.repository.FarmRepository;
import com.example.swinecore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FarmService {

    private final FarmRepository farmRepository;
    private final BuildingRepository buildingRepository;
    private final UserRepository userRepository;

    public Farm create(Farm farm) {
        farm.setCode(nextFarmCode());
        if (farmRepository.existsByName(farm.getName()))
            throw new IllegalArgumentException("Farm name already in use: " + farm.getName());
        return farmRepository.save(farm);
    }

    private synchronized String nextFarmCode() {
        for (int number = 1; number <= 999; number++) {
            String code = "F" + String.format("%03d", number);
            if (!farmRepository.existsByCode(code)) return code;
        }
        throw new IllegalStateException("Farm code capacity has been reached.");
    }

    public Farm save(Farm farm) {
        return farmRepository.save(farm);
    }

    public Optional<Farm> findById(Long id) {
        return farmRepository.findById(id);
    }

    public List<Farm> findAll() {
        return farmRepository.findAll();
    }

    public void delete(Long id, String confirmedName) {
        Farm farm = farmRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Farm not found"));
        if (!farm.getName().equalsIgnoreCase(confirmedName))
            throw new IllegalArgumentException("Name confirmation does not match. Deletion aborted.");
        farmRepository.delete(farm);
    }

    public long countBuildings(Farm farm) {
        return buildingRepository.countByFarm(farm);
    }

    /** Blueprint guardrail: Farm must have at least one building with a room. */
    public boolean hasMinimumArchitecture(Farm farm) {
        long buildingCount = buildingRepository.countByFarm(farm);
        if (buildingCount == 0) return false;
        return buildingRepository.findByFarmWithFarm(farm).stream()
            .anyMatch(b -> b.getRooms() != null && !b.getRooms().isEmpty());
    }
}
