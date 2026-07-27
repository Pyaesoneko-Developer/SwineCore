package com.example.swinecore.repository;

import com.example.swinecore.entity.Farm;
import com.example.swinecore.entity.FinanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinanceTransactionRepository extends JpaRepository<FinanceTransaction, Long> {
    List<FinanceTransaction> findByFarm(Farm farm);
    List<FinanceTransaction> findByFarmAndType(Farm farm, String type);
    Optional<FinanceTransaction> findByReferenceId(String referenceId);
    boolean existsByReferenceId(String referenceId);

    @Query("SELECT SUM(f.amount) FROM FinanceTransaction f WHERE f.farm = :farm AND f.type = :type AND f.createdAt BETWEEN :start AND :end")
    Double sumByFarmAndTypeAndPeriod(@Param("farm") Farm farm, @Param("type") String type,
                                     @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
