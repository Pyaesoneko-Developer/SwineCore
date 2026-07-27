package com.example.swinecore.service;

import com.example.swinecore.entity.Farm;
import com.example.swinecore.entity.RuleSchedule;
import com.example.swinecore.entity.User;
import com.example.swinecore.entity.enums.RuleType;
import com.example.swinecore.repository.RuleScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RuleScheduleService {

    private static final String SEMEN_PRICE_RULE = "SYSTEM_SEMEN_PRICE_PER_DOSE";
    private static final double DEFAULT_SEMEN_PRICE = 60_000.0;

    private final RuleScheduleRepository ruleRepo;

    public RuleSchedule save(RuleSchedule rule) {
        validateNoOverlap(rule);
        return ruleRepo.save(rule);
    }

    public Optional<RuleSchedule> findById(Long id) {
        return ruleRepo.findById(id);
    }

    public List<RuleSchedule> findByFarm(Farm farm) {
        return ruleRepo.findByFarm(farm).stream()
            .filter(r -> !SEMEN_PRICE_RULE.equals(r.getName())).toList();
    }

    public double getSemenPrice(Farm farm) {
        if (farm == null) return DEFAULT_SEMEN_PRICE;
        return ruleRepo.findByFarm(farm).stream()
            .filter(RuleSchedule::isActive)
            .filter(r -> SEMEN_PRICE_RULE.equals(r.getName()))
            .map(RuleSchedule::getFeedAmountKg)
            .filter(v -> v != null && v > 0)
            .findFirst().orElse(DEFAULT_SEMEN_PRICE);
    }

    public void setSemenPrice(Farm farm, User manager, double price) {
        if (farm == null) throw new IllegalArgumentException("Manager has no assigned farm.");
        if (price <= 0 || price > 100_000_000)
            throw new IllegalArgumentException("Semen price must be greater than zero.");
        RuleSchedule setting = ruleRepo.findByFarm(farm).stream()
            .filter(r -> SEMEN_PRICE_RULE.equals(r.getName())).findFirst()
            .orElseGet(() -> RuleSchedule.builder().name(SEMEN_PRICE_RULE)
                .ruleType(RuleType.GENERAL).farm(farm).createdBy(manager)
                .appliesTo("BREEDING_BOAR").active(true).build());
        setting.setFeedAmountKg(price);
        setting.setDescription("Manager-defined semen price per dose (MMK)");
        setting.setActive(true);
        ruleRepo.save(setting);
    }

    public List<RuleSchedule> findActiveByFarm(Farm farm) {
        return ruleRepo.findByFarmAndActiveTrue(farm);
    }

    public void delete(Long id) {
        ruleRepo.deleteById(id);
    }

    /** Blueprint guardrail: Block overlapping day ranges within same farm + rule type. */
    private void validateNoOverlap(RuleSchedule rule) {
        if (rule.getFarm() == null) return;
        if (rule.getDayRangeStart() == null || rule.getDayRangeEnd() == null) return;

        List<RuleSchedule> existing = ruleRepo.findByFarm(rule.getFarm()).stream()
            .filter(r -> r.getId() == null || !r.getId().equals(rule.getId()))
            .filter(r -> r.getRuleType() == rule.getRuleType())
            .filter(RuleSchedule::isActive)
            .toList();

        for (RuleSchedule existingRule : existing) {
            if (existingRule.getDayRangeStart() == null || existingRule.getDayRangeEnd() == null) continue;
            boolean overlaps = rule.getDayRangeStart() <= existingRule.getDayRangeEnd()
                && rule.getDayRangeEnd() >= existingRule.getDayRangeStart();
            if (overlaps) {
                throw new IllegalArgumentException(
                    "Day range overlap detected with rule '" + existingRule.getName()
                    + "' (Days " + existingRule.getDayRangeStart() + "-" + existingRule.getDayRangeEnd()
                    + "). Overlapping configurations within the same pig category are not allowed.");
            }
        }
    }
}
