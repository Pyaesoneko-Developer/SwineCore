package com.example.swinecore.repository;

import com.example.swinecore.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FarmRepository extends JpaRepository<Farm, Long> {
    Optional<Farm> findByCode(String code);
    Optional<Farm> findByName(String name);
    boolean existsByCode(String code);
    boolean existsByName(String name);
}
