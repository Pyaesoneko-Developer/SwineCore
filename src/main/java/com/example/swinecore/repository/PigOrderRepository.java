package com.example.swinecore.repository;

import com.example.swinecore.entity.CustomerAccount;
import com.example.swinecore.entity.PigOrder;
import com.example.swinecore.entity.enums.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PigOrderRepository extends JpaRepository<PigOrder, Long> {
    List<PigOrder> findByCustomer(CustomerAccount customer);
    Optional<PigOrder> findByOrderReference(String reference);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from PigOrder o where o.orderReference = :reference")
    Optional<PigOrder> findLockedByOrderReference(@Param("reference") String reference);
    List<PigOrder> findByStatus(OrderStatus status);
}
