USE swinecore_testing;
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;
-- =========================================================
-- CLEAN OLD DEMO ORDERS
-- =========================================================
DELETE FROM pig_order_items
WHERE id BETWEEN 991001 AND 991999;
DELETE FROM pig_orders
WHERE id BETWEEN 990001 AND 990999;
DELETE FROM semen_orders
WHERE id BETWEEN 992001 AND 992999;
-- =========================================================
-- CLEAN FINANCE / INVENTORY / SHIPMENT
-- =========================================================
DELETE FROM finance_transactions
WHERE id BETWEEN 980001 AND 980999;
DELETE FROM feed_shipments
WHERE id BETWEEN 940001 AND 940999;
DELETE FROM inventory
WHERE id BETWEEN 930001 AND 930999;
-- =========================================================
-- CLEAN TASKS
-- =========================================================
DELETE FROM daily_tasks
WHERE id BETWEEN 970001 AND 970999;
DELETE FROM daily_tasks
WHERE assigned_staff_id BETWEEN 910001 AND 910999;
DELETE FROM daily_tasks
WHERE reviewed_by BETWEEN 910001 AND 910999;
-- =========================================================
-- CLEAN ATTENDANCE
-- =========================================================
DELETE FROM attendance
WHERE id BETWEEN 960001 AND 960999;
DELETE FROM attendance
WHERE user_id BETWEEN 910001 AND 910999;
-- =========================================================
-- CLEAN BIRTH RECORDS + PIGS
-- =========================================================
UPDATE pigs
SET birth_record_id = NULL
WHERE id BETWEEN 950001 AND 950999;
DELETE FROM birth_records
WHERE id BETWEEN 955001 AND 955999;
DELETE FROM birth_records
WHERE recorded_by BETWEEN 910001 AND 910999;
DELETE FROM pigs
WHERE id BETWEEN 950001 AND 950999;
-- =========================================================
-- CLEAN RULES
-- =========================================================
DELETE FROM rule_schedules
WHERE id BETWEEN 920001 AND 920999;
DELETE FROM rule_schedules
WHERE created_by BETWEEN 910001 AND 910999;
-- =========================================================
-- CLEAN STAFF SHIFT
-- =========================================================
DELETE FROM staff_shifts
WHERE id BETWEEN 915001 AND 915999;
DELETE FROM staff_shifts
WHERE user_id BETWEEN 910001 AND 910999;
DELETE FROM shifts
WHERE id BETWEEN 914001 AND 914999;
-- =========================================================
-- CLEAN ROOMS
-- =========================================================
DELETE FROM rooms
WHERE id BETWEEN 913001 AND 913999;
-- =========================================================
-- CLEAN CUSTOMER
-- =========================================================
DELETE FROM customer_accounts
WHERE id BETWEEN 912001 AND 912999;
-- =========================================================
-- CLEAN USERS
-- =========================================================
DELETE FROM users
WHERE id BETWEEN 910001 AND 910999;
-- =========================================================
-- CLEAN BUILDINGS / FARMS
-- =========================================================
DELETE FROM buildings
WHERE id BETWEEN 909001 AND 909999;
DELETE FROM farms
WHERE id BETWEEN 908001 AND 908999;
SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;
-- =========================================================
-- GENETICS
-- =========================================================
INSERT IGNORE INTO genetics (id, name, code, description, active)
VALUES (
        901001,
        'Yorkshire',
        'Y',
        'Large White maternal line',
        1
    ),
    (
        901002,
        'Landrace',
        'L',
        'High litter size maternal line',
        1
    ),
    (
        901003,
        'Duroc',
        'D',
        'Fast growth terminal sire line',
        1
    ),
    (
        901004,
        'Yorkshire x Landrace',
        'YL',
        'Commercial breeding sow line',
        1
    ),
    (
        901005,
        'Duroc x Landrace',
        'DL',
        'Fast growing market pig line',
        1
    );
SET @GEN_Y = (
        SELECT id
        FROM genetics
        WHERE code = 'Y'
        LIMIT 1
    );
SET @GEN_L = (
        SELECT id
        FROM genetics
        WHERE code = 'L'
        LIMIT 1
    );
SET @GEN_D = (
        SELECT id
        FROM genetics
        WHERE code = 'D'
        LIMIT 1
    );
SET @GEN_YL = (
        SELECT id
        FROM genetics
        WHERE code = 'YL'
        LIMIT 1
    );
SET @GEN_DL = (
        SELECT id
        FROM genetics
        WHERE code = 'DL'
        LIMIT 1
    );
-- =========================================================
-- FARMS
-- =========================================================
INSERT INTO farms (
        id,
        name,
        code,
        location,
        description,
        image_path,
        latitude,
        longitude,
        created_at
    )
VALUES (
        908001,
        'Hmawbi Integrated Swine Farm',
        'HMB1',
        'Hmawbi Township, Yangon Region',
        'Breeding and commercial pig farm for Yangon area.',
        '/images/farms/hmawbi.jpg',
        17.1074,
        96.0451,
        NOW()
    ),
    (
        908002,
        'Bago Golden Valley Swine Farm',
        'BGO2',
        'Bago Township, Bago Region',
        'Breeding and market pig farm for Bago region.',
        '/images/farms/bago.jpg',
        17.3352,
        96.4810,
        NOW()
    );
-- =========================================================
-- BUILDINGS
-- =========================================================
INSERT INTO buildings (
        id,
        name,
        code,
        description,
        farm_id,
        birth_form_rotation_index,
        created_at
    )
VALUES (
        909001,
        'Hmawbi Breeding House A',
        'HB-A',
        'Breeding sows, boars and piglet area',
        908001,
        0,
        NOW()
    ),
    (
        909002,
        'Hmawbi Grower House B',
        'HG-B',
        'Grower and finisher pig area',
        908001,
        0,
        NOW()
    ),
    (
        909003,
        'Bago Breeding House A',
        'BB-A',
        'Breeding sows, boars and newborn piglets',
        908002,
        0,
        NOW()
    ),
    (
        909004,
        'Bago Grower House B',
        'BG-B',
        'Grower and market pig area',
        908002,
        0,
        NOW()
    );
-- =========================================================
-- ROOMS
-- =========================================================
INSERT INTO rooms (
        id,
        name,
        code,
        description,
        building_id,
        active,
        capacity,
        created_at
    )
VALUES (
        913001,
        'Hmawbi Sow Room',
        'HSR1',
        'Breeding sow room',
        909001,
        1,
        40,
        NOW()
    ),
    (
        913002,
        'Hmawbi Piglet Room',
        'HPR1',
        'Newborn piglet room',
        909001,
        1,
        120,
        NOW()
    ),
    (
        913003,
        'Hmawbi Grower Room',
        'HGR1',
        'Grower pig room',
        909002,
        1,
        160,
        NOW()
    ),
    (
        913004,
        'Bago Sow Room',
        'BSR1',
        'Breeding sow room',
        909003,
        1,
        45,
        NOW()
    ),
    (
        913005,
        'Bago Piglet Room',
        'BPR1',
        'Piglet nursery room',
        909003,
        1,
        130,
        NOW()
    ),
    (
        913006,
        'Bago Grower Room',
        'BGR1',
        'Grower and finisher room',
        909004,
        1,
        170,
        NOW()
    );
-- =========================================================
-- USERS
-- Password = 123123
-- =========================================================
INSERT INTO users (
        id,
        name,
        email,
        password,
        role,
        farm_id,
        building_id,
        start_date,
        phone,
        profile_image_path,
        enabled,
        must_change_password,
        created_at,
        updated_at
    )
VALUES -- ADMIN
    (
        910001,
        'System Admin',
        'admin@swinecore.mm',
        '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
        'ADMIN',
        NULL,
        NULL,
        CURDATE(),
        '09400100001',
        '/images/logo.png',
        1,
        0,
        NOW(),
        NOW()
    ),
    -- HR
    (
        910002,
        'HR Officer',
        'hr@swinecore.mm',
        '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
        'HR',
        NULL,
        NULL,
        CURDATE(),
        '09400100002',
        '/images/logo.png',
        1,
        0,
        NOW(),
        NOW()
    ),
    -- HMAWBI MANAGER
    (
        910003,
        'U Aung Kyaw',
        'manager.hmawbi@swinecore.mm',
        '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
        'MANAGER',
        908001,
        NULL,
        CURDATE(),
        '09420110003',
        '/images/logo.png',
        1,
        0,
        NOW(),
        NOW()
    ),
    -- HMAWBI SUPERVISOR
    (
        910004,
        'Daw May Thu',
        'supervisor.hmawbi@swinecore.mm',
        '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
        'SUPERVISOR',
        908001,
        909001,
        CURDATE(),
        '09420110004',
        '/images/logo.png',
        1,
        0,
        NOW(),
        NOW()
    ),
    -- HMAWBI BREEDING STAFF
    (
        910005,
        'Ko Min Naing',
        'staff.hmawbi.breeding@swinecore.mm',
        '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
        'STAFF',
        908001,
        909001,
        CURDATE(),
        '09420110005',
        '/images/logo.png',
        1,
        0,
        NOW(),
        NOW()
    ),
    -- HMAWBI GROWER STAFF
    (
        910006,
        'Ko Zaw Lin',
        'staff.hmawbi.grower@swinecore.mm',
        '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
        'STAFF',
        908001,
        909002,
        CURDATE(),
        '09420110006',
        '/images/logo.png',
        1,
        0,
        NOW(),
        NOW()
    ),
    -- BAGO MANAGER
    (
        910007,
        'U Thet Paing',
        'manager.bago@swinecore.mm',
        '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
        'MANAGER',
        908002,
        NULL,
        CURDATE(),
        '09430220007',
        '/images/logo.png',
        1,
        0,
        NOW(),
        NOW()
    ),
    -- BAGO SUPERVISOR
    (
        910008,
        'Daw Ei Mon',
        'supervisor.bago@swinecore.mm',
        '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
        'SUPERVISOR',
        908002,
        909003,
        CURDATE(),
        '09430220008',
        '/images/logo.png',
        1,
        0,
        NOW(),
        NOW()
    ),
    -- BAGO BREEDING STAFF
    (
        910009,
        'Ko Hein Htet',
        'staff.bago.breeding@swinecore.mm',
        '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
        'STAFF',
        908002,
        909003,
        CURDATE(),
        '09430220009',
        '/images/logo.png',
        1,
        0,
        NOW(),
        NOW()
    ),
    -- BAGO GROWER STAFF
    (
        910010,
        'Ko Nyi Nyi',
        'staff.bago.grower@swinecore.mm',
        '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
        'STAFF',
        908002,
        909004,
        CURDATE(),
        '09430220010',
        '/images/logo.png',
        1,
        0,
        NOW(),
        NOW()
    );
