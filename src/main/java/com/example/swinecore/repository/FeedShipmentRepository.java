package com.example.swinecore.repository;

import com.example.swinecore.entity.Farm;
import com.example.swinecore.entity.FeedShipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedShipmentRepository extends JpaRepository<FeedShipment, Long> {
    List<FeedShipment> findByFarm(Farm farm);
    List<FeedShipment> findByFarmAndVerifiedFalse(Farm farm);
    List<FeedShipment> findByFarmAndPaymentReleasedFalse(Farm farm);
    Optional<FeedShipment> findByInvoiceNumber(String invoiceNumber);
}
