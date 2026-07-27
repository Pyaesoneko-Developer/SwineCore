package com.example.swinecore.repository;

import com.example.swinecore.entity.Building;
import com.example.swinecore.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

    /**
     * Fetches buildings with their farm eagerly joined — prevents LazyInitializationException
     * when templates access b.farm.name. Staff and pigs are left lazy; open-in-view=true
     * ensures they load safely during Thymeleaf rendering.
     * NOTE: Do NOT add a second JOIN FETCH for a bag (staff/pigs) in the same query —
     * Hibernate throws MultipleBagFetchException when two collections are fetched together.
     */
    @Query("SELECT b FROM Building b JOIN FETCH b.farm WHERE b.farm = :farm ORDER BY b.name")
    List<Building> findByFarmWithFarm(@Param("farm") Farm farm);

    /** Single building with farm joined for controllers that display b.farm.name. */
    @Query("SELECT b FROM Building b JOIN FETCH b.farm WHERE b.id = :id")
    Optional<Building> findByIdWithFarm(@Param("id") Long id);

    // ---- Raw finders ----
    List<Building> findByFarm(Farm farm);
    List<Building> findByFarmId(Long farmId);
    long countByFarm(Farm farm);
    boolean existsByCode(String code);

    @Query("SELECT b FROM Building b JOIN FETCH b.farm ORDER BY b.farm.name, b.name")
    List<Building> findAllWithFarm();
}
