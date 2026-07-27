package com.example.swinecore.service;

import com.example.swinecore.entity.*;
import com.example.swinecore.entity.enums.PigStatus;
import com.example.swinecore.entity.enums.RuleType;
import com.example.swinecore.repository.BuildingRepository;
import com.example.swinecore.repository.InventoryRepository;
import com.example.swinecore.repository.PigRepository;
import com.example.swinecore.repository.RuleScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private static final String KEY_SEPARATOR = "::";

    private final InventoryRepository inventoryRepository;
    private final PigRepository pigRepository;
    private final RuleScheduleRepository ruleRepo;
    private final BuildingRepository buildingRepository;

    public List<Inventory> getByFarm(Farm farm) {
        return inventoryRepository.findByFarm(farm);
    }

    /**
     * Uses the existing feed_type column as BuildingId::FeedType.
     * No schema change required.
     */
    public Inventory addFactoryStock(Building building, String feedType, double quantityKg) {
        if (building == null || building.getFarm() == null) {
            throw new IllegalArgumentException("Building or farm is missing.");
        }

        if (feedType == null || feedType.isBlank()) {
            throw new IllegalArgumentException("Feed type is required.");
        }

        String key = stockKey(building.getId(), feedType);

        Inventory inv = inventoryRepository.findByFarmAndFeedType(building.getFarm(), key)
                .orElse(Inventory.builder()
                        .farm(building.getFarm())
                        .feedType(key)
                        .quantityKg(0.0)
                        .alertThresholdKg(0.0)
                        .build());

        inv.setQuantityKg(safe(inv.getQuantityKg()) + quantityKg);

        refreshMetrics(inv, building, feedType);

        return inventoryRepository.save(inv);
    }

    public void deductDailyConsumption(Building building) {
        if (building == null || building.getFarm() == null) {
            return;
        }

        List<RuleSchedule> rules = activeFeedRules(building.getFarm());

        ensureRuleLedgers(building, rules);

        inventoryRepository.findByFarm(building.getFarm()).stream()
                .filter(inv -> building.getId().equals(buildingId(inv.getFeedType())))
                .forEach(inv -> {
                    String feed = displayFeedType(inv.getFeedType());
                    double daily = calculateDailyConsumption(building, feed, rules);

                    inv.setQuantityKg(Math.max(0.0, safe(inv.getQuantityKg()) - daily));
                    updateMetrics(inv, daily);

                    inventoryRepository.save(inv);
                });
    }

    public void refreshBuilding(Building building) {
        if (building == null || building.getFarm() == null) {
            return;
        }

        List<RuleSchedule> rules = activeFeedRules(building.getFarm());

        ensureRuleLedgers(building, rules);

        inventoryRepository.findByFarm(building.getFarm()).stream()
                .filter(inv -> building.getId().equals(buildingId(inv.getFeedType())))
                .forEach(inv -> {
                    refreshMetrics(inv, building, displayFeedType(inv.getFeedType()));
                    inventoryRepository.save(inv);
                });
    }

    private void ensureRuleLedgers(Building building, List<RuleSchedule> rules) {
        rules.stream()
                .map(RuleSchedule::getFeedType)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .distinct()
                .forEach(feed -> inventoryRepository
                        .findByFarmAndFeedType(building.getFarm(), stockKey(building.getId(), feed))
                        .orElseGet(() -> inventoryRepository.save(Inventory.builder()
                                .farm(building.getFarm())
                                .feedType(stockKey(building.getId(), feed))
                                .quantityKg(0.0)
                                .alertThresholdKg(0.0)
                                .alertTriggered(true)
                                .build())));
    }

    private void refreshMetrics(Inventory inv, Building building, String feed) {
        double daily = calculateDailyConsumption(
                building,
                feed,
                activeFeedRules(building.getFarm()));

        updateMetrics(inv, daily);
    }

    private void updateMetrics(Inventory inv, double daily) {
        inv.setAlertThresholdKg(daily * 2.0);
        inv.setAlertTriggered(daily > 0 && safe(inv.getQuantityKg()) <= daily * 2.0);
    }

    private double calculateDailyConsumption(
            Building building,
            String feedType,
            List<RuleSchedule> rules) {

        double total = 0.0;

        List<Pig> activePigs = pigRepository.findByBuilding(building).stream()
                .filter(this::activePig)
                .toList();

        for (Pig pig : activePigs) {
            for (RuleSchedule rule : rules) {
                if (rule.getFeedType() != null
                        && rule.getFeedType().equalsIgnoreCase(feedType)
                        && rule.getFeedAmountKg() != null
                        && matchesRule(pig, rule)
                        && matchesCategory(pig, rule)) {

                    total += rule.getFeedAmountKg();
                }
            }
        }

        return total;
    }

    private boolean matchesRule(Pig pig, RuleSchedule rule) {
        long age = pig.getAgeInDays();

        if (rule.getDayRangeStart() != null && rule.getDayRangeEnd() != null) {
            return age >= rule.getDayRangeStart()
                    && age <= rule.getDayRangeEnd();
        }

        return rule.getDayFromBirth() != null
                && rule.getDayFromBirth() == age;
    }

    private boolean matchesCategory(Pig pig, RuleSchedule rule) {
        if (pig == null || pig.getStatus() == null || rule == null || rule.getAppliesTo() == null) {
            return false;
        }

        return "ALL".equals(rule.getAppliesTo())
                || pig.getStatus().name().equals(rule.getAppliesTo());
    }

    /**
     * Current PigStatus supports:
     * PIGLET, GROWER, FINISHER, BREEDING_SOW, BREEDING_BOAR,
     * PENDING_SALE_APPROVAL, FOR_SALE, SOLD.
     *
     * Only SOLD is removed from feed consumption.
     * PENDING_SALE_APPROVAL is still physically in the farm, so it remains active.
     */
    private boolean activePig(Pig pig) {
        return pig != null
                && pig.getStatus() != null
                && pig.getStatus() != PigStatus.SOLD;
    }

    private List<RuleSchedule> activeFeedRules(Farm farm) {
        if (farm == null) {
            return List.of();
        }

        return ruleRepo.findByFarmAndRuleType(farm, RuleType.FEEDING).stream()
                .filter(RuleSchedule::isActive)
                .toList();
    }

    private double safe(Double value) {
        return value == null ? 0.0 : value;
    }

    public String stockKey(Long buildingId, String feed) {
        return buildingId + KEY_SEPARATOR + feed.trim();
    }

    public Long buildingId(String key) {
        try {
            if (key == null || !key.contains(KEY_SEPARATOR)) {
                return null;
            }

            return Long.valueOf(key.substring(0, key.indexOf(KEY_SEPARATOR)));
        } catch (Exception e) {
            return null;
        }
    }

    public String displayFeedType(String key) {
        if (key == null) {
            return "-";
        }

        int index = key.indexOf(KEY_SEPARATOR);

        return index < 0 ? key : key.substring(index + KEY_SEPARATOR.length());
    }

    public List<Inventory> getAlertsForFarm(Farm farm) {
        return inventoryRepository.findByFarmAndAlertTriggeredTrue(farm);
    }

    public List<InventoryView> getViews(Farm farm) {
        if (farm == null) {
            return List.of();
        }

        return inventoryRepository.findByFarm(farm).stream()
                .map(this::view)
                .toList();
    }

    public List<InventoryView> getAlertViews() {
        return inventoryRepository.findByAlertTriggeredTrue().stream()
                .map(this::view)
                .toList();
    }

    private InventoryView view(Inventory inv) {
        Long buildingId = buildingId(inv.getFeedType());

        Building building = buildingId == null
                ? null
                : buildingRepository.findById(buildingId).orElse(null);

        double daily = safe(inv.getAlertThresholdKg()) / 2.0;
        double days = daily > 0 ? safe(inv.getQuantityKg()) / daily : 0.0;

        return new InventoryView(
                inv.getId(),
                inv.getFarm(),
                building,
                displayFeedType(inv.getFeedType()),
                safe(inv.getQuantityKg()),
                daily,
                days,
                safe(inv.getAlertThresholdKg()),
                inv.isAlertTriggered());
    }

    public record InventoryView(
            Long id,
            Farm farm,
            Building building,
            String feedType,
            double quantityKg,
            double dailyConsumptionKg,
            double remainingDays,
            double alertThresholdKg,
            boolean alertTriggered) {
    }
}