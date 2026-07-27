package com.example.swinecore.service.impl;

import com.example.swinecore.entity.*;
import com.example.swinecore.entity.enums.PigGender;
import com.example.swinecore.entity.enums.PigStatus;
import com.example.swinecore.repository.PigRepository;
import com.example.swinecore.repository.BirthRecordRepository;
import com.example.swinecore.util.PigCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * SMART GENETIC HYBRIDIZATION ENGINE
 *
 * Computes offspring genetic codes upon birth registration:
 * - Pure Matching: Y + Y = Y
 * - Cross-Breed: Y + L = YL
 * - Three-Way Cross: D + YL = DYL
 *
 * Generates piglet identity codes:
 * [Farm ID]-[Building ID]-[Calculated Hybrid Genetics]-[Last 2 Digits of Mother Sow Code]-[Record Date]-[Seq]
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BreedingServiceImpl {

    private final PigRepository pigRepository;
    private final BirthRecordRepository birthRecordRepository;
    private final PigCodeGenerator codeGenerator;

    /**
     * Compute hybrid genetics code from father and mother genetics.
     * Pure matching: single character
     * Cross-breed: sequential combination
     * Three-way: preserves all lineages
     */
    public String computeHybridGenetics(Genetics fatherGenetics, Genetics motherGenetics) {
        String fatherCode = fatherGenetics.getCode().trim().toUpperCase();
        String motherCode = motherGenetics.getCode().trim().toUpperCase();

        if (fatherCode.equals(motherCode)) {
            // Pure Matching Mating: [Y] + [Y] = [Y]
            return fatherCode;
        }

        // Cross-Breed / Three-Way: combine preserving all lineages
        Set<String> combined = new TreeSet<>();
        for (char c : fatherCode.toCharArray()) {
            combined.add(String.valueOf(c));
        }
        for (char c : motherCode.toCharArray()) {
            combined.add(String.valueOf(c));
        }
        return String.join("", combined);
    }

    /**
     * Compute hybrid genetics from a mother pig and a father genetics code.
     * Used when father is unknown but genetics are specified.
     */
    public String computeHybridGenetics(Genetics fatherGenetics, Pig mother) {
        if (mother.getGenetics() == null) {
            return fatherGenetics.getCode().trim().toUpperCase();
        }
        return computeHybridGenetics(fatherGenetics, mother.getGenetics());
    }

    /**
     * Generate piglet codes using the smart hybridization engine.
     * Format: [FarmCode][BuildCode][HybridGenetics][MotherSuffix2][RecordDate(ddMMyyyy)][SeqNum(01)]
     */
    public List<Pig> generatePiglets(Farm farm, Building building, Genetics fatherGenetics,
                                      Pig mother, LocalDate birthDate, int aliveCount, int deadCount,
                                      User recordedBy) {
        if (farm == null || building == null || mother == null || fatherGenetics == null) {
            throw new IllegalArgumentException("Farm, building, mother sow and father genetics are required.");
        }
        if (mother.getBuilding() == null || !building.getId().equals(mother.getBuilding().getId())) {
            throw new IllegalArgumentException("The selected mother sow does not belong to your building.");
        }
        if (!isEligibleMotherSow(mother)) {
            throw new IllegalArgumentException("The selected sow is not eligible for birth recording.");
        }
        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future.");
        }
        if (aliveCount < 0 || deadCount < 0 || aliveCount + deadCount < 1) {
            throw new IllegalArgumentException("Enter at least one alive or dead piglet.");
        }

        BirthRecord birthRecord = BirthRecord.builder()
                .mother(mother)
                .building(building)
                .recordedBy(recordedBy)
                .birthDate(birthDate)
                .litterSize(aliveCount + deadCount)
                .alivePiglets(aliveCount)
                .deadPiglets(deadCount)
                .confirmedByStaff(true)
                .confirmedBySupervisor(false)
                .build();
        birthRecord = birthRecordRepository.save(birthRecord);

        String hybridGenetics = computeHybridGenetics(fatherGenetics, mother);
        List<String> codes = codeGenerator.generateLitterCodes(
                farm, building, createTemporaryGenetics(hybridGenetics),
                mother, LocalDate.now(), aliveCount);

        List<Pig> piglets = new ArrayList<>();
        for (int i = 0; i < aliveCount; i++) {
            Pig piglet = Pig.builder()
                    .code(codes.get(i))
                    .gender(i % 2 == 0 ? PigGender.FEMALE : PigGender.MALE)
                    .status(PigStatus.PIGLET)
                    .building(building)
                    .genetics(fatherGenetics)
                    .mother(mother)
                    .birthRecord(birthRecord)
                    .birthDate(birthDate)
                    .recordedDate(LocalDate.now())
                    .build();
            piglets.add(pigRepository.save(piglet));
        }
        birthRecord.getPiglets().addAll(piglets);
        return piglets;
    }

    /**
     * Get available mother sows for a building.
     * Filters:
     * 1. Same building (geographical)
     * 2. Must be BREEDING_SOW status (reinseminated)
     * 3. Gestation threshold > 105 days from insemination date
     */
    public List<Pig> getAvailableMotherSows(Building building) {
        List<Pig> sows = pigRepository.findSowsByBuildingWithGenetics(building);
        List<Pig> eligible = new ArrayList<>();
        for (Pig sow : sows) {
            if (isEligibleMotherSow(sow)) {
                eligible.add(sow);
            }
        }
        return eligible;
    }

    /**
     * Check if a sow is eligible as a mother for birth recording.
     * Must be BREEDING_SOW with gestation > 105 days.
     */
    public boolean isEligibleMotherSow(Pig sow) {
        if (sow.getStatus() != PigStatus.BREEDING_SOW) return false;
        if (sow.getBirthDate() == null) return false;

        // Calculate days since last recorded date (proxy for insemination)
        long daysSinceInsemination = java.time.temporal.ChronoUnit.DAYS.between(
                sow.getRecordedDate() != null ? sow.getRecordedDate() : sow.getBirthDate(),
                LocalDate.now());

        return daysSinceInsemination > 105;
    }

    private Genetics createTemporaryGenetics(String code) {
        Genetics g = new Genetics();
        g.setCode(code);
        g.setName("Hybrid " + code);
        return g;
    }
}
