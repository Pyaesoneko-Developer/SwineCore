package com.example.swinecore.scheduler;

import com.example.swinecore.entity.Pig;
import com.example.swinecore.entity.enums.PigGender;
import com.example.swinecore.entity.enums.PigStatus;
import com.example.swinecore.repository.PigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Daily job to auto-classify pigs by age:
 * - PIGLET -> GROWER at 60 days
 * - GROWER -> FINISHER at 100 days
 * - FEMALE -> BREEDING_SOW at 150 days
 * - MALE -> BREEDING_BOAR at 150 days
 *
 * Marketplace / approval statuses are skipped:
 * - PENDING_SALE_APPROVAL
 * - FOR_SALE
 * - SOLD
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PigStatusScheduler {

    private final PigRepository pigRepository;

    @Scheduled(cron = "0 0 1 * * *") // 1:00 AM daily
    @Transactional
    public void autoClassifyPigs() {
        log.info("Running pig auto-classification job...");

        List<Pig> pigs = pigRepository.findAll();
        int updated = 0;

        for (Pig pig : pigs) {
            if (pig == null || pig.getBirthDate() == null || pig.getStatus() == null) {
                continue;
            }

            /*
             * Do not auto-change sale workflow pigs.
             * Supervisor requested pigs must stay pending until manager approve/reject.
             */
            if (pig.getStatus() == PigStatus.PENDING_SALE_APPROVAL
                    || pig.getStatus() == PigStatus.FOR_SALE
                    || pig.getStatus() == PigStatus.SOLD) {
                continue;
            }

            long age = pig.getAgeInDays();

            if (age >= 150
                    && pig.getGender() == PigGender.FEMALE
                    && pig.getStatus() != PigStatus.BREEDING_SOW) {

                pig.setStatus(PigStatus.BREEDING_SOW);
                pigRepository.save(pig);
                updated++;

            } else if (age >= 150
                    && pig.getGender() == PigGender.MALE
                    && pig.getStatus() != PigStatus.BREEDING_BOAR) {

                pig.setStatus(PigStatus.BREEDING_BOAR);
                pigRepository.save(pig);
                updated++;

            } else if (age >= 100
                    && pig.getStatus() == PigStatus.GROWER) {

                pig.setStatus(PigStatus.FINISHER);
                pigRepository.save(pig);
                updated++;

            } else if (age >= 60
                    && pig.getStatus() == PigStatus.PIGLET) {

                pig.setStatus(PigStatus.GROWER);
                pigRepository.save(pig);
                updated++;
            }
        }

        log.info("Pig auto-classification complete. Updated {} pig(s).", updated);
    }
}