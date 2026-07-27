package com.example.swinecore.repository;

import com.example.swinecore.entity.Building;
import com.example.swinecore.entity.Farm;
import com.example.swinecore.entity.User;
import com.example.swinecore.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByPasswordResetToken(String token);

    // ---- Eager-join versions for template rendering ----

    /** All users with farm and building joined — avoids LazyInitializationException in templates */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.farm LEFT JOIN FETCH u.building ORDER BY u.name")
    List<User> findAllWithAssociations();

    /** Users filtered by role with associations eagerly loaded */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.farm LEFT JOIN FETCH u.building WHERE u.role = :role ORDER BY u.name")
    List<User> findByRoleWithAssociations(@Param("role") Role role);

    /** Users in a farm with associations */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.building WHERE u.farm = :farm ORDER BY u.role, u.name")
    List<User> findByFarmWithAssociations(@Param("farm") Farm farm);

    /** Users in a building with farm joined */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.farm WHERE u.building = :building ORDER BY u.name")
    List<User> findByBuildingWithAssociations(@Param("building") Building building);

    /** Staff/supervisors in a building by role */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.farm WHERE u.building = :building AND u.role = :role")
    List<User> findByBuildingAndRoleWithAssociations(@Param("building") Building building, @Param("role") Role role);

    // ---- Raw finders used internally (service layer only — session still open) ----
    List<User> findByRole(Role role);
    List<User> findByFarm(Farm farm);
    List<User> findByFarmAndRole(Farm farm, Role role);
    List<User> findByBuilding(Building building);
    List<User> findByBuildingAndRole(Building building, Role role);
    List<User> findByFarmIsNull();

    @Query("SELECT u FROM User u WHERE u.farm = :farm AND u.role IN (:roles)")
    List<User> findByFarmAndRoles(@Param("farm") Farm farm, @Param("roles") List<Role> roles);

    boolean existsByEmail(String email);
}