-- =========================================================
-- CUSTOMER ACCOUNT
-- =========================================================
INSERT INTO customer_accounts (
        id,
        name,
        email,
        password,
        phone,
        address,
        profile_image_path,
        enabled,
        created_at
    )
VALUES (
        912001,
        'Yangon Meat Shop Buyer',
        'buyer.yangon@swinecore.mm',
        '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
        '09455300001',
        'Bayintnaung Market, Mayangone Township, Yangon',
        '/images/logo.png',
        1,
        NOW()
    );
-- =========================================================
-- SHIFTS
-- =========================================================
INSERT INTO shifts (
        id,
        name,
        farm_id,
        standard_start,
        standard_end,
        grace_period_minutes,
        minimum_required_hours,
        active
    )
VALUES (
        914001,
        'Hmawbi Day Shift',
        908001,
        '08:00:00',
        '17:00:00',
        15,
        8.0,
        1
    ),
    (
        914002,
        'Bago Day Shift',
        908002,
        '08:00:00',
        '17:00:00',
        15,
        8.0,
        1
    );
-- =========================================================
-- STAFF SHIFTS
-- =========================================================
INSERT INTO staff_shifts (
        id,
        user_id,
        shift_id,
        effective_date,
        end_date,
        status
    )
VALUES (
        915001,
        910005,
        914001,
        CURDATE(),
        NULL,
        'ACTIVE'
    ),
    (
        915002,
        910006,
        914001,
        CURDATE(),
        NULL,
        'ACTIVE'
    ),
    (
        915003,
        910009,
        914002,
        CURDATE(),
        NULL,
        'ACTIVE'
    ),
    (
        915004,
        910010,
        914002,
        CURDATE(),
        NULL,
        'ACTIVE'
    );
-- =========================================================
-- ATTENDANCE
-- =========================================================
INSERT INTO attendance (
        id,
        user_id,
        work_date,
        clock_in_time,
        clock_out_time,
        attended,
        clocked_out,
        status,
        early_departure_reason,
        wage_deduction_amount,
        created_at
    )
VALUES (
        960001,
        910005,
        CURDATE(),
        CONCAT(CURDATE(), ' 08:00:00'),
        NULL,
        1,
        0,
        'NORMAL',
        NULL,
        NULL,
        NOW()
    ),
    (
        960002,
        910006,
        CURDATE(),
        CONCAT(CURDATE(), ' 08:00:00'),
        NULL,
        1,
        0,
        'NORMAL',
        NULL,
        NULL,
        NOW()
    ),
    (
        960003,
        910009,
        CURDATE(),
        CONCAT(CURDATE(), ' 08:00:00'),
        NULL,
        1,
        0,
        'NORMAL',
        NULL,
        NULL,
        NOW()
    ),
    (
        960004,
        910010,
        CURDATE(),
        CONCAT(CURDATE(), ' 08:00:00'),
        NULL,
        1,
        0,
        'NORMAL',
        NULL,
        NULL,
        NOW()
    );
-- =========================================================
-- RULE SCHEDULES
-- =========================================================
INSERT INTO rule_schedules (
        id,
        name,
        rule_type,
        day_from_birth,
        day_range_start,
        day_range_end,
        feed_amount_kg,
        feed_type,
        medication,
        dosage,
        administration_route,
        fixed_target_day,
        breeding_month,
        applies_to,
        description,
        active,
        farm_id,
        created_by,
        created_at
    )
VALUES (
        920001,
        'Hmawbi Day 1 Iron Supplement',
        'MEDICATION',
        1,
        NULL,
        NULL,
        NULL,
        NULL,
        'Iron Supplement',
        '2 ml',
        'ORAL',
        1,
        NULL,
        'PIGLET',
        'Newborn piglet iron supplement.',
        1,
        908001,
        910003,
        NOW()
    ),
    (
        920002,
        'Hmawbi Day 30 PRRS Vaccine',
        'VACCINATION',
        30,
        NULL,
        NULL,
        NULL,
        NULL,
        'PRRS Vaccine',
        '2 ml',
        'INTRAMUSCULAR',
        30,
        NULL,
        'PIGLET',
        'Piglet vaccination schedule.',
        1,
        908001,
        910003,
        NOW()
    ),
    (
        920003,
        'Hmawbi Grower Feed Program',
        'FEEDING',
        NULL,
        60,
        120,
        2.5,
        'GROWER_FEED',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'GROWER',
        'Daily grower feeding program.',
        1,
        908001,
        910003,
        NOW()
    ),
    (
        920004,
        'Bago Day 1 Iron Supplement',
        'MEDICATION',
        1,
        NULL,
        NULL,
        NULL,
        NULL,
        'Iron Supplement',
        '2 ml',
        'ORAL',
        1,
        NULL,
        'PIGLET',
        'Newborn piglet iron supplement.',
        1,
        908002,
        910007,
        NOW()
    ),
    (
        920005,
        'Bago Day 28 Mycoplasma Vaccine',
        'VACCINATION',
        28,
        NULL,
        NULL,
        NULL,
        NULL,
        'Mycoplasma Vaccine',
        '2 ml',
        'INTRAMUSCULAR',
        28,
        NULL,
        'PIGLET',
        'Piglet vaccination schedule.',
        1,
        908002,
        910007,
        NOW()
    ),
    (
        920006,
        'Bago Finisher Feed Program',
        'FEEDING',
        NULL,
        121,
        180,
        3.2,
        'FINISHER_FEED',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'FINISHER',
        'Daily finisher feeding program.',
        1,
        908002,
        910007,
        NOW()
    );
-- =========================================================
-- INVENTORY
-- =========================================================
INSERT INTO inventory (
        id,
        farm_id,
        feed_type,
        quantity_kg,
        alert_threshold_kg,
        alert_triggered,
        updated_at
    )
VALUES (
        930001,
        908001,
        'Iron Supplement',
        60.0,
        5.0,
        0,
        NOW()
    ),
    (
        930002,
        908001,
        'PRRS Vaccine',
        120.0,
        10.0,
        0,
        NOW()
    ),
    (
        930003,
        908001,
        'GROWER_FEED',
        3000.0,
        500.0,
        0,
        NOW()
    ),
    (
        930004,
        908002,
        'Iron Supplement',
        55.0,
        5.0,
        0,
        NOW()
    ),
    (
        930005,
        908002,
        'Mycoplasma Vaccine',
        100.0,
        10.0,
        0,
        NOW()
    ),
    (
        930006,
        908002,
        'FINISHER_FEED',
        3500.0,
        600.0,
        0,
        NOW()
    );
-- PIGS
-- BUILDING 909002
-- HMAWBI GROWER HOUSE B
-- TOTAL 20 PIGS
-- =========================================================
INSERT INTO pigs (
        id,
        code,
        gender,
        status,
        building_id,
        genetics_id,
        mother_id,
        birth_record_id,
        birth_date,
        recorded_date,
        sold_date,
        listed_for_sale_date,
        current_weight,
        photo_path,
        notes,
        listed_for_sale,
        sale_price,
        created_at
    )
