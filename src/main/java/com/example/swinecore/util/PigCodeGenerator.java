package com.example.swinecore.util;

import com.example.swinecore.entity.Building;
import com.example.swinecore.entity.Genetics;
import com.example.swinecore.entity.Pig;
import com.example.swinecore.repository.PigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PigCodeGenerator {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("ddMMyyyy");

    private final PigRepository pigRepository;

    public List<String> generateLitterCodes(
            com.example.swinecore.entity.Farm farm,
            Building building,
            Genetics genetics,
            Pig mother,
            LocalDate recordDate,
            int count) {

        String farmCode = compactToken(farm.getCode(), 2);
        String buildCode = compactToken(building.getCode(), 2);
        String genCode = compactToken(genetics.getCode(), 3);
        String motherSuffix = resolveMotherSuffix(mother);
        String datePart = recordDate.format(DATE_FMT);

        List<String> codes = new ArrayList<>();

        int seq = 1;

        while (codes.size() < count) {
            String candidate = String.format(
                    "%s%s%s%s%s%02d",
                    farmCode,
                    buildCode,
                    genCode,
                    motherSuffix,
                    datePart,
                    seq
            );

            if (!pigRepository.existsByCode(candidate)) {
                codes.add(candidate);
            }

            seq++;
        }

        return codes;
    }

    private String resolveMotherSuffix(Pig mother) {
        if (mother == null || mother.getCode() == null) {
            return "00";
        }

        String code = mother.getCode()
                .replaceAll("[^A-Za-z0-9]", "")
                .toUpperCase();

        if (code.length() < 2) {
            return "00";
        }

        return code.substring(code.length() - 2);
    }

    private String compactToken(String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return "X";
        }

        String cleaned = value
                .replaceAll("[^A-Za-z0-9]", "")
                .toUpperCase();

        if (cleaned.isBlank()) {
            return "X";
        }

        return cleaned.length() <= maxLength
                ? cleaned
                : cleaned.substring(0, maxLength);
    }
}