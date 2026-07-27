package com.example.swinecore.scheduler;

import com.example.swinecore.entity.Building;
import com.example.swinecore.repository.BuildingRepository;
import com.example.swinecore.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryScheduler {
    private final BuildingRepository buildingRepository;
    private final InventoryService inventoryService;

    @Scheduled(cron = "0 30 23 * * *")
    public void deductDailyFeed() {
        log.info("Running building-level daily feed deduction...");
        for (Building building : buildingRepository.findAll()) {
            try {
                inventoryService.deductDailyConsumption(building);
                inventoryService.getViews(building.getFarm()).stream()
                    .filter(inv -> inv.building() != null && inv.building().getId().equals(building.getId()))
                    .filter(inv -> inv.alertTriggered()).forEach(inv ->
                        log.warn("LOW STOCK — Farm: {}, Building: {}, Feed: {}, Qty: {}kg, Days: {}",
                            building.getFarm().getName(), building.getName(), inv.feedType(),
                            inv.quantityKg(), inv.remainingDays()));
            } catch (Exception e) {
                log.error("Feed deduction failed for building {}: {}", building.getName(), e.getMessage());
            }
        }
    }
}