VALUES -- =========================
    -- GROWER 10
    -- =========================
    (
        950021,
        'HMB-GROWER-001',
        'MALE',
        'GROWER',
        909002,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 90 DAY),
        CURDATE(),
        NULL,
        NULL,
        45.0,
        '/images/pigs/hmb-grower-001.jpg',
        'Grower pig for feeding demo.',
        0,
        NULL,
        NOW()
    ),
    (
        950022,
        'HMB-GROWER-002',
        'FEMALE',
        'GROWER',
        909002,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 95 DAY),
        CURDATE(),
        NULL,
        NULL,
        48.0,
        '/images/pigs/hmb-grower-002.jpg',
        'Grower pig for feeding demo.',
        0,
        NULL,
        NOW()
    ),
    (
        950023,
        'HMB-GROWER-003',
        'MALE',
        'GROWER',
        909002,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 100 DAY),
        CURDATE(),
        NULL,
        NULL,
        52.0,
        '/images/pigs/hmb-grower-003.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950024,
        'HMB-GROWER-004',
        'FEMALE',
        'GROWER',
        909002,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 105 DAY),
        CURDATE(),
        NULL,
        NULL,
        55.0,
        '/images/pigs/hmb-grower-004.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950025,
        'HMB-GROWER-005',
        'MALE',
        'GROWER',
        909002,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 110 DAY),
        CURDATE(),
        NULL,
        NULL,
        58.0,
        '/images/pigs/hmb-grower-005.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950026,
        'HMB-GROWER-006',
        'FEMALE',
        'GROWER',
        909002,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 115 DAY),
        CURDATE(),
        NULL,
        NULL,
        60.0,
        '/images/pigs/hmb-grower-006.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950027,
        'HMB-GROWER-007',
        'MALE',
        'GROWER',
        909002,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 120 DAY),
        CURDATE(),
        NULL,
        NULL,
        62.0,
        '/images/pigs/hmb-grower-007.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950028,
        'HMB-GROWER-008',
        'FEMALE',
        'GROWER',
        909002,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 125 DAY),
        CURDATE(),
        NULL,
        NULL,
        65.0,
        '/images/pigs/hmb-grower-008.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950029,
        'HMB-GROWER-009',
        'MALE',
        'GROWER',
        909002,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 130 DAY),
        CURDATE(),
        NULL,
        NULL,
        68.0,
        '/images/pigs/hmb-grower-009.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950030,
        'HMB-GROWER-010',
        'FEMALE',
        'GROWER',
        909002,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 135 DAY),
        CURDATE(),
        NULL,
        NULL,
        70.0,
        '/images/pigs/hmb-grower-010.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    -- =========================
    -- FINISHER 6
    -- =========================
    (
        950031,
        'HMB-FINISHER-001',
        'MALE',
        'FINISHER',
        909002,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 150 DAY),
        CURDATE(),
        NULL,
        NULL,
        85.0,
        '/images/pigs/hmb-finisher-001.jpg',
        'Finisher pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950032,
        'HMB-FINISHER-002',
        'FEMALE',
        'FINISHER',
        909002,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 155 DAY),
        CURDATE(),
        NULL,
        NULL,
        90.0,
        '/images/pigs/hmb-finisher-002.jpg',
        'Finisher pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950033,
        'HMB-FINISHER-003',
        'MALE',
        'FINISHER',
        909002,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 160 DAY),
        CURDATE(),
        NULL,
        NULL,
        95.0,
        '/images/pigs/hmb-finisher-003.jpg',
        'Finisher pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950034,
        'HMB-FINISHER-004',
        'FEMALE',
        'FINISHER',
        909002,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 165 DAY),
        CURDATE(),
        NULL,
        NULL,
        100.0,
        '/images/pigs/hmb-finisher-004.jpg',
        'Finisher pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950035,
        'HMB-FINISHER-005',
        'MALE',
        'FINISHER',
        909002,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 170 DAY),
        CURDATE(),
        NULL,
        NULL,
        105.0,
        '/images/pigs/hmb-finisher-005.jpg',
        'Finisher pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950036,
        'HMB-FINISHER-006',
        'FEMALE',
        'FINISHER',
        909002,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 175 DAY),
        CURDATE(),
        NULL,
        NULL,
        110.0,
        '/images/pigs/hmb-finisher-006.jpg',
        'Finisher pig.',
        0,
        NULL,
        NOW()
    ),
    -- =========================
    -- FOR SALE 2
    -- =========================
    (
        950037,
        'HMB-MARKET-001',
        'MALE',
        'FOR_SALE',
        909002,
        @GEN_D,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 180 DAY),
        CURDATE(),
        NULL,
        CURDATE(),
        115.0,
        '/images/pigs/hmb-market-001.jpg',
        'Ready for marketplace sale.',
        1,
        450000,
        NOW()
    ),
    (
        950038,
        'HMB-MARKET-002',
        'FEMALE',
        'FOR_SALE',
        909002,
        @GEN_D,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 185 DAY),
        CURDATE(),
        NULL,
        CURDATE(),
        120.0,
        '/images/pigs/hmb-market-002.jpg',
        'Ready for marketplace sale.',
        1,
        480000,
        NOW()
    ),
    -- =========================
    -- PENDING SALE APPROVAL 2
    -- =========================
    (
        950039,
        'HMB-PENDING-001',
        'MALE',
        'PENDING_SALE_APPROVAL',
        909002,
        @GEN_D,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 170 DAY),
        CURDATE(),
        NULL,
        NULL,
        108.0,
        '/images/pigs/hmb-pending-001.jpg',
        'Waiting supervisor approval.',
        0,
        420000,
        NOW()
    ),
    (
        950040,
        'HMB-PENDING-002',
        'FEMALE',
        'PENDING_SALE_APPROVAL',
        909002,
        @GEN_D,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 175 DAY),
        CURDATE(),
        NULL,
        NULL,
        112.0,
        '/images/pigs/hmb-pending-002.jpg',
        'Waiting supervisor approval.',
        0,
        430000,
        NOW()
    );
-- =========================================================
-- PIGS
-- BUILDING 909003
-- BAGO BREEDING HOUSE A
-- TOTAL 20 PIGS
-- =========================================================
INSERT INTO pigs (
        id,
        code,
        gender,
        status,
        building_id,
        genetics_id,
        mother_id,
        birth_record_id,
        birth_date,
        recorded_date,
        sold_date,
        listed_for_sale_date,
        current_weight,
        photo_path,
        notes,
        listed_for_sale,
        sale_price,
        created_at
    )
VALUES -- =========================
    -- BREEDING SOW 8
    -- =========================
    (
        950041,
        'BGO-SOW-001',
        'FEMALE',
        'BREEDING_SOW',
        909003,
        @GEN_YL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 760 DAY),
        CURDATE(),
        NULL,
        NULL,
        190.0,
        '/images/pigs/bgo-sow-001.jpg',
        'Healthy breeding sow. Ready for mating.',
        0,
        NULL,
        NOW()
    ),
    (
        950042,
        'BGO-SOW-002',
        'FEMALE',
        'BREEDING_SOW',
        909003,
        @GEN_YL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 730 DAY),
        CURDATE(),
        NULL,
        NULL,
        186.0,
        '/images/pigs/bgo-sow-002.jpg',
        'Healthy breeding sow.',
        0,
        NULL,
        NOW()
    ),
    (
        950043,
        'BGO-SOW-003',
        'FEMALE',
        'BREEDING_SOW',
        909003,
        @GEN_YL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 700 DAY),
        CURDATE(),
        NULL,
        NULL,
        188.0,
        '/images/pigs/bgo-sow-003.jpg',
        'Healthy breeding sow.',
        0,
        NULL,
        NOW()
    ),
    (
        950044,
        'BGO-SOW-004',
        'FEMALE',
        'BREEDING_SOW',
        909003,
        @GEN_YL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 680 DAY),
        CURDATE(),
        NULL,
        NULL,
        193.0,
        '/images/pigs/bgo-sow-004.jpg',
        'Healthy breeding sow.',
        0,
        NULL,
        NOW()
    ),
    (
        950045,
        'BGO-SOW-005',
        'FEMALE',
        'BREEDING_SOW',
        909003,
        @GEN_YL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 650 DAY),
        CURDATE(),
        NULL,
        NULL,
        181.0,
        '/images/pigs/bgo-sow-005.jpg',
        'Healthy breeding sow.',
        0,
        NULL,
        NOW()
    ),
    (
        950046,
        'BGO-SOW-006',
        'FEMALE',
        'BREEDING_SOW',
        909003,
        @GEN_YL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 620 DAY),
        CURDATE(),
        NULL,
        NULL,
        184.0,
        '/images/pigs/bgo-sow-006.jpg',
        'Healthy breeding sow.',
        0,
        NULL,
        NOW()
    ),
    (
        950047,
        'BGO-SOW-007',
        'FEMALE',
        'BREEDING_SOW',
        909003,
        @GEN_YL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 600 DAY),
        CURDATE(),
        NULL,
        NULL,
        187.0,
        '/images/pigs/bgo-sow-007.jpg',
        'Healthy breeding sow.',
        0,
        NULL,
        NOW()
    ),
    (
        950048,
        'BGO-SOW-008',
        'FEMALE',
        'BREEDING_SOW',
        909003,
        @GEN_YL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 590 DAY),
        CURDATE(),
        NULL,
        NULL,
        189.0,
        '/images/pigs/bgo-sow-008.jpg',
        'Healthy breeding sow.',
        0,
        NULL,
        NOW()
    ),
    -- =========================
    -- BREEDING BOAR 2
    -- =========================
    (
        950049,
        'BGO-BOAR-001',
        'MALE',
        'BREEDING_BOAR',
        909003,
        @GEN_D,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 700 DAY),
        CURDATE(),
        NULL,
        NULL,
        245.0,
        '/images/pigs/bgo-boar-001.jpg',
        'Active breeding boar.',
        0,
        NULL,
        NOW()
    ),
    (
        950050,
        'BGO-BOAR-002',
        'MALE',
        'BREEDING_BOAR',
        909003,
        @GEN_D,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 680 DAY),
        CURDATE(),
        NULL,
        NULL,
        238.0,
        '/images/pigs/bgo-boar-002.jpg',
        'Active breeding boar.',
        0,
        NULL,
        NOW()
    ),
    -- =========================
    -- PIGLET 6
    -- =========================
    (
        950051,
        'BGO-PIGLET-001',
        'MALE',
        'PIGLET',
        909003,
        @GEN_Y,
        950041,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 15 DAY),
        CURDATE(),
        NULL,
        NULL,
        7.6,
        '/images/pigs/bgo-piglet-001.jpg',
        'New born piglet.',
        0,
        NULL,
        NOW()
    ),
    (
        950052,
        'BGO-PIGLET-002',
        'FEMALE',
        'PIGLET',
        909003,
        @GEN_Y,
        950041,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 15 DAY),
        CURDATE(),
        NULL,
        NULL,
        7.4,
        '/images/pigs/bgo-piglet-002.jpg',
        'New born piglet.',
        0,
        NULL,
        NOW()
    ),
    (
        950053,
        'BGO-PIGLET-003',
        'MALE',
        'PIGLET',
        909003,
        @GEN_Y,
        950042,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 20 DAY),
        CURDATE(),
        NULL,
        NULL,
        8.1,
        '/images/pigs/bgo-piglet-003.jpg',
        'New born piglet.',
        0,
        NULL,
        NOW()
    ),
    (
        950054,
        'BGO-PIGLET-004',
        'FEMALE',
        'PIGLET',
        909003,
        @GEN_Y,
        950042,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 20 DAY),
        CURDATE(),
        NULL,
        NULL,
        7.9,
        '/images/pigs/bgo-piglet-004.jpg',
        'New born piglet.',
        0,
        NULL,
        NOW()
    ),
    (
        950055,
        'BGO-PIGLET-005',
        'MALE',
        'PIGLET',
        909003,
        @GEN_Y,
        950043,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 25 DAY),
        CURDATE(),
        NULL,
        NULL,
        8.3,
        '/images/pigs/bgo-piglet-005.jpg',
        'New born piglet.',
        0,
        NULL,
        NOW()
    ),
    (
        950056,
        'BGO-PIGLET-006',
        'FEMALE',
        'PIGLET',
        909003,
        @GEN_Y,
        950043,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 25 DAY),
        CURDATE(),
        NULL,
        NULL,
        8.0,
        '/images/pigs/bgo-piglet-006.jpg',
        'New born piglet.',
        0,
        NULL,
        NOW()
    ),
    -- =========================
    -- GROWER 4
    -- =========================
    (
        950057,
        'BGO-GROWER-001',
        'MALE',
        'GROWER',
        909003,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 90 DAY),
        CURDATE(),
        NULL,
        NULL,
        46.0,
        '/images/pigs/bgo-grower-001.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950058,
        'BGO-GROWER-002',
        'FEMALE',
        'GROWER',
        909003,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 95 DAY),
        CURDATE(),
        NULL,
        NULL,
        50.0,
        '/images/pigs/bgo-grower-002.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950059,
        'BGO-GROWER-003',
        'MALE',
        'GROWER',
        909003,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 100 DAY),
        CURDATE(),
        NULL,
        NULL,
        53.0,
        '/images/pigs/bgo-grower-003.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950060,
        'BGO-GROWER-004',
        'FEMALE',
        'GROWER',
        909003,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 105 DAY),
        CURDATE(),
        NULL,
        NULL,
        56.0,
        '/images/pigs/bgo-grower-004.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    );
