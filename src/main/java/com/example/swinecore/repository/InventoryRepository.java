package com.example.swinecore.repository;

import com.example.swinecore.entity.Farm;
import com.example.swinecore.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByFarm(Farm farm);
    Optional<Inventory> findByFarmAndFeedType(Farm farm, String feedType);
    List<Inventory> findByFarmAndAlertTriggeredTrue(Farm farm);
    List<Inventory> findByAlertTriggeredTrue();
}
