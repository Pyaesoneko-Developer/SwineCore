package com.example.swinecore.repository;

import com.example.swinecore.entity.PigOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PigOrderItemRepository extends JpaRepository<PigOrderItem, Long> {
}
