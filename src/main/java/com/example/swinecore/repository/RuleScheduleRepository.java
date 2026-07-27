package com.example.swinecore.repository;

import com.example.swinecore.entity.Farm;
import com.example.swinecore.entity.RuleSchedule;
import com.example.swinecore.entity.enums.RuleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleScheduleRepository extends JpaRepository<RuleSchedule, Long> {
    List<RuleSchedule> findByFarmAndActiveTrue(Farm farm);
    List<RuleSchedule> findByFarm(Farm farm);
    List<RuleSchedule> findByFarmAndRuleType(Farm farm, RuleType ruleType);
    List<RuleSchedule> findByFarmAndDayFromBirth(Farm farm, int day);
}