-- =========================================================
-- PIGS
-- BUILDING 909004
-- BAGO GROWER HOUSE B
-- TOTAL 20 PIGS
-- =========================================================
INSERT INTO pigs (
        id,
        code,
        gender,
        status,
        building_id,
        genetics_id,
        mother_id,
        birth_record_id,
        birth_date,
        recorded_date,
        sold_date,
        listed_for_sale_date,
        current_weight,
        photo_path,
        notes,
        listed_for_sale,
        sale_price,
        created_at
    )
VALUES -- =========================================================
    -- GROWER 10
    -- =========================================================
    (
        950061,
        'BGO-GROWER-001',
        'MALE',
        'GROWER',
        909004,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 90 DAY),
        CURDATE(),
        NULL,
        NULL,
        45.0,
        '/images/pigs/bgo-grower-001.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950062,
        'BGO-GROWER-002',
        'FEMALE',
        'GROWER',
        909004,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 95 DAY),
        CURDATE(),
        NULL,
        NULL,
        48.0,
        '/images/pigs/bgo-grower-002.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950063,
        'BGO-GROWER-003',
        'MALE',
        'GROWER',
        909004,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 100 DAY),
        CURDATE(),
        NULL,
        NULL,
        52.0,
        '/images/pigs/bgo-grower-003.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950064,
        'BGO-GROWER-004',
        'FEMALE',
        'GROWER',
        909004,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 105 DAY),
        CURDATE(),
        NULL,
        NULL,
        55.0,
        '/images/pigs/bgo-grower-004.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950065,
        'BGO-GROWER-005',
        'MALE',
        'GROWER',
        909004,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 110 DAY),
        CURDATE(),
        NULL,
        NULL,
        58.0,
        '/images/pigs/bgo-grower-005.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950066,
        'BGO-GROWER-006',
        'FEMALE',
        'GROWER',
        909004,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 115 DAY),
        CURDATE(),
        NULL,
        NULL,
        60.0,
        '/images/pigs/bgo-grower-006.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950067,
        'BGO-GROWER-007',
        'MALE',
        'GROWER',
        909004,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 120 DAY),
        CURDATE(),
        NULL,
        NULL,
        62.0,
        '/images/pigs/bgo-grower-007.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950068,
        'BGO-GROWER-008',
        'FEMALE',
        'GROWER',
        909004,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 125 DAY),
        CURDATE(),
        NULL,
        NULL,
        65.0,
        '/images/pigs/bgo-grower-008.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950069,
        'BGO-GROWER-009',
        'MALE',
        'GROWER',
        909004,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 130 DAY),
        CURDATE(),
        NULL,
        NULL,
        68.0,
        '/images/pigs/bgo-grower-009.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950070,
        'BGO-GROWER-010',
        'FEMALE',
        'GROWER',
        909004,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 135 DAY),
        CURDATE(),
        NULL,
        NULL,
        70.0,
        '/images/pigs/bgo-grower-010.jpg',
        'Grower pig.',
        0,
        NULL,
        NOW()
    ),
    -- =========================================================
    -- FINISHER 6
    -- =========================================================
    (
        950071,
        'BGO-FINISHER-001',
        'MALE',
        'FINISHER',
        909004,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 150 DAY),
        CURDATE(),
        NULL,
        NULL,
        85.0,
        '/images/pigs/bgo-finisher-001.jpg',
        'Finisher pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950072,
        'BGO-FINISHER-002',
        'FEMALE',
        'FINISHER',
        909004,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 155 DAY),
        CURDATE(),
        NULL,
        NULL,
        90.0,
        '/images/pigs/bgo-finisher-002.jpg',
        'Finisher pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950073,
        'BGO-FINISHER-003',
        'MALE',
        'FINISHER',
        909004,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 160 DAY),
        CURDATE(),
        NULL,
        NULL,
        95.0,
        '/images/pigs/bgo-finisher-003.jpg',
        'Finisher pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950074,
        'BGO-FINISHER-004',
        'FEMALE',
        'FINISHER',
        909004,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 165 DAY),
        CURDATE(),
        NULL,
        NULL,
        100.0,
        '/images/pigs/bgo-finisher-004.jpg',
        'Finisher pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950075,
        'BGO-FINISHER-005',
        'MALE',
        'FINISHER',
        909004,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 170 DAY),
        CURDATE(),
        NULL,
        NULL,
        105.0,
        '/images/pigs/bgo-finisher-005.jpg',
        'Finisher pig.',
        0,
        NULL,
        NOW()
    ),
    (
        950076,
        'BGO-FINISHER-006',
        'FEMALE',
        'FINISHER',
        909004,
        @GEN_DL,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 175 DAY),
        CURDATE(),
        NULL,
        NULL,
        110.0,
        '/images/pigs/bgo-finisher-006.jpg',
        'Finisher pig.',
        0,
        NULL,
        NOW()
    ),
    -- =========================================================
    -- FOR SALE 2
    -- =========================================================
    (
        950077,
        'BGO-MARKET-001',
        'MALE',
        'FOR_SALE',
        909004,
        @GEN_D,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 180 DAY),
        CURDATE(),
        NULL,
        CURDATE(),
        115.0,
        '/images/pigs/bgo-market-001.jpg',
        'Ready for marketplace sale.',
        1,
        460000,
        NOW()
    ),
    (
        950078,
        'BGO-MARKET-002',
        'FEMALE',
        'FOR_SALE',
        909004,
        @GEN_D,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 185 DAY),
        CURDATE(),
        NULL,
        CURDATE(),
        120.0,
        '/images/pigs/bgo-market-002.jpg',
        'Ready for marketplace sale.',
        1,
        490000,
        NOW()
    ),
    -- =========================================================
    -- PENDING SALE APPROVAL 2
    -- =========================================================
    (
        950079,
        'BGO-PENDING-001',
        'MALE',
        'PENDING_SALE_APPROVAL',
        909004,
        @GEN_D,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 170 DAY),
        CURDATE(),
        NULL,
        NULL,
        108.0,
        '/images/pigs/bgo-pending-001.jpg',
        'Waiting supervisor approval.',
        0,
        430000,
        NOW()
    ),
    (
        950080,
        'BGO-PENDING-002',
        'FEMALE',
        'PENDING_SALE_APPROVAL',
        909004,
        @GEN_D,
        NULL,
        NULL,
        DATE_SUB(CURDATE(), INTERVAL 175 DAY),
        CURDATE(),
        NULL,
        NULL,
        112.0,
        '/images/pigs/bgo-pending-002.jpg',
        'Waiting supervisor approval.',
        0,
        440000,
        NOW()
    );
-- =========================================================
-- BIRTH RECORDS
-- =========================================================
INSERT INTO birth_records (
        id,
        mother_id,
        building_id,
        recorded_by,
        birth_date,
        litter_size,
        alive_piglets,
        dead_piglets,
        confirmed_by_staff,
        confirmed_by_supervisor,
        created_at
    )
