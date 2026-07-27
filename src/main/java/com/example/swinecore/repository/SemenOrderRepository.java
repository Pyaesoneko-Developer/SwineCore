package com.example.swinecore.repository;

import com.example.swinecore.entity.CustomerAccount;
import com.example.swinecore.entity.SemenOrder;
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
public interface SemenOrderRepository extends JpaRepository<SemenOrder, Long> {
    List<SemenOrder> findByCustomer(CustomerAccount customer);
    Optional<SemenOrder> findByOrderReference(String reference);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from SemenOrder o where o.orderReference = :reference")
    Optional<SemenOrder> findLockedByOrderReference(@Param("reference") String reference);
    List<SemenOrder> findByStatus(OrderStatus status);
}
