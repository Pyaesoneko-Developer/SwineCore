package com.example.swinecore.service;

import com.example.swinecore.entity.Farm;
import com.example.swinecore.entity.enums.PigStatus;
import com.example.swinecore.entity.enums.Role;
import com.example.swinecore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final FarmRepository farmRepository;
    private final BuildingRepository buildingRepository;
    private final UserRepository userRepository;
    private final PigRepository pigRepository;
    private final AttendanceRepository attendanceRepository;
    private final FinanceTransactionRepository financeRepo;
    private final PigOrderRepository pigOrderRepository;

    /**
     * Global summary for Admin dashboard.
     */
    public Map<String, Object> getGlobalSummary() {
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("totalFarms", farmRepository.count());
        data.put("totalBuildings", buildingRepository.count());
        data.put("totalUsers", userRepository.count());
        data.put("totalPigs", pigRepository.count());
        data.put("totalSold", pigRepository.findByStatus(PigStatus.SOLD).size());

        /*
         * Marketplace-visible pigs only.
         * PENDING_SALE_APPROVAL is not counted as for sale.
         */
        data.put("totalForSale", pigRepository.findByListedForSaleTrue().size());

        return data;
    }

    /**
     * Per-farm pig breakdown.
     * Sold pigs are excluded from active pig count.
     * Pending sale approval pigs are still farm pigs, so they remain included.
     */
    public Map<String, Long> getPigDistributionByFarm() {
        Map<String, Long> dist = new LinkedHashMap<>();

        farmRepository.findAll().forEach(farm -> {
            long count = pigRepository.findByFarm(farm).stream()
                    .filter(p -> p.getStatus() != PigStatus.SOLD)
                    .count();

            dist.put(farm.getName(), count);
        });

        return dist;
    }

    /**
     * Monthly income/expense for a farm, last 6 months.
     */
    public Map<String, Map<String, Double>> getMonthlyFinancials(Farm farm) {
        Map<String, Map<String, Double>> result = new LinkedHashMap<>();

        if (farm == null) {
            return result;
        }

        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);

            LocalDateTime start = ym.atDay(1).atStartOfDay();
            LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

            Double income = financeRepo.sumByFarmAndTypeAndPeriod(farm, "INCOME", start, end);
            Double expense = financeRepo.sumByFarmAndTypeAndPeriod(farm, "EXPENSE", start, end);

            Map<String, Double> monthData = new HashMap<>();
            monthData.put("income", income != null ? income : 0.0);
            monthData.put("expense", expense != null ? expense : 0.0);

            result.put(ym.toString(), monthData);
        }

        return result;
    }

    /**
     * Attendance stats for a farm, last 30 days.
     */
    public Map<String, Long> getAttendanceStats(Farm farm) {
        Map<String, Long> stats = new LinkedHashMap<>();

        if (farm == null) {
            return stats;
        }

        LocalDate from = LocalDate.now().minusDays(30);
        LocalDate to = LocalDate.now();

        userRepository.findByFarmAndRoles(
                        farm,
                        List.of(Role.STAFF, Role.SUPERVISOR)
                )
                .forEach(user -> {
                    long days = attendanceRepository
                            .findByUserAndWorkDateBetween(user, from, to)
                            .stream()
                            .filter(a -> a.isAttended())
                            .count();

                    stats.put(user.getFullName(), days);
                });

        return stats;
    }
}