VALUES (
        955001,
        950001,
        909001,
        910005,
        CURDATE(),
        6,
        5,
        1,
        1,
        0,
        NOW()
    ),
    (
        955002,
        950003,
        909001,
        910005,
        CURDATE(),
        5,
        4,
        1,
        1,
        0,
        NOW()
    ),
    (
        955003,
        950041,
        909003,
        910009,
        CURDATE(),
        6,
        5,
        1,
        1,
        0,
        NOW()
    ),
    (
        955004,
        950043,
        909003,
        910009,
        CURDATE(),
        5,
        4,
        1,
        1,
        0,
        NOW()
    );
-- =========================================================
-- NEWBORN PIGS FROM BIRTH RECORD
-- =========================================================
INSERT INTO pigs (
        id,
        code,
        gender,
        status,
        building_id,
        genetics_id,
        mother_id,
        birth_record_id,
        birth_date,
        recorded_date,
        sold_date,
        listed_for_sale_date,
        current_weight,
        photo_path,
        notes,
        listed_for_sale,
        sale_price,
        created_at
    )
VALUES -- HMAWBI BIRTH RECORD 955001
    -- Mother 950001
    (
        950081,
        'HMB-BIRTH-001-A',
        'MALE',
        'PIGLET',
        909001,
        @GEN_Y,
        950001,
        955001,
        CURDATE(),
        CURDATE(),
        NULL,
        NULL,
        1.5,
        '/images/pigs/newborn-hmb-001-a.jpg',
        'Born from Hmawbi birth record.',
        0,
        NULL,
        NOW()
    ),
    (
        950082,
        'HMB-BIRTH-001-B',
        'FEMALE',
        'PIGLET',
        909001,
        @GEN_Y,
        950001,
        955001,
        CURDATE(),
        CURDATE(),
        NULL,
        NULL,
        1.4,
        '/images/pigs/newborn-hmb-001-b.jpg',
        'Born from Hmawbi birth record.',
        0,
        NULL,
        NOW()
    ),
    (
        950083,
        'HMB-BIRTH-001-C',
        'MALE',
        'PIGLET',
        909001,
        @GEN_Y,
        950001,
        955001,
        CURDATE(),
        CURDATE(),
        NULL,
        NULL,
        1.3,
        '/images/pigs/newborn-hmb-001-c.jpg',
        'Born from Hmawbi birth record.',
        0,
        NULL,
        NOW()
    ),
    (
        950084,
        'HMB-BIRTH-001-D',
        'FEMALE',
        'PIGLET',
        909001,
        @GEN_Y,
        950001,
        955001,
        CURDATE(),
        CURDATE(),
        NULL,
        NULL,
        1.5,
        '/images/pigs/newborn-hmb-001-d.jpg',
        'Born from Hmawbi birth record.',
        0,
        NULL,
        NOW()
    ),
    (
        950085,
        'HMB-BIRTH-001-E',
        'MALE',
        'PIGLET',
        909001,
        @GEN_Y,
        950001,
        955001,
        CURDATE(),
        CURDATE(),
        NULL,
        NULL,
        1.2,
        '/images/pigs/newborn-hmb-001-e.jpg',
        'Born from Hmawbi birth record.',
        0,
        NULL,
        NOW()
    ),
    -- HMAWBI BIRTH RECORD 955002
    (
        950086,
        'HMB-BIRTH-002-A',
        'FEMALE',
        'PIGLET',
        909001,
        @GEN_Y,
        950003,
        955002,
        CURDATE(),
        CURDATE(),
        NULL,
        NULL,
        1.4,
        '/images/pigs/newborn-hmb-002-a.jpg',
        'Born from Hmawbi birth record.',
        0,
        NULL,
        NOW()
    ),
    (
        950087,
        'HMB-BIRTH-002-B',
        'MALE',
        'PIGLET',
        909001,
        @GEN_Y,
        950003,
        955002,
        CURDATE(),
        CURDATE(),
        NULL,
        NULL,
        1.3,
        '/images/pigs/newborn-hmb-002-b.jpg',
        'Born from Hmawbi birth record.',
        0,
        NULL,
        NOW()
    ),
    (
        950088,
        'HMB-BIRTH-002-C',
        'FEMALE',
        'PIGLET',
        909001,
        @GEN_Y,
        950003,
        955002,
        CURDATE(),
        CURDATE(),
        NULL,
        NULL,
        1.5,
        '/images/pigs/newborn-hmb-002-c.jpg',
        'Born from Hmawbi birth record.',
        0,
        NULL,
        NOW()
    ),
    (
        950089,
        'HMB-BIRTH-002-D',
        'MALE',
        'PIGLET',
        909001,
        @GEN_Y,
        950003,
        955002,
        CURDATE(),
        CURDATE(),
        NULL,
        NULL,
        1.4,
        '/images/pigs/newborn-hmb-002-d.jpg',
        'Born from Hmawbi birth record.',
        0,
        NULL,
        NOW()
    ),
    -- BAGO BIRTH RECORD 955003
    (
        950090,
        'BGO-BIRTH-001-A',
        'MALE',
        'PIGLET',
        909003,
        @GEN_Y,
        950041,
        955003,
        CURDATE(),
        CURDATE(),
        NULL,
        NULL,
        1.5,
        '/images/pigs/newborn-bgo-001-a.jpg',
        'Born from Bago birth record.',
        0,
        NULL,
        NOW()
    ),
    (
        950091,
        'BGO-BIRTH-001-B',
        'FEMALE',
        'PIGLET',
        909003,
        @GEN_Y,
        950041,
        955003,
        CURDATE(),
        CURDATE(),
        NULL,
        NULL,
        1.4,
        '/images/pigs/newborn-bgo-001-b.jpg',
        'Born from Bago birth record.',
        0,
        NULL,
        NOW()
    ),
    (
        950092,
        'BGO-BIRTH-001-C',
        'MALE',
        'PIGLET',
        909003,
        @GEN_Y,
        950041,
        955003,
        CURDATE(),
        CURDATE(),
        NULL,
        NULL,
        1.3,
        '/images/pigs/newborn-bgo-001-c.jpg',
        'Born from Bago birth record.',
        0,
        NULL,
        NOW()
    ),
    (
        950093,
        'BGO-BIRTH-001-D',
        'FEMALE',
        'PIGLET',
        909003,
        @GEN_Y,
        950041,
        955003,
        CURDATE(),
        CURDATE(),
        NULL,
        NULL,
        1.5,
        '/images/pigs/newborn-bgo-001-d.jpg',
        'Born from Bago birth record.',
        0,
        NULL,
        NOW()
    ),
    (
        950094,
        'BGO-BIRTH-001-E',
        'MALE',
        'PIGLET',
        909003,
        @GEN_Y,
        950041,
        955003,
        CURDATE(),
        CURDATE(),
        NULL,
        NULL,
        1.2,
        '/images/pigs/newborn-bgo-001-e.jpg',
        'Born from Bago birth record.',
        0,
        NULL,
        NOW()
    ),
    -- BAGO BIRTH RECORD 955004
    (
        950095,
        'BGO-BIRTH-002-A',
        'FEMALE',
        'PIGLET',
        909003,
        @GEN_Y,
        950043,
        955004,
        CURDATE(),
        CURDATE(),
        NULL,
        NULL,
        1.4,
        '/images/pigs/newborn-bgo-002-a.jpg',
        'Born from Bago birth record.',
        0,
        NULL,
        NOW()
    ),
    (
        950096,
        'BGO-BIRTH-002-B',
        'MALE',
        'PIGLET',
        909003,
        @GEN_Y,
        950043,
        955004,
        CURDATE(),
        CURDATE(),
        NULL,
        NULL,
        1.3,
        '/images/pigs/newborn-bgo-002-b.jpg',
        'Born from Bago birth record.',
        0,
        NULL,
        NOW()
    ),
    (
        950097,
        'BGO-BIRTH-002-C',
        'FEMALE',
        'PIGLET',
        909003,
        @GEN_Y,
        950043,
        955004,
        CURDATE(),
        CURDATE(),
        NULL,
        NULL,
        1.5,
        '/images/pigs/newborn-bgo-002-c.jpg',
        'Born from Bago birth record.',
        0,
        NULL,
        NOW()
    ),
    (
        950098,
        'BGO-BIRTH-002-D',
        'MALE',
        'PIGLET',
        909003,
        @GEN_Y,
        950043,
        955004,
        CURDATE(),
        CURDATE(),
        NULL,
        NULL,
        1.4,
        '/images/pigs/newborn-bgo-002-d.jpg',
        'Born from Bago birth record.',
        0,
        NULL,
        NOW()
    );
-- =========================================================
-- DAILY TASKS
-- PROJECT SHOW DEMO DATA
-- =========================================================
INSERT INTO daily_tasks (
        id,
        building_id,
        assigned_staff_id,
        rule_schedule_id,
        pig_id,
        task_date,
        task_name,
        task_description,
        is_standard_task,
        status,
        staff_notes,
        supervisor_comments,
        submitted_at,
        reviewed_at,
        reviewed_by,
        created_at
    )
