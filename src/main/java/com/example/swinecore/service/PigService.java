package com.example.swinecore.service;

import com.example.swinecore.entity.BirthRecord;
import com.example.swinecore.entity.Building;
import com.example.swinecore.entity.Farm;
import com.example.swinecore.entity.Genetics;
import com.example.swinecore.entity.Pig;
import com.example.swinecore.entity.User;
import com.example.swinecore.entity.enums.PigGender;
import com.example.swinecore.entity.enums.PigStatus;
import com.example.swinecore.repository.BirthRecordRepository;
import com.example.swinecore.repository.PigRepository;
import com.example.swinecore.util.PigCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PigService {

    private final PigRepository pigRepository;
    private final BirthRecordRepository birthRecordRepository;
    private final PigCodeGenerator codeGenerator;

    public Pig save(Pig pig) {
        return pigRepository.save(pig);
    }

    public Optional<Pig> findById(Long id) {
        return pigRepository.findById(id);
    }

    public Optional<Pig> findByCode(String code) {
        return pigRepository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public List<Pig> findByBuilding(Building building) {
        return pigRepository.findByBuildingWithAssociations(building);
    }

    @Transactional(readOnly = true)
    public List<Pig> findByFarm(Farm farm) {
        return pigRepository.findByFarmWithAssociations(farm);
    }

    @Transactional(readOnly = true)
    public List<Pig> findListedForSale() {
        return pigRepository.findListedForSaleWithAssociations();
    }

    @Transactional(readOnly = true)
    public List<Pig> findListedForSaleByFarm(Farm farm) {
        return pigRepository.findListedForSaleByFarmWithAssociations(farm);
    }

    @Transactional(readOnly = true)
    public List<Pig> findBreedingBoarsForSemen() {
        return pigRepository.findBreedingBoarsWithAssociations();
    }

    @Transactional(readOnly = true)
    public List<Pig> findSowsByBuilding(Building building) {
        return pigRepository.findSowsByBuildingWithGenetics(building);
    }

    // =========================================================
    // BIRTH RECORD
    // =========================================================

    public List<Pig> recordLitter(
            Farm farm,
            Building building,
            Genetics genetics,
            Pig mother,
            LocalDate birthDate,
            int litterSize,
            int alive,
            User recordedBy) {

        if (farm == null) {
            throw new IllegalArgumentException("Farm is required.");
        }

        if (building == null) {
            throw new IllegalArgumentException("Building is required.");
        }

        if (genetics == null) {
            throw new IllegalArgumentException("Father genetics is required.");
        }

        if (mother == null) {
            throw new IllegalArgumentException("Mother sow is required.");
        }

        if (mother.getStatus() != PigStatus.BREEDING_SOW) {
            throw new IllegalArgumentException("Selected mother must be breeding sow.");
        }

        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date is required.");
        }

        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be future date.");
        }

        if (litterSize <= 0) {
            throw new IllegalArgumentException("Litter size is required.");
        }

        if (alive < 0 || alive > litterSize) {
            throw new IllegalArgumentException("Invalid alive piglet count.");
        }

        int dead = litterSize - alive;

        BirthRecord birthRecord = BirthRecord.builder()
                .mother(mother)
                .building(building)
                .recordedBy(recordedBy)
                .birthDate(birthDate)
                .litterSize(litterSize)
                .alivePiglets(alive)
                .deadPiglets(dead)
                .confirmedByStaff(true)
                .confirmedBySupervisor(false)
                .build();

        birthRecord = birthRecordRepository.save(birthRecord);

        List<Pig> piglets = new ArrayList<>();

        if (alive > 0) {
            List<String> codes = codeGenerator.generateLitterCodes(
                    farm,
                    building,
                    genetics,
                    mother,
                    birthDate,
                    alive);

            for (int i = 0; i < alive; i++) {
                String code = codes.get(i).trim().toUpperCase();

                if (pigRepository.findByCode(code).isPresent()) {
                    throw new IllegalArgumentException("Pig code already exists: " + code);
                }

                Pig piglet = Pig.builder()
                        .code(code)
                        .gender(i % 2 == 0 ? PigGender.FEMALE : PigGender.MALE)
                        .status(PigStatus.PIGLET)
                        .building(building)
                        .genetics(genetics)
                        .mother(mother)
                        .birthRecord(birthRecord)
                        .birthDate(birthDate)
                        .recordedDate(LocalDate.now())
                        .currentWeight(null)
                        .photoPath(null)
                        .notes("Born from " + mother.getCode())
                        .build();

                piglets.add(pigRepository.save(piglet));
            }
        }

        String oldNotes = mother.getNotes() == null ? "" : mother.getNotes();

        String birthNote = "\n\nBirth Record:"
                + "\nDate: " + birthDate
                + "\nAlive Piglets: " + alive
                + "\nDead Piglets: " + dead
                + "\nRecorded By: " + (recordedBy != null ? recordedBy.getFullName() : "-");

        mother.setStatus(PigStatus.BREEDING_SOW);
        mother.setNotes(oldNotes + birthNote);
        mother.setRecordedDate(LocalDate.now());

        pigRepository.save(mother);

        return piglets;
    }

    // =========================================================
    // PURCHASED PIG
    // =========================================================

    public Pig registerPurchasedPig(
            String code,
            Farm farm,
            Building building,
            Genetics genetics,
            PigGender gender,
            PigStatus status,
            LocalDate birthDate,
            Double currentWeight,
            String notes,
            String photoPath) {

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Pig code is required.");
        }

        if (farm == null) {
            throw new IllegalArgumentException("Farm is required.");
        }

        if (building == null) {
            throw new IllegalArgumentException("Building is required.");
        }

        if (genetics == null) {
            throw new IllegalArgumentException("Genetics is required.");
        }

        if (gender == null) {
            throw new IllegalArgumentException("Gender is required.");
        }

        if (status == null) {
            throw new IllegalArgumentException("Status is required.");
        }

        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date is required.");
        }

        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be future date.");
        }

        if (currentWeight == null || currentWeight <= 0) {
            throw new IllegalArgumentException("Current weight must be greater than zero.");
        }

        String cleanCode = code.trim().toUpperCase();

        if (pigRepository.findByCode(cleanCode).isPresent()) {
            throw new IllegalArgumentException("Pig code already exists.");
        }

        Pig pig = Pig.builder()
                .code(cleanCode)
                .gender(gender)
                .status(status)
                .building(building)
                .genetics(genetics)
                .birthDate(birthDate)
                .recordedDate(LocalDate.now())
                .currentWeight(currentWeight)
                .photoPath(photoPath)
                .notes(notes)
                .build();

        return pigRepository.save(pig);
    }

    // =========================================================
    // MARKETPLACE
    // =========================================================

    public void markForSale(Long pigId, Double price) {
        Pig pig = pigRepository.findById(pigId)
                .orElseThrow(() -> new IllegalArgumentException("Pig not found."));

        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Sale price must be greater than zero.");
        }

        pig.setStatus(PigStatus.FOR_SALE);
        pig.setListedForSale(true);
        pig.setSalePrice(price);

        if (pig.getListedForSaleDate() == null) {
            pig.setListedForSaleDate(LocalDate.now());
        }

        pigRepository.save(pig);
    }

    public void markSold(Long pigId) {
        Pig pig = pigRepository.findById(pigId)
                .orElseThrow(() -> new IllegalArgumentException("Pig not found."));

        pig.setStatus(PigStatus.SOLD);
        pig.setListedForSale(false);
        pig.setSoldDate(LocalDate.now());

        pigRepository.save(pig);
    }

    public void unlistFromSale(Long pigId) {
        Pig pig = pigRepository.findById(pigId)
                .orElseThrow(() -> new IllegalArgumentException("Pig not found."));

        pig.setStatus(PigStatus.FINISHER);
        pig.setListedForSale(false);
        pig.setSalePrice(null);
        pig.setListedForSaleDate(null);

        pigRepository.save(pig);
    }

    // =========================================================
    // UPDATE SALE PIG
    // =========================================================

    public void updatePigForSale(
            Long id,
            Building building,
            Genetics genetics,
            PigGender gender,
            PigStatus status,
            LocalDate birthDate,
            Double weight,
            Double price,
            String notes,
            String photoPath) {

        Pig pig = pigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pig not found."));

        if (building == null) {
            throw new IllegalArgumentException("Building is required.");
        }

        if (genetics == null) {
            throw new IllegalArgumentException("Genetics is required.");
        }

        if (gender == null) {
            throw new IllegalArgumentException("Gender is required.");
        }

        if (status == null) {
            throw new IllegalArgumentException("Status is required.");
        }

        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date is required.");
        }

        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be future date.");
        }

        if (weight == null || weight <= 0) {
            throw new IllegalArgumentException("Current weight must be greater than zero.");
        }

        PigStatus oldStatus = pig.getStatus();

        pig.setBuilding(building);
        pig.setGenetics(genetics);
        pig.setGender(gender);
        pig.setBirthDate(birthDate);
        pig.setCurrentWeight(weight);
        pig.setNotes(notes);

        if (oldStatus == PigStatus.FOR_SALE) {
            if (price == null || price <= 0) {
                throw new IllegalArgumentException("Sale price is required for marketplace pig.");
            }

            pig.setStatus(PigStatus.FOR_SALE);
            pig.setListedForSale(true);
            pig.setSalePrice(price);

            if (pig.getListedForSaleDate() == null) {
                pig.setListedForSaleDate(LocalDate.now());
            }

        } else if (oldStatus == PigStatus.SOLD) {
            pig.setStatus(PigStatus.SOLD);
            pig.setListedForSale(false);

        } else {
            pig.setStatus(status);

            if (status == PigStatus.FOR_SALE) {
                if (price == null || price <= 0) {
                    throw new IllegalArgumentException("Sale price is required for marketplace pig.");
                }

                pig.setListedForSale(true);
                pig.setSalePrice(price);

                if (pig.getListedForSaleDate() == null) {
                    pig.setListedForSaleDate(LocalDate.now());
                }

            } else if (status == PigStatus.SOLD) {
                pig.setListedForSale(false);

            } else {
                pig.setListedForSale(false);
                pig.setSalePrice(null);
                pig.setListedForSaleDate(null);
            }
        }

        if (photoPath != null && !photoPath.isBlank()) {
            pig.setPhotoPath(photoPath);
        }

        pigRepository.save(pig);
    }

    // =========================================================
    // DELETE
    // =========================================================

    public void delete(Long id) {
        pigRepository.deleteById(id);
    }
}