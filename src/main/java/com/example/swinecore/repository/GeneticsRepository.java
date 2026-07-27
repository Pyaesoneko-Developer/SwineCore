package com.example.swinecore.repository;

import com.example.swinecore.entity.Genetics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeneticsRepository extends JpaRepository<Genetics, Long> {
    Optional<Genetics> findByCode(String code);
    List<Genetics> findByActiveTrue();
    boolean existsByCode(String code);
}