VALUES -- =========================================================
    -- HMAWBI BREEDING STAFF
    -- STAFF 910005
    -- BUILDING 909001
    -- =========================================================
    (
        970001,
        909001,
        910005,
        NULL,
        NULL,
        CURDATE(),
        'Daily Care Routine',
        'Check breeding sows, boars, piglet health, water and temperature.',
        1,
        'PENDING',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NOW()
    ),
    (
        970002,
        909001,
        910005,
        920001,
        950081,
        CURDATE(),
        'MEDICATION: Iron Supplement',
        'Give iron supplement to newborn piglet HMB-BIRTH-001-A.',
        0,
        'PENDING',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NOW()
    ),
    (
        970003,
        909001,
        910005,
        920002,
        950011,
        DATE_ADD(CURDATE(), INTERVAL 1 DAY),
        'VACCINATION: PRRS Vaccine',
        'Vaccinate HMB-PIGLET-001.',
        0,
        'PENDING',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NOW()
    ),
    -- APPROVED TASK DEMO
    (
        970004,
        909001,
        910005,
        NULL,
        950081,
        DATE_SUB(CURDATE(), INTERVAL 1 DAY),
        'Piglet Health Check',
        'Check newborn piglet condition.',
        0,
        'APPROVED',
        'Piglet active and feeding normally.',
        'Good work.',
        DATE_SUB(NOW(), INTERVAL 5 HOUR),
        DATE_SUB(NOW(), INTERVAL 3 HOUR),
        910004,
        NOW()
    ),
    -- =========================================================
    -- HMAWBI GROWER STAFF
    -- STAFF 910006
    -- BUILDING 909002
    -- =========================================================
    (
        970005,
        909002,
        910006,
        NULL,
        NULL,
        CURDATE(),
        'Daily Care Routine',
        'Check grower house, feed and water system.',
        1,
        'PENDING',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NOW()
    ),
    (
        970006,
        909002,
        910006,
        920003,
        950031,
        CURDATE(),
        'FEEDING: Grower Feed Program',
        'Give 2.5 kg grower feed to finisher/grower group.',
        0,
        'PENDING',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NOW()
    ),
    -- =========================================================
    -- BAGO BREEDING STAFF
    -- STAFF 910009
    -- BUILDING 909003
    -- =========================================================
    (
        970007,
        909003,
        910009,
        NULL,
        NULL,
        CURDATE(),
        'Daily Care Routine',
        'Check Bago breeding house and piglet area.',
        1,
        'PENDING',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NOW()
    ),
    (
        970008,
        909003,
        910009,
        920004,
        950090,
        CURDATE(),
        'MEDICATION: Iron Supplement',
        'Give iron supplement to Bago newborn piglet.',
        0,
        'PENDING',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NOW()
    ),
    (
        970009,
        909003,
        910009,
        920005,
        950051,
        DATE_ADD(CURDATE(), INTERVAL 1 DAY),
        'VACCINATION: Mycoplasma Vaccine',
        'Vaccinate BGO-PIGLET-001.',
        0,
        'PENDING',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NOW()
    ),
    -- =========================================================
    -- BAGO GROWER STAFF
    -- STAFF 910010
    -- BUILDING 909004
    -- =========================================================
    (
        970010,
        909004,
        910010,
        NULL,
        NULL,
        CURDATE(),
        'Daily Care Routine',
        'Check grower house, feed and market pigs.',
        1,
        'PENDING',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NOW()
    ),
    (
        970011,
        909004,
        910010,
        920006,
        950071,
        CURDATE(),
        'FEEDING: Finisher Feed Program',
        'Give finisher feed according to schedule.',
        0,
        'PENDING',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NOW()
    ),
    (
        970012,
        909004,
        910010,
        NULL,
        950077,
        DATE_SUB(CURDATE(), INTERVAL 1 DAY),
        'Market Pig Health Check',
        'Check FOR_SALE pig condition before customer order.',
        0,
        'APPROVED',
        'Pig weight and health checked.',
        'Approved for marketplace.',
        DATE_SUB(NOW(), INTERVAL 4 HOUR),
        DATE_SUB(NOW(), INTERVAL 2 HOUR),
        910008,
        NOW()
    );
-- =========================================================
-- FEED SHIPMENTS
-- PROJECT SHOW DEMO DATA
-- =========================================================
INSERT INTO feed_shipments (
        id,
        farm_id,
        received_by,
        feed_type,
        quantity_kg,
        price_per_kg,
        total_amount,
        shipment_date,
        supplier_name,
        invoice_number,
        dispatch_quantity_kg,
        verified,
        manager_confirmed,
        manager_rejected,
        manager_override_reason,
        payment_released,
        override_status,
        created_at
    )
VALUES -- =========================================================
    -- HMAWBI FARM
    -- =========================================================
    (
        940001,
        908001,
        NULL,
        '909001::SOW_FEED',
        1000.0,
        1550.0,
        1550000.0,
        CURDATE(),
        'Yangon Premium Feed Mill',
        'HMB-FEED-001',
        1000.0,
        0,
        0,
        0,
        NULL,
        0,
        'PENDING_VERIFICATION',
        NOW()
    ),
    (
        940002,
        908001,
        NULL,
        '909002::GROWER_FEED',
        2000.0,
        1420.0,
        2840000.0,
        CURDATE(),
        'Yangon Premium Feed Mill',
        'HMB-FEED-002',
        2000.0,
        0,
        0,
        0,
        NULL,
        0,
        'PENDING_VERIFICATION',
        NOW()
    ),
    -- =========================================================
    -- BAGO FARM
    -- =========================================================
    (
        940003,
        908002,
        NULL,
        '909003::PIGLET_FEED',
        1200.0,
        1680.0,
        2016000.0,
        CURDATE(),
        'Bago Agro Feed Factory',
        'BGO-FEED-001',
        1200.0,
        0,
        0,
        0,
        NULL,
        0,
        'PENDING_VERIFICATION',
        NOW()
    ),
    (
        940004,
        908002,
        NULL,
        '909004::FINISHER_FEED',
        2500.0,
        1390.0,
        3475000.0,
        CURDATE(),
        'Bago Agro Feed Factory',
        'BGO-FEED-002',
        2500.0,
        0,
        0,
        0,
        NULL,
        0,
        'PENDING_VERIFICATION',
        NOW()
    );
-- =========================================================
-- APPROVED SHIPMENT DEMO
-- =========================================================
INSERT INTO feed_shipments (
        id,
        farm_id,
        received_by,
        feed_type,
        quantity_kg,
        price_per_kg,
        total_amount,
        shipment_date,
        supplier_name,
        invoice_number,
        dispatch_quantity_kg,
        verified,
        manager_confirmed,
        manager_rejected,
        manager_override_reason,
        payment_released,
        override_status,
        created_at
    )
VALUES (
        940005,
        908001,
        910003,
        '909002::GROWER_FEED',
        1500.0,
        1400.0,
        2100000.0,
        DATE_SUB(CURDATE(), INTERVAL 2 DAY),
        'Yangon Premium Feed Mill',
        'HMB-FEED-003',
        1500.0,
        1,
        1,
        0,
        NULL,
        1,
        'CONFIRMED',
        DATE_SUB(NOW(), INTERVAL 2 DAY)
    ),
    (
        940006,
        908002,
        910007,
        '909004::FINISHER_FEED',
        2000.0,
        1380.0,
        2760000.0,
        DATE_SUB(CURDATE(), INTERVAL 3 DAY),
        'Bago Agro Feed Factory',
        'BGO-FEED-003',
        2000.0,
        1,
        1,
        0,
        NULL,
        1,
        'CONFIRMED',
        DATE_SUB(NOW(), INTERVAL 3 DAY)
    );
-- =========================================================
-- REJECTED SHIPMENT DEMO
-- =========================================================
INSERT INTO feed_shipments (
        id,
        farm_id,
        received_by,
        feed_type,
        quantity_kg,
        price_per_kg,
        total_amount,
        shipment_date,
        supplier_name,
        invoice_number,
        dispatch_quantity_kg,
        verified,
        manager_confirmed,
        manager_rejected,
        manager_override_reason,
        payment_released,
        override_status,
        created_at
    )
VALUES (
        940007,
        908001,
        910003,
        '909001::SOW_FEED',
        500.0,
        1600.0,
        800000.0,
        DATE_SUB(CURDATE(), INTERVAL 5 DAY),
        'Test Feed Supplier',
        'HMB-FEED-TEST',
        500.0,
        1,
        0,
        1,
        'Wrong invoice quantity.',
        0,
        'REJECTED',
        DATE_SUB(NOW(), INTERVAL 5 DAY)
    );
-- =========================================================
-- FINANCE TRANSACTIONS
-- PROJECT SHOW DEMO DATA
-- =========================================================
INSERT INTO finance_transactions (
        id,
        farm_id,
        type,
        category,
        amount,
        description,
        reference_id,
        status,
        created_at
    )
