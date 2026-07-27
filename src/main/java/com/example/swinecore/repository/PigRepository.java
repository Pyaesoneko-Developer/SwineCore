package com.example.swinecore.repository;

import com.example.swinecore.entity.Building;
import com.example.swinecore.entity.Farm;
import com.example.swinecore.entity.Pig;
import com.example.swinecore.entity.enums.PigGender;
import com.example.swinecore.entity.enums.PigStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PigRepository extends JpaRepository<Pig, Long> {

    Optional<Pig> findByCode(String code);

    boolean existsByCode(String code);

    // =========================================================
    // Eager-join versions for template rendering
    // =========================================================

    @Query("""
            SELECT p
            FROM Pig p
            JOIN FETCH p.building b
            JOIN FETCH b.farm
            LEFT JOIN FETCH p.genetics
            ORDER BY p.id DESC
            """)
    List<Pig> findAllWithAssociations();

    @Query("""
            SELECT p
            FROM Pig p
            JOIN FETCH p.building b
            JOIN FETCH b.farm
            LEFT JOIN FETCH p.genetics
            WHERE p.building = :building
            ORDER BY p.id DESC
            """)
    List<Pig> findByBuildingWithAssociations(@Param("building") Building building);

    @Query("""
            SELECT p
            FROM Pig p
            JOIN FETCH p.building b
            JOIN FETCH b.farm f
            LEFT JOIN FETCH p.genetics
            WHERE f = :farm
            ORDER BY p.id DESC
            """)
    List<Pig> findByFarmWithAssociations(@Param("farm") Farm farm);

    @Query("""
            SELECT p
            FROM Pig p
            JOIN FETCH p.building b
            JOIN FETCH b.farm
            LEFT JOIN FETCH p.genetics
            WHERE p.listedForSale = true
            ORDER BY p.id DESC
            """)
    List<Pig> findListedForSaleWithAssociations();

    @Query("""
            SELECT p
            FROM Pig p
            JOIN FETCH p.building b
            JOIN FETCH b.farm f
            LEFT JOIN FETCH p.genetics
            WHERE f = :farm
              AND p.listedForSale = true
            ORDER BY p.id DESC
            """)
    List<Pig> findListedForSaleByFarmWithAssociations(@Param("farm") Farm farm);

    @Query("""
            SELECT p
            FROM Pig p
            JOIN FETCH p.building b
            JOIN FETCH b.farm
            LEFT JOIN FETCH p.genetics
            WHERE p.status = :status
              AND p.gender = :gender
            ORDER BY p.id DESC
            """)
    List<Pig> findBreedingBoarsWithAssociations(
            @Param("status") PigStatus status,
            @Param("gender") PigGender gender
    );

    default List<Pig> findBreedingBoarsWithAssociations() {
        return findBreedingBoarsWithAssociations(PigStatus.BREEDING_BOAR, PigGender.MALE);
    }

    @Query("""
            SELECT p
            FROM Pig p
            LEFT JOIN FETCH p.genetics
            JOIN FETCH p.building b
            WHERE p.status = :status
              AND p.building = :building
            ORDER BY p.id DESC
            """)
    List<Pig> findSowsByBuildingWithGenetics(
            @Param("building") Building building,
            @Param("status") PigStatus status
    );

    default List<Pig> findSowsByBuildingWithGenetics(Building building) {
        return findSowsByBuildingWithGenetics(building, PigStatus.BREEDING_SOW);
    }

    // =========================================================
    // Raw finders used internally
    // =========================================================

    List<Pig> findByBuilding(Building building);

    List<Pig> findByBuildingId(Long buildingId);

    List<Pig> findByBuildingAndStatus(Building building, PigStatus status);

    @Query("""
            SELECT p
            FROM Pig p
            WHERE p.building.farm = :farm
            ORDER BY p.id DESC
            """)
    List<Pig> findByFarm(@Param("farm") Farm farm);

    @Query("""
            SELECT p
            FROM Pig p
            WHERE p.building.farm = :farm
              AND p.status = :status
            ORDER BY p.id DESC
            """)
    List<Pig> findByFarmAndStatus(
            @Param("farm") Farm farm,
            @Param("status") PigStatus status
    );

    List<Pig> findByStatus(PigStatus status);

    List<Pig> findByStatusAndGender(PigStatus status, PigGender gender);

    List<Pig> findByListedForSaleTrue();

    @Query("""
            SELECT p
            FROM Pig p
            WHERE p.building.farm = :farm
              AND p.listedForSale = true
            ORDER BY p.id DESC
            """)
    List<Pig> findListedForSaleByFarm(@Param("farm") Farm farm);

    @Query("""
            SELECT COUNT(p)
            FROM Pig p
            WHERE p.building = :building
              AND p.status <> :soldStatus
            """)
    long countActiveByBuilding(
            @Param("building") Building building,
            @Param("soldStatus") PigStatus soldStatus
    );

    default long countActiveByBuilding(Building building) {
        return countActiveByBuilding(building, PigStatus.SOLD);
    }
}