VALUES -- =========================================================
    -- HMAWBI FARM EXPENSE
    -- =========================================================
    (
        980001,
        908001,
        'EXPENSE',
        'FEED',
        1550000,
        'Hmawbi sow feed purchase',
        'HMB-FEED-001',
        'COMPLETED',
        NOW()
    ),
    (
        980002,
        908001,
        'EXPENSE',
        'FEED',
        2840000,
        'Hmawbi grower feed purchase',
        'HMB-FEED-002',
        'COMPLETED',
        NOW()
    ),
    (
        980003,
        908001,
        'EXPENSE',
        'MEDICINE',
        250000,
        'Piglet medicine and vaccination stock',
        'HMB-MED-001',
        'COMPLETED',
        NOW()
    ),
    -- =========================================================
    -- BAGO FARM EXPENSE
    -- =========================================================
    (
        980004,
        908002,
        'EXPENSE',
        'FEED',
        2016000,
        'Bago piglet feed purchase',
        'BGO-FEED-001',
        'COMPLETED',
        NOW()
    ),
    (
        980005,
        908002,
        'EXPENSE',
        'FEED',
        3475000,
        'Bago finisher feed purchase',
        'BGO-FEED-002',
        'COMPLETED',
        NOW()
    ),
    (
        980006,
        908002,
        'EXPENSE',
        'MEDICINE',
        280000,
        'Bago vaccination and medicine stock',
        'BGO-MED-001',
        'COMPLETED',
        NOW()
    ),
    -- =========================================================
    -- PIG SALE INCOME
    -- =========================================================
    (
        980007,
        908001,
        'INCOME',
        'PIG_SALE',
        450000,
        'Hmawbi market pig sale',
        'HMB-PIG-ORDER-001',
        'COMPLETED',
        NOW()
    ),
    (
        980008,
        908001,
        'INCOME',
        'PIG_SALE',
        480000,
        'Hmawbi market pig sale',
        'HMB-PIG-ORDER-002',
        'PENDING',
        NOW()
    ),
    (
        980009,
        908002,
        'INCOME',
        'PIG_SALE',
        460000,
        'Bago market pig sale',
        'BGO-PIG-ORDER-001',
        'COMPLETED',
        NOW()
    ),
    (
        980010,
        908002,
        'INCOME',
        'PIG_SALE',
        490000,
        'Bago market pig sale',
        'BGO-PIG-ORDER-002',
        'PENDING',
        NOW()
    ),
    -- =========================================================
    -- SEMEN SERVICE INCOME
    -- =========================================================
    (
        980011,
        908001,
        'INCOME',
        'SEMEN_SALE',
        180000,
        'Hmawbi breeding boar semen service',
        'HMB-SEMEN-001',
        'COMPLETED',
        NOW()
    ),
    (
        980012,
        908002,
        'INCOME',
        'SEMEN_SALE',
        130000,
        'Bago breeding boar semen service',
        'BGO-SEMEN-001',
        'COMPLETED',
        NOW()
    ),
    -- =========================================================
    -- FARM OPERATING COST
    -- =========================================================
    (
        980013,
        908001,
        'EXPENSE',
        'LABOR',
        1200000,
        'Hmawbi staff monthly wage',
        'HMB-SALARY-001',
        'COMPLETED',
        NOW()
    ),
    (
        980014,
        908002,
        'EXPENSE',
        'LABOR',
        1200000,
        'Bago staff monthly wage',
        'BGO-SALARY-001',
        'COMPLETED',
        NOW()
    ),
    -- =========================================================
    -- MAINTENANCE
    -- =========================================================
    (
        980015,
        908001,
        'EXPENSE',
        'MAINTENANCE',
        350000,
        'Breeding house maintenance',
        'HMB-MAINT-001',
        'COMPLETED',
        NOW()
    ),
    (
        980016,
        908002,
        'EXPENSE',
        'MAINTENANCE',
        400000,
        'Grower house maintenance',
        'BGO-MAINT-001',
        'COMPLETED',
        NOW()
    );
-- =========================================================
-- PIG ORDERS
-- CUSTOMER PURCHASE DEMO
-- =========================================================
INSERT INTO pig_orders (
        id,
        customer_id,
        status,
        total_amount,
        qr_code_path,
        order_reference,
        paid_at,
        created_at
    )
VALUES (
        990001,
        912001,
        'PENDING',
        450000,
        NULL,
        'HMB-PIG-ORDER-001',
        NULL,
        NOW()
    ),
    (
        990002,
        912001,
        'PENDING',
        480000,
        NULL,
        'HMB-PIG-ORDER-002',
        NULL,
        NOW()
    ),
    (
        990003,
        912001,
        'PAID',
        460000,
        NULL,
        'BGO-PIG-ORDER-001',
        NOW(),
        DATE_SUB(NOW(), INTERVAL 1 DAY)
    ),
    (
        990004,
        912001,
        'PENDING',
        490000,
        NULL,
        'BGO-PIG-ORDER-002',
        NULL,
        NOW()
    );
-- =========================================================
-- PIG ORDER ITEMS
-- =========================================================
INSERT INTO pig_order_items (id, order_id, pig_id, unit_price)
VALUES (991001, 990001, 950037, 450000),
    (991002, 990002, 950038, 480000),
    (991003, 990003, 950077, 460000),
    (991004, 990004, 950078, 490000);
-- =========================================================
-- UPDATE SOLD DEMO ORDER
-- =========================================================
UPDATE pigs
SET status = 'SOLD',
    listed_for_sale = FALSE,
    sold_date = CURDATE()
WHERE id = 950077;
-- =========================================================
-- KEEP PENDING MARKET PIGS AVAILABLE
-- =========================================================
UPDATE pigs
SET status = 'FOR_SALE',
    listed_for_sale = TRUE,
    listed_for_sale_date = CURDATE()
WHERE id IN (950037, 950038, 950078);
-- =========================================================
-- CHECK ORDER RESULT
-- =========================================================
SELECT po.id AS order_id,
    po.order_reference,
    po.status,
    po.total_amount,
    poi.pig_id,
    p.code,
    p.status AS pig_status
FROM pig_orders po
    JOIN pig_order_items poi ON po.id = poi.order_id
    JOIN pigs p ON poi.pig_id = p.id
ORDER BY po.id;
-- =========================================================
-- SEMEN ORDERS
-- CUSTOMER SEMEN SERVICE DEMO
-- =========================================================
INSERT INTO semen_orders (
        id,
        customer_id,
        boar_id,
        quantity,
        price_per_unit,
        total_amount,
        status,
        qr_code_path,
        order_reference,
        paid_at,
        created_at
    )
VALUES (
        992001,
        912001,
        950009,
        3,
        60000,
        180000,
        'PENDING',
        NULL,
        'HMB-SEMEN-ORDER-001',
        NULL,
        NOW()
    ),
    (
        992002,
        912001,
        950010,
        2,
        65000,
        130000,
        'PAID',
        NULL,
        'HMB-SEMEN-ORDER-002',
        NOW(),
        DATE_SUB(NOW(), INTERVAL 1 DAY)
    ),
    (
        992003,
        912001,
        950049,
        4,
        60000,
        240000,
        'PENDING',
        NULL,
        'BGO-SEMEN-ORDER-001',
        NULL,
        NOW()
    ),
    (
        992004,
        912001,
        950050,
        2,
        70000,
        140000,
        'PAID',
        NULL,
        'BGO-SEMEN-ORDER-002',
        NOW(),
        DATE_SUB(NOW(), INTERVAL 2 DAY)
    );
-- =========================================================
-- ADD SEMEN REVENUE TO FINANCE
-- =========================================================
INSERT INTO finance_transactions (
        id,
        farm_id,
        type,
        category,
        amount,
        description,
        reference_id,
        status,
        created_at
    )
VALUES (
        980017,
        908001,
        'INCOME',
        'SEMEN_SALE',
        180000,
        'Hmawbi boar semen service order',
        'HMB-SEMEN-ORDER-001',
        'PENDING',
        NOW()
    ),
    (
        980018,
        908001,
        'INCOME',
        'SEMEN_SALE',
        130000,
        'Hmawbi boar semen service completed',
        'HMB-SEMEN-ORDER-002',
        'COMPLETED',
        NOW()
    ),
    (
        980019,
        908002,
        'INCOME',
        'SEMEN_SALE',
        240000,
        'Bago boar semen service order',
        'BGO-SEMEN-ORDER-001',
        'PENDING',
        NOW()
    ),
    (
        980020,
        908002,
        'INCOME',
        'SEMEN_SALE',
        140000,
        'Bago boar semen service completed',
        'BGO-SEMEN-ORDER-002',
        'COMPLETED',
        NOW()
    );
-- =========================================================
-- CHECK SEMEN ORDER RESULT
-- =========================================================
SELECT so.id,
    so.order_reference,
    so.quantity,
    so.price_per_unit,
    so.total_amount,
    so.status,
    p.code AS boar_code
FROM semen_orders so
    JOIN pigs p ON so.boar_id = p.id
ORDER BY so.id;
-- =========================================================
-- READY FOR SELL APPROVAL DEMO
-- =========================================================
-- =========================================================
-- PENDING SALE APPROVAL PIGS
-- =========================================================
UPDATE pigs
SET status = 'PENDING_SALE_APPROVAL',
    listed_for_sale = FALSE,
    sale_price = 420000,
    notes = '
Ready for Sell Request:
Weight: 108 kg
Physical Condition: GOOD
Supervisor Notes: Waiting approval.
'
WHERE id = 950039;
UPDATE pigs
SET status = 'PENDING_SALE_APPROVAL',
    listed_for_sale = FALSE,
    sale_price = 430000,
    notes = '
Ready for Sell Request:
Weight: 112 kg
Physical Condition: GOOD
Supervisor Notes: Waiting approval.
'
WHERE id = 950040;
UPDATE pigs
SET status = 'PENDING_SALE_APPROVAL',
    listed_for_sale = FALSE,
    sale_price = 430000,
    notes = '
Ready for Sell Request:
Weight: 108 kg
Physical Condition: GOOD
Supervisor Notes: Waiting approval.
'
WHERE id = 950079;
UPDATE pigs
SET status = 'PENDING_SALE_APPROVAL',
    listed_for_sale = FALSE,
    sale_price = 440000,
    notes = '
Ready for Sell Request:
Weight: 112 kg
Physical Condition: GOOD
Supervisor Notes: Waiting approval.
'
WHERE id = 950080;
-- =========================================================
-- APPROVED SALE PIG
-- Supervisor Approved
-- =========================================================
UPDATE pigs
SET status = 'FOR_SALE',
    listed_for_sale = TRUE,
    listed_for_sale_date = CURDATE(),
    sale_price = 420000,
    notes = '
Ready for Sell Request:
Approved by Supervisor.
Available in Marketplace.
'
WHERE id = 950039;
-- =========================================================
-- REJECTED SALE DEMO
-- =========================================================
UPDATE pigs
SET status = 'FINISHER',
    listed_for_sale = FALSE,
    sale_price = NULL,
    notes = '
Ready for Sell Request:
Rejected.
Reason:
Weight condition not suitable.
'
WHERE id = 950080;
-- =========================================================
-- CHECK READY FOR SELL RESULT
-- =========================================================
SELECT id,
    code,
    status,
    listed_for_sale,
    sale_price,
    current_weight,
    notes
FROM pigs
WHERE status IN ('PENDING_SALE_APPROVAL', 'FOR_SALE')
ORDER BY id;
-- =========================================================
-- INVENTORY ALERT DEMO DATA
-- =========================================================
-- =========================================================
-- NORMAL STOCK
-- =========================================================
INSERT INTO inventory (
        id,
        farm_id,
        feed_type,
        quantity_kg,
        alert_threshold_kg,
        alert_triggered,
        updated_at
    )
VALUES (
        930001,
        908001,
        'GESTATION_FEED',
        2500.0,
        500.0,
        0,
        NOW()
    ),
    (
        930002,
        908001,
        'GROWER_FEED',
        3000.0,
        600.0,
        0,
        NOW()
    ),
    (
        930003,
        908002,
        'FINISHER_FEED',
        3500.0,
        700.0,
        0,
        NOW()
    ),
    (
        930004,
        908002,
        'PIGLET_FEED',
        1500.0,
        300.0,
        0,
        NOW()
    );
-- =========================================================
-- LOW STOCK ALERT
-- =========================================================
INSERT INTO inventory (
        id,
        farm_id,
        feed_type,
        quantity_kg,
        alert_threshold_kg,
        alert_triggered,
        updated_at
    )
VALUES (
        930005,
        908001,
        'IRON_SUPPLEMENT',
        3.0,
        10.0,
        1,
        NOW()
    ),
    (
        930006,
        908001,
        'PRRS_VACCINE',
        5.0,
        20.0,
        1,
        NOW()
    ),
    (
        930007,
        908002,
        'MYCOPLASMA_VACCINE',
        4.0,
        15.0,
        1,
        NOW()
    ),
    (
        930008,
        908002,
        'FINISHER_FEED',
        100.0,
        500.0,
        1,
        NOW()
    );
-- =========================================================
-- STOCK UPDATE DEMO
-- =========================================================
UPDATE inventory
SET quantity_kg = 2800,
    alert_triggered = 0,
    updated_at = NOW()
WHERE id = 930002;
UPDATE inventory
SET quantity_kg = 2,
    alert_triggered = 1,
    updated_at = NOW()
WHERE id = 930005;
-- =========================================================
-- CHECK INVENTORY ALERT
-- =========================================================
SELECT id,
    farm_id,
    feed_type,
    quantity_kg,
    alert_threshold_kg,
    alert_triggered
FROM inventory
ORDER BY alert_triggered DESC,
    id;
-- =========================================================
-- REPORT / ANALYTICS CHECK DATA
-- PROJECT SHOW DEMO
-- =========================================================
-- =========================================================
-- PIG STATUS SUMMARY CHECK
-- =========================================================
SELECT f.name AS farm_name,
    b.name AS building_name,
    p.status,
    COUNT(p.id) AS total_pigs
FROM pigs p
    JOIN buildings b ON p.building_id = b.id
    JOIN farms f ON b.farm_id = f.id
GROUP BY f.name,
    b.name,
    p.status
ORDER BY f.name,
    b.name,
    p.status;
-- =========================================================
-- FARM TOTAL PIG SUMMARY
-- =========================================================
SELECT f.id,
    f.name,
    COUNT(p.id) AS total_pigs
FROM farms f
    LEFT JOIN buildings b ON f.id = b.farm_id
    LEFT JOIN pigs p ON b.id = p.building_id
GROUP BY f.id,
    f.name;
-- =========================================================
-- BREEDING SUMMARY
-- =========================================================
SELECT b.name AS building,
    COUNT(p.id) AS breeding_sows
FROM buildings b
    JOIN pigs p ON b.id = p.building_id
WHERE p.status = 'BREEDING_SOW'
GROUP BY b.name;
-- =========================================================
-- SALE READY SUMMARY
-- =========================================================
SELECT code,
    status,
    sale_price,
    current_weight
FROM pigs
WHERE status IN (
        'FOR_SALE',
        'PENDING_SALE_APPROVAL',
        'SOLD'
    )
ORDER BY status,
    id;
-- =========================================================
-- ATTENDANCE SUMMARY
-- =========================================================
SELECT u.name,
    COUNT(a.id) AS attendance_days,
    SUM(
        CASE
            WHEN a.attended = 1 THEN 1
            ELSE 0
        END
    ) AS present_days
FROM users u
    LEFT JOIN attendance a ON u.id = a.user_id
WHERE u.role = 'STAFF'
GROUP BY u.id,
    u.name;
-- =========================================================
-- TASK STATUS SUMMARY
-- =========================================================
SELECT u.name,
    dt.status,
    COUNT(dt.id) AS task_count
FROM daily_tasks dt
    JOIN users u ON dt.assigned_staff_id = u.id
GROUP BY u.name,
    dt.status
ORDER BY u.name;
-- =========================================================
-- FINANCE SUMMARY
-- =========================================================
SELECT f.name,
    ft.type,
    SUM(ft.amount) AS total_amount
FROM finance_transactions ft
    JOIN farms f ON ft.farm_id = f.id
GROUP BY f.name,
    ft.type;
-- =========================================================
-- MONTHLY REVENUE CHECK
-- =========================================================
SELECT DATE_FORMAT(created_at, '%Y-%m') AS month,
    SUM(amount) AS revenue
FROM finance_transactions
WHERE type = 'INCOME'
GROUP BY DATE_FORMAT(created_at, '%Y-%m')
ORDER BY month;
-- =========================================================
-- MONTHLY EXPENSE CHECK
-- =========================================================
SELECT DATE_FORMAT(created_at, '%Y-%m') AS month,
    SUM(amount) AS expense
FROM finance_transactions
WHERE type = 'EXPENSE'
GROUP BY DATE_FORMAT(created_at, '%Y-%m')
ORDER BY month;
-- =========================================================
-- FINAL PROJECT SHOW DATABASE CHECK
-- =========================================================
SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;
-- =========================================================
-- FARM CHECK
-- =========================================================
SELECT id,
    name,
    code,
    location
FROM farms
ORDER BY id;
-- =========================================================
-- BUILDING CHECK
-- =========================================================
SELECT b.id,
    b.name,
    f.name AS farm_name
FROM buildings b
    JOIN farms f ON b.farm_id = f.id
ORDER BY b.id;
-- =========================================================
-- PIG TOTAL CHECK
-- =========================================================
SELECT COUNT(*) AS total_pigs
FROM pigs;
-- Expected:
-- 80 original demo pigs
-- + newborn piglets from birth records
-- =========================================================
-- PIG COUNT BY BUILDING
-- =========================================================
SELECT building_id,
    COUNT(id) AS pig_count
FROM pigs
GROUP BY building_id
ORDER BY building_id;
-- Expected:
-- 909001 = 20+
-- 909002 = 20
-- 909003 = 20+
-- 909004 = 20
-- =========================================================
-- PIG STATUS CHECK
-- =========================================================
SELECT status,
    COUNT(*) AS total
FROM pigs
GROUP BY status
ORDER BY status;
-- =========================================================
-- BREEDING CHECK
-- =========================================================
SELECT code,
    status,
    building_id
FROM pigs
WHERE status IN ('BREEDING_SOW', 'BREEDING_BOAR')
ORDER BY building_id;
-- =========================================================
-- BIRTH RECORD CHECK
-- =========================================================
SELECT br.id,
    p.code AS mother,
    br.birth_date,
    br.alive_piglets,
    br.dead_piglets
FROM birth_records br
    JOIN pigs p ON br.mother_id = p.id;
-- =========================================================
-- SALE READY CHECK
-- =========================================================
SELECT id,
    code,
    status,
    sale_price,
    current_weight
FROM pigs
WHERE status IN (
        'FOR_SALE',
        'PENDING_SALE_APPROVAL',
        'SOLD'
    )
ORDER BY id;
-- =========================================================
-- CUSTOMER ORDER CHECK
-- =========================================================
SELECT po.id,
    po.order_reference,
    po.status,
    poi.pig_id,
    p.code
FROM pig_orders po
    JOIN pig_order_items poi ON po.id = poi.order_id
    JOIN pigs p ON poi.pig_id = p.id;
-- =========================================================
-- SEMEN ORDER CHECK
-- =========================================================
SELECT so.id,
    so.order_reference,
    p.code AS boar,
    so.quantity,
    so.total_amount,
    so.status
FROM semen_orders so
    JOIN pigs p ON so.boar_id = p.id;
-- =========================================================
-- STAFF TASK CHECK
-- =========================================================
SELECT u.name,
    dt.task_name,
    dt.status
FROM daily_tasks dt
    JOIN users u ON dt.assigned_staff_id = u.id
ORDER BY dt.id;
-- =========================================================
-- INVENTORY ALERT CHECK
-- =========================================================
SELECT feed_type,
    quantity_kg,
    alert_threshold_kg,
    alert_triggered
FROM inventory
ORDER BY alert_triggered DESC;
-- =========================================================
-- FINANCE CHECK
-- =========================================================
SELECT type,
    SUM(amount) AS total
FROM finance_transactions
GROUP BY type;
-- =========================================================
-- DEMO LOGIN ACCOUNT LIST
-- =========================================================
SELECT name,
    email,
    role
FROM users
ORDER BY role;
-- CUSTOMER LOGIN
SELECT name,
    email
FROM customer_accounts;