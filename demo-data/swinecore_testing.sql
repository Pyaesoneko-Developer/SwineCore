USE swinecore_testing;

SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================================
-- READY FOR SELL DATABASE FIX - OLD MYSQL COMPATIBLE
-- =========================================================

ALTER TABLE pigs
MODIFY COLUMN status ENUM(
    'PIGLET',
    'GROWER',
    'FINISHER',
    'BREEDING_SOW',
    'BREEDING_BOAR',
    'PENDING_SALE_APPROVAL',
    'FOR_SALE',
    'SOLD'
) NOT NULL;

SET @db_name = DATABASE();

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE pigs ADD COLUMN sale_price DOUBLE NULL',
        'SELECT "sale_price already exists"'
    )
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @db_name
      AND TABLE_NAME = 'pigs'
      AND COLUMN_NAME = 'sale_price'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE pigs ADD COLUMN listed_for_sale BOOLEAN NOT NULL DEFAULT FALSE',
        'SELECT "listed_for_sale already exists"'
    )
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @db_name
      AND TABLE_NAME = 'pigs'
      AND COLUMN_NAME = 'listed_for_sale'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE pigs ADD COLUMN listed_for_sale_date DATE NULL',
        'SELECT "listed_for_sale_date already exists"'
    )
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @db_name
      AND TABLE_NAME = 'pigs'
      AND COLUMN_NAME = 'listed_for_sale_date'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE pigs ADD COLUMN sold_date DATE NULL',
        'SELECT "sold_date already exists"'
    )
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @db_name
      AND TABLE_NAME = 'pigs'
      AND COLUMN_NAME = 'sold_date'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE pigs ADD COLUMN current_weight DOUBLE NULL',
        'SELECT "current_weight already exists"'
    )
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @db_name
      AND TABLE_NAME = 'pigs'
      AND COLUMN_NAME = 'current_weight'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE pigs ADD COLUMN photo_path VARCHAR(255) NULL',
        'SELECT "photo_path already exists"'
    )
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @db_name
      AND TABLE_NAME = 'pigs'
      AND COLUMN_NAME = 'photo_path'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE pigs ADD COLUMN notes TEXT NULL',
        'SELECT "notes already exists"'
    )
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @db_name
      AND TABLE_NAME = 'pigs'
      AND COLUMN_NAME = 'notes'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE pigs
SET listed_for_sale = FALSE,
    listed_for_sale_date = NULL
WHERE status = 'PENDING_SALE_APPROVAL';

UPDATE pigs
SET listed_for_sale = TRUE
WHERE status = 'FOR_SALE';

UPDATE pigs
SET listed_for_sale = FALSE,
    listed_for_sale_date = NULL
WHERE status = 'SOLD';

-- =========================================================
-- CLEAN OLD DEMO DATA
-- =========================================================

DELETE FROM pig_order_items WHERE id >= 991001 AND id <= 991999;
DELETE FROM pig_orders WHERE id >= 990001 AND id <= 990999;
DELETE FROM semen_orders WHERE id >= 992001 AND id <= 992999;

DELETE FROM finance_transactions WHERE id >= 980001 AND id <= 980999;
DELETE FROM feed_shipments WHERE id >= 940001 AND id <= 940999;
DELETE FROM inventory WHERE id >= 930001 AND id <= 930999;

DELETE FROM daily_tasks WHERE id >= 970001 AND id <= 970999;
DELETE FROM daily_tasks WHERE assigned_staff_id >= 910001 AND assigned_staff_id <= 910999;
DELETE FROM daily_tasks WHERE reviewed_by >= 910001 AND reviewed_by <= 910999;

DELETE FROM attendance WHERE id >= 960001 AND id <= 960999;
DELETE FROM attendance WHERE user_id >= 910001 AND user_id <= 910999;

UPDATE pigs SET birth_record_id = NULL WHERE id >= 950001 AND id <= 950999;
DELETE FROM birth_records WHERE id >= 955001 AND id <= 955999;
DELETE FROM birth_records WHERE recorded_by >= 910001 AND recorded_by <= 910999;
DELETE FROM pigs WHERE id >= 950001 AND id <= 950999;

DELETE FROM rule_schedules WHERE id >= 920001 AND id <= 920999;
DELETE FROM rule_schedules WHERE created_by >= 910001 AND created_by <= 910999;

DELETE FROM staff_shifts WHERE id >= 915001 AND id <= 915999;
DELETE FROM staff_shifts WHERE user_id >= 910001 AND user_id <= 910999;

DELETE FROM shifts WHERE id >= 914001 AND id <= 914999;
DELETE FROM rooms WHERE id >= 913001 AND id <= 913999;

DELETE FROM customer_accounts WHERE id >= 912001 AND id <= 912999;
DELETE FROM customer_accounts WHERE email = 'buyer.yangon@swinecore.mm';

DELETE FROM users WHERE id >= 910001 AND id <= 910999;
DELETE FROM users WHERE email = 'admin@swinecore.mm';
DELETE FROM users WHERE email = 'hr@swinecore.mm';
DELETE FROM users WHERE email = 'manager.hmawbi@swinecore.mm';
DELETE FROM users WHERE email = 'supervisor.hmawbi@swinecore.mm';
DELETE FROM users WHERE email = 'staff.hmawbi.breeding@swinecore.mm';
DELETE FROM users WHERE email = 'staff.hmawbi.grower@swinecore.mm';
DELETE FROM users WHERE email = 'manager.bago@swinecore.mm';
DELETE FROM users WHERE email = 'supervisor.bago@swinecore.mm';
DELETE FROM users WHERE email = 'staff.bago.breeding@swinecore.mm';
DELETE FROM users WHERE email = 'staff.bago.grower@swinecore.mm';

DELETE FROM buildings WHERE id >= 909001 AND id <= 909999;
DELETE FROM farms WHERE id >= 908001 AND id <= 908999;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================================================
-- GENETICS
-- =========================================================

INSERT IGNORE INTO genetics (id, name, code, description, active)
VALUES
(901001, 'Yorkshire', 'Y', 'Large White maternal line', 1),
(901002, 'Landrace', 'L', 'High litter size maternal line', 1),
(901003, 'Duroc', 'D', 'Fast growth terminal sire line', 1),
(901004, 'Yorkshire x Landrace', 'YL', 'F1 sow line for commercial piglet production', 1),
(901005, 'Duroc x Landrace', 'DL', 'Fast-growing commercial cross', 1);

SET @GEN_Y  = (SELECT id FROM genetics WHERE code = 'Y' LIMIT 1);
SET @GEN_L  = (SELECT id FROM genetics WHERE code = 'L' LIMIT 1);
SET @GEN_D  = (SELECT id FROM genetics WHERE code = 'D' LIMIT 1);
SET @GEN_YL = (SELECT id FROM genetics WHERE code = 'YL' LIMIT 1);
SET @GEN_DL = (SELECT id FROM genetics WHERE code = 'DL' LIMIT 1);

-- =========================================================
-- FARMS
-- =========================================================

INSERT INTO farms
(id, name, code, location, description, image_path, latitude, longitude, created_at)
VALUES
(908001, 'Hmawbi Integrated Swine Farm', 'HMB1', 'Hmawbi Township, Yangon Region',
 'Integrated breeding and grower farm supplying piglets, market pigs, and semen service for Yangon region.',
 '/images/farms/hmawbi-swine-farm.jpg', 17.1074, 96.0451, NOW()),

(908002, 'Bago Golden Valley Swine Farm', 'BGO2', 'Bago Township, Bago Region',
 'Commercial grower and breeding farm serving Bago, Waw, Thanatpin, and nearby township markets.',
 '/images/farms/bago-swine-farm.jpg', 17.3352, 96.4810, NOW());

-- =========================================================
-- BUILDINGS
-- =========================================================

INSERT INTO buildings
(id, name, code, description, farm_id, birth_form_rotation_index, created_at)
VALUES
(909001, 'Hmawbi Breeding House A', 'HB1', 'Sows, boars, mating pens and farrowing crates', 908001, 0, NOW()),
(909002, 'Hmawbi Grower House B', 'HG2', 'Grower and finisher pigs for commercial sale', 908001, 0, NOW()),
(909003, 'Bago Breeding House A', 'BB1', 'Breeding sows, boars and newborn piglets', 908002, 0, NOW()),
(909004, 'Bago Grower House B', 'BG2', 'Grower pigs and market pigs', 908002, 0, NOW());

-- =========================================================
-- ROOMS
-- =========================================================

INSERT INTO rooms
(id, name, code, description, building_id, active, capacity, created_at)
VALUES
(913001, 'Hmawbi Sow Pen 1', 'HR1', 'Gestating and lactating sows', 909001, 1, 40, NOW()),
(913002, 'Hmawbi Piglet Pen 1', 'HR2', 'Newborn and weaned piglets', 909001, 1, 120, NOW()),
(913003, 'Hmawbi Grower Pen 1', 'HR3', 'Grower pigs from 60 to 120 days', 909002, 1, 160, NOW()),
(913004, 'Bago Sow Pen 1', 'BR1', 'Breeding sows and boars', 909003, 1, 45, NOW()),
(913005, 'Bago Piglet Pen 1', 'BR2', 'Piglets and nursery pigs', 909003, 1, 130, NOW()),
(913006, 'Bago Grower Pen 1', 'BR3', 'Grower and finisher pigs', 909004, 1, 170, NOW());

-- =========================================================
-- USERS
-- password all = 123123
-- =========================================================

INSERT INTO users
(id, name, email, password, role, farm_id, building_id, start_date, phone, profile_image_path, enabled, must_change_password, created_at, updated_at)
VALUES
(910001, 'System Admin', 'admin@swinecore.mm',
 '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
 'ADMIN', NULL, NULL, CURDATE(), '09400100001', '/images/logo.png', 1, 0, NOW(), NOW()),

(910002, 'HR Officer', 'hr@swinecore.mm',
 '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
 'HR', NULL, NULL, CURDATE(), '09400100002', '/images/logo.png', 1, 0, NOW(), NOW()),

(910003, 'U Aung Kyaw', 'manager.hmawbi@swinecore.mm',
 '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
 'MANAGER', 908001, NULL, CURDATE(), '09420110003', '/images/logo.png', 1, 0, NOW(), NOW()),

(910004, 'Daw May Thu', 'supervisor.hmawbi@swinecore.mm',
 '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
 'SUPERVISOR', 908001, 909001, CURDATE(), '09420110004', '/images/logo.png', 1, 0, NOW(), NOW()),

(910005, 'Ko Min Naing', 'staff.hmawbi.breeding@swinecore.mm',
 '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
 'STAFF', 908001, 909001, CURDATE(), '09420110005', '/images/logo.png', 1, 0, NOW(), NOW()),

(910006, 'Ko Zaw Lin', 'staff.hmawbi.grower@swinecore.mm',
 '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
 'STAFF', 908001, 909002, CURDATE(), '09420110006', '/images/logo.png', 1, 0, NOW(), NOW()),

(910007, 'U Thet Paing', 'manager.bago@swinecore.mm',
 '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
 'MANAGER', 908002, NULL, CURDATE(), '09430220007', '/images/logo.png', 1, 0, NOW(), NOW()),

(910008, 'Daw Ei Mon', 'supervisor.bago@swinecore.mm',
 '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
 'SUPERVISOR', 908002, 909003, CURDATE(), '09430220008', '/images/logo.png', 1, 0, NOW(), NOW()),

(910009, 'Ko Hein Htet', 'staff.bago.breeding@swinecore.mm',
 '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
 'STAFF', 908002, 909003, CURDATE(), '09430220009', '/images/logo.png', 1, 0, NOW(), NOW()),

(910010, 'Ko Nyi Nyi', 'staff.bago.grower@swinecore.mm',
 '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
 'STAFF', 908002, 909004, CURDATE(), '09430220010', '/images/logo.png', 1, 0, NOW(), NOW());

-- =========================================================
-- CUSTOMER ACCOUNT
-- password = 123123
-- =========================================================

INSERT INTO customer_accounts
(id, name, email, password, phone, address, profile_image_path, enabled, created_at)
VALUES
(912001, 'Yangon Meat Shop Buyer', 'buyer.yangon@swinecore.mm',
 '$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',
 '09455300001', 'Bayintnaung Market, Mayangone Township, Yangon', '/images/logo.png', 1, NOW());

-- =========================================================
-- SHIFTS + STAFF SHIFTS
-- =========================================================

INSERT INTO shifts
(id, name, farm_id, standard_start, standard_end, grace_period_minutes, minimum_required_hours, active)
VALUES
(914001, 'Hmawbi Day Shift', 908001, '08:00:00', '17:00:00', 15, 8.0, 1),
(914002, 'Bago Day Shift', 908002, '08:00:00', '17:00:00', 15, 8.0, 1);

INSERT INTO staff_shifts
(id, user_id, shift_id, effective_date, end_date, status)
VALUES
(915001, 910005, 914001, CURDATE(), NULL, 'ACTIVE'),
(915002, 910006, 914001, CURDATE(), NULL, 'ACTIVE'),
(915003, 910009, 914002, CURDATE(), NULL, 'ACTIVE'),
(915004, 910010, 914002, CURDATE(), NULL, 'ACTIVE');

-- =========================================================
-- ATTENDANCE
-- =========================================================

INSERT INTO attendance
(id, user_id, work_date, clock_in_time, clock_out_time, attended, clocked_out, status, early_departure_reason, wage_deduction_amount, created_at)
VALUES
(960001, 910005, CURDATE(), CONCAT(CURDATE(), ' 08:00:00'), NULL, 1, 0, 'NORMAL', NULL, NULL, NOW()),
(960002, 910006, CURDATE(), CONCAT(CURDATE(), ' 08:00:00'), NULL, 1, 0, 'NORMAL', NULL, NULL, NOW()),
(960003, 910009, CURDATE(), CONCAT(CURDATE(), ' 08:00:00'), NULL, 1, 0, 'NORMAL', NULL, NULL, NOW()),
(960004, 910010, CURDATE(), CONCAT(CURDATE(), ' 08:00:00'), NULL, 1, 0, 'NORMAL', NULL, NULL, NOW());

-- =========================================================
-- RULE SCHEDULES
-- =========================================================

INSERT INTO rule_schedules
(id, name, rule_type, day_from_birth, day_range_start, day_range_end, feed_amount_kg, feed_type, medication, dosage, administration_route, fixed_target_day, breeding_month, applies_to, description, active, farm_id, created_by, created_at)
VALUES
(920001, 'Hmawbi Day 1 Iron Supplement', 'MEDICATION', 1, NULL, NULL, NULL, NULL, 'Iron Supplement', '2 ml', 'ORAL', 1, NULL, 'PIGLET',
 'Newborn piglet iron support on day 1.', 1, 908001, 910003, NOW()),

(920002, 'Hmawbi Day 30 PRRS Vaccination', 'VACCINATION', 30, NULL, NULL, NULL, NULL, 'PRRS Vaccine', '2 ml', 'INTRAMUSCULAR', 30, NULL, 'PIGLET',
 'Vaccination for piglets at 30 days old.', 1, 908001, 910003, NOW()),

(920003, 'Hmawbi Grower Feed Program', 'FEEDING', NULL, 60, 120, 2.5, 'GROWER_FEED', NULL, NULL, NULL, NULL, NULL, 'GROWER',
 'Grower daily feed from day 60 to day 120.', 1, 908001, 910003, NOW()),

(920004, 'Bago Day 1 Iron Supplement', 'MEDICATION', 1, NULL, NULL, NULL, NULL, 'Iron Supplement', '2 ml', 'ORAL', 1, NULL, 'PIGLET',
 'Newborn piglet iron support on day 1.', 1, 908002, 910007, NOW()),

(920005, 'Bago Day 28 Mycoplasma Vaccination', 'VACCINATION', 28, NULL, NULL, NULL, NULL, 'Mycoplasma Vaccine', '2 ml', 'INTRAMUSCULAR', 28, NULL, 'PIGLET',
 'Vaccination for piglets at 28 days old.', 1, 908002, 910007, NOW()),

(920006, 'Bago Finisher Feed Program', 'FEEDING', NULL, 121, 180, 3.2, 'FINISHER_FEED', NULL, NULL, NULL, NULL, NULL, 'FINISHER',
 'Finisher daily feed from day 121 to day 180.', 1, 908002, 910007, NOW());

-- =========================================================
-- INVENTORY
-- =========================================================

INSERT INTO inventory
(id, farm_id, feed_type, quantity_kg, alert_threshold_kg, alert_triggered, updated_at)
VALUES
(930001, 908001, 'Iron Supplement', 60.0, 5.0, 0, NOW()),
(930002, 908001, 'PRRS Vaccine', 120.0, 10.0, 0, NOW()),
(930003, 908001, 'GROWER_FEED', 3000.0, 500.0, 0, NOW()),
(930004, 908002, 'Iron Supplement', 55.0, 5.0, 0, NOW()),
(930005, 908002, 'Mycoplasma Vaccine', 100.0, 10.0, 0, NOW()),
(930006, 908002, 'FINISHER_FEED', 3500.0, 600.0, 0, NOW());

-- =========================================================
-- PIGS
-- =========================================================

INSERT INTO pigs
(id, code, gender, status, building_id, genetics_id, mother_id, birth_record_id, birth_date, recorded_date, sold_date, listed_for_sale_date, current_weight, photo_path, notes, listed_for_sale, sale_price, created_at)
VALUES
(950001, 'HMB-SOW-001', 'FEMALE', 'BREEDING_SOW', 909001, @GEN_YL, NULL, NULL,
 DATE_SUB(CURDATE(), INTERVAL 760 DAY), CURDATE(), NULL, NULL, 188.0, '/images/pigs/sow-hmawbi.jpg',
 'Breeding Record: served by HMB-BOAR-001. Healthy sow for demo birth record.', 0, NULL, NOW()),

(950002, 'HMB-BOAR-001', 'MALE', 'BREEDING_BOAR', 909001, @GEN_D, NULL, NULL,
 DATE_SUB(CURDATE(), INTERVAL 690 DAY), CURDATE(), NULL, NULL, 238.0, '/images/pigs/boar-hmawbi.jpg',
 'Active breeding boar and semen demo boar.', 0, NULL, NOW()),

(950003, 'HMB-PIGLET-D29', 'MALE', 'PIGLET', 909001, @GEN_Y, 950001, NULL,
 DATE_SUB(CURDATE(), INTERVAL 29 DAY), CURDATE(), NULL, NULL, 8.5, '/images/pigs/piglet-hmawbi.jpg',
 'Tomorrow this pig becomes 30 days old for PRRS preview.', 0, NULL, NOW()),

(950004, 'HMB-GROWER-D75', 'FEMALE', 'GROWER', 909002, @GEN_DL, NULL, NULL,
 DATE_SUB(CURDATE(), INTERVAL 75 DAY), CURDATE(), NULL, NULL, 46.2, '/images/pigs/grower-hmawbi.jpg',
 'Grower feed task demo.', 0, NULL, NOW()),

(950005, 'HMB-MARKET-001', 'MALE', 'FOR_SALE', 909002, @GEN_D, NULL, NULL,
 DATE_SUB(CURDATE(), INTERVAL 165 DAY), CURDATE(), NULL, CURDATE(), 95.0, '/images/pigs/market-hmawbi.jpg',
 'Market-ready pig for customer marketplace demo.', 1, 430000.0, NOW()),

(950009, 'HMB-PENDING-SALE-001', 'MALE', 'PENDING_SALE_APPROVAL', 909001, @GEN_D, NULL, NULL,
 DATE_SUB(CURDATE(), INTERVAL 155 DAY), CURDATE(), NULL, NULL, 88.0, '/images/pigs/market-hmawbi.jpg',
 'Ready for Sell Request:
Weight: 88.0 kg
Physical Condition: GOOD
Supervisor Notes: Demo pending approval pig from supervisor.', 0, 410000.0, NOW()),

(950101, 'BGO-SOW-001', 'FEMALE', 'BREEDING_SOW', 909003, @GEN_YL, NULL, NULL,
 DATE_SUB(CURDATE(), INTERVAL 735 DAY), CURDATE(), NULL, NULL, 181.0, '/images/pigs/sow-bago.jpg',
 'Breeding Record: served by BGO-BOAR-001. Active sow for Bago farm demo.', 0, NULL, NOW()),

(950102, 'BGO-BOAR-001', 'MALE', 'BREEDING_BOAR', 909003, @GEN_D, NULL, NULL,
 DATE_SUB(CURDATE(), INTERVAL 700 DAY), CURDATE(), NULL, NULL, 242.0, '/images/pigs/boar-bago.jpg',
 'Bago breeding boar and semen marketplace demo.', 0, NULL, NOW()),

(950103, 'BGO-PIGLET-D27', 'FEMALE', 'PIGLET', 909003, @GEN_L, 950101, NULL,
 DATE_SUB(CURDATE(), INTERVAL 27 DAY), CURDATE(), NULL, NULL, 7.9, '/images/pigs/piglet-bago.jpg',
 'Tomorrow this pig becomes 28 days old for Mycoplasma preview.', 0, NULL, NOW()),

(950104, 'BGO-FINISHER-D140', 'MALE', 'FINISHER', 909004, @GEN_DL, NULL, NULL,
 DATE_SUB(CURDATE(), INTERVAL 140 DAY), CURDATE(), NULL, NULL, 82.5, '/images/pigs/finisher-bago.jpg',
 'Finisher feed task demo.', 0, NULL, NOW()),

(950105, 'BGO-MARKET-001', 'FEMALE', 'FOR_SALE', 909004, @GEN_DL, NULL, NULL,
 DATE_SUB(CURDATE(), INTERVAL 170 DAY), CURDATE(), NULL, CURDATE(), 98.0, '/images/pigs/market-bago.jpg',
 'Market-ready pig for Bago farm demo.', 1, 455000.0, NOW()),

(950110, 'BGO-PENDING-SALE-001', 'FEMALE', 'PENDING_SALE_APPROVAL', 909003, @GEN_DL, NULL, NULL,
 DATE_SUB(CURDATE(), INTERVAL 150 DAY), CURDATE(), NULL, NULL, 84.5, '/images/pigs/market-bago.jpg',
 'Ready for Sell Request:
Weight: 84.5 kg
Physical Condition: GOOD
Supervisor Notes: Demo pending approval pig from supervisor.', 0, 400000.0, NOW());

-- =========================================================
-- BIRTH RECORDS
-- =========================================================

INSERT INTO birth_records
(id, mother_id, building_id, recorded_by, birth_date, litter_size, alive_piglets, dead_piglets, confirmed_by_staff, confirmed_by_supervisor, created_at)
VALUES
(955001, 950001, 909001, 910005, CURDATE(), 4, 3, 1, 1, 0, NOW()),
(955002, 950101, 909003, 910009, CURDATE(), 5, 4, 1, 1, 0, NOW());

INSERT INTO pigs
(id, code, gender, status, building_id, genetics_id, mother_id, birth_record_id, birth_date, recorded_date, sold_date, listed_for_sale_date, current_weight, photo_path, notes, listed_for_sale, sale_price, created_at)
VALUES
(950006, 'HMB-BIRTH-001-A', 'FEMALE', 'PIGLET', 909001, @GEN_YL, 950001, 955001,
 CURDATE(), CURDATE(), NULL, NULL, 1.4, '/images/pigs/newborn-hmawbi.jpg',
 'Born from Hmawbi birth record. Demo newborn piglet.', 0, NULL, NOW()),

(950007, 'HMB-BIRTH-001-B', 'MALE', 'PIGLET', 909001, @GEN_YL, 950001, 955001,
 CURDATE(), CURDATE(), NULL, NULL, 1.3, '/images/pigs/newborn-hmawbi.jpg',
 'Born from Hmawbi birth record. Demo newborn piglet.', 0, NULL, NOW()),

(950008, 'HMB-BIRTH-001-C', 'FEMALE', 'PIGLET', 909001, @GEN_YL, 950001, 955001,
 CURDATE(), CURDATE(), NULL, NULL, 1.2, '/images/pigs/newborn-hmawbi.jpg',
 'Born from Hmawbi birth record. Demo newborn piglet.', 0, NULL, NOW()),

(950106, 'BGO-BIRTH-001-A', 'MALE', 'PIGLET', 909003, @GEN_YL, 950101, 955002,
 CURDATE(), CURDATE(), NULL, NULL, 1.5, '/images/pigs/newborn-bago.jpg',
 'Born from Bago birth record. Demo newborn piglet.', 0, NULL, NOW()),

(950107, 'BGO-BIRTH-001-B', 'FEMALE', 'PIGLET', 909003, @GEN_YL, 950101, 955002,
 CURDATE(), CURDATE(), NULL, NULL, 1.4, '/images/pigs/newborn-bago.jpg',
 'Born from Bago birth record. Demo newborn piglet.', 0, NULL, NOW()),

(950108, 'BGO-BIRTH-001-C', 'MALE', 'PIGLET', 909003, @GEN_YL, 950101, 955002,
 CURDATE(), CURDATE(), NULL, NULL, 1.3, '/images/pigs/newborn-bago.jpg',
 'Born from Bago birth record. Demo newborn piglet.', 0, NULL, NOW()),

(950109, 'BGO-BIRTH-001-D', 'FEMALE', 'PIGLET', 909003, @GEN_YL, 950101, 955002,
 CURDATE(), CURDATE(), NULL, NULL, 1.2, '/images/pigs/newborn-bago.jpg',
 'Born from Bago birth record. Demo newborn piglet.', 0, NULL, NOW());

-- =========================================================
-- DAILY TASKS
-- =========================================================

INSERT INTO daily_tasks
(id, building_id, assigned_staff_id, rule_schedule_id, pig_id, task_date, task_name, task_description, is_standard_task, status, staff_notes, supervisor_comments, submitted_at, reviewed_at, reviewed_by, created_at)
VALUES
(970001, 909001, 910005, NULL, NULL, CURDATE(),
 'Daily Care Routine',
 'Check sow pens, piglet pens, water lines, temperature, and general health.',
 1, 'PENDING', NULL, NULL, NULL, NULL, NULL, NOW()),

(970002, 909001, 910005, 920001, 950006, CURDATE(),
 'MEDICATION: Hmawbi Day 1 Iron Supplement',
 'Administer Iron Supplement 2 ml to HMB-BIRTH-001-A.',
 0, 'PENDING', NULL, NULL, NULL, NULL, NULL, NOW()),

(970021, 909001, 910005, 920001, 950007, CURDATE(),
 'MEDICATION: Hmawbi Day 1 Iron Supplement',
 'Submitted demo task for supervisor review.',
 0, 'APPROVED',
 'Iron supplement given. Piglet active and nursing well.',
 '-',
 DATE_SUB(NOW(), INTERVAL 1 HOUR),
 DATE_SUB(NOW(), INTERVAL 30 MINUTE),
 910004,
 NOW()),

(970011, 909001, 910005, 920002, 950003, DATE_ADD(CURDATE(), INTERVAL 1 DAY),
 'VACCINATION: Hmawbi Day 30 PRRS Vaccination',
 'Tomorrow demo: PRRS Vaccine 2 ml for HMB-PIGLET-D29.',
 0, 'PENDING', NULL, NULL, NULL, NULL, NULL, NOW()),

(970012, 909001, 910005, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 2 DAY),
 'Daily Care Routine',
 'Next day preview: check sow pens, piglet pens, water lines, temperature, and general health.',
 1, 'PENDING', NULL, NULL, NULL, NULL, NULL, NOW()),

(970003, 909002, 910006, 920003, 950004, CURDATE(),
 'FEEDING: Hmawbi Grower Feed Program',
 'Feed GROWER_FEED 2.5 kg for HMB-GROWER-D75.',
 0, 'PENDING', NULL, NULL, NULL, NULL, NULL, NOW()),

(970013, 909002, 910006, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 1 DAY),
 'Daily Care Routine',
 'Tomorrow preview: check grower house, water and feed condition.',
 1, 'PENDING', NULL, NULL, NULL, NULL, NULL, NOW()),

(970101, 909003, 910009, NULL, NULL, CURDATE(),
 'Daily Care Routine',
 'Check Bago breeding pens, newborn piglets, water and room hygiene.',
 1, 'PENDING', NULL, NULL, NULL, NULL, NULL, NOW()),

(970102, 909003, 910009, 920004, 950106, CURDATE(),
 'MEDICATION: Bago Day 1 Iron Supplement',
 'Administer Iron Supplement 2 ml to BGO-BIRTH-001-A.',
 0, 'PENDING', NULL, NULL, NULL, NULL, NULL, NOW()),

(970121, 909003, 910009, 920004, 950107, CURDATE(),
 'MEDICATION: Bago Day 1 Iron Supplement',
 'Submitted demo task for supervisor review.',
 0, 'APPROVED',
 'Iron supplement given. Piglet condition normal.',
 '-',
 DATE_SUB(NOW(), INTERVAL 2 HOUR),
 DATE_SUB(NOW(), INTERVAL 90 MINUTE),
 910008,
 NOW()),

(970111, 909003, 910009, 920005, 950103, DATE_ADD(CURDATE(), INTERVAL 1 DAY),
 'VACCINATION: Bago Day 28 Mycoplasma Vaccination',
 'Tomorrow demo: Mycoplasma Vaccine 2 ml for BGO-PIGLET-D27.',
 0, 'PENDING', NULL, NULL, NULL, NULL, NULL, NOW()),

(970112, 909003, 910009, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 2 DAY),
 'Daily Care Routine',
 'Next day preview: check Bago breeding pens, piglet health and hygiene.',
 1, 'PENDING', NULL, NULL, NULL, NULL, NULL, NOW()),

(970103, 909004, 910010, 920006, 950104, CURDATE(),
 'FEEDING: Bago Finisher Feed Program',
 'Feed FINISHER_FEED 3.2 kg for BGO-FINISHER-D140.',
 0, 'PENDING', NULL, NULL, NULL, NULL, NULL, NOW()),

(970113, 909004, 910010, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 1 DAY),
 'Daily Care Routine',
 'Tomorrow preview: check Bago grower house, feed, water and hygiene.',
 1, 'PENDING', NULL, NULL, NULL, NULL, NULL, NOW());

-- =========================================================
-- FEED SHIPMENTS
-- =========================================================

INSERT INTO feed_shipments
(id, farm_id, received_by, feed_type, quantity_kg, price_per_kg, total_amount, shipment_date, supplier_name, invoice_number, dispatch_quantity_kg, verified, manager_confirmed, manager_rejected, manager_override_reason, payment_released, override_status, created_at)
VALUES
(940001, 908001, NULL, '909001::SOW_FEED', NULL, 1550.0, 775000.0, CURDATE(),
 'Yangon Premium Feed Mill', 'HMB-FEED-001', 500.0, 0, 0, 0, NULL, 0, 'PENDING_VERIFICATION', NOW()),

(940002, 908001, NULL, '909002::GROWER_FEED', NULL, 1420.0, 710000.0, CURDATE(),
 'Yangon Premium Feed Mill', 'HMB-FEED-002', 500.0, 0, 0, 0, NULL, 0, 'PENDING_VERIFICATION', NOW()),

(940101, 908002, NULL, '909003::PIGLET_FEED', NULL, 1680.0, 672000.0, CURDATE(),
 'Bago Agro Feed Factory', 'BGO-FEED-001', 400.0, 0, 0, 0, NULL, 0, 'PENDING_VERIFICATION', NOW()),

(940102, 908002, NULL, '909004::FINISHER_FEED', NULL, 1390.0, 834000.0, CURDATE(),
 'Bago Agro Feed Factory', 'BGO-FEED-002', 600.0, 0, 0, 0, NULL, 0, 'PENDING_VERIFICATION', NOW());

-- =========================================================
-- FINANCE
-- =========================================================

INSERT INTO finance_transactions
(id, farm_id, type, category, amount, description, reference_id, status, created_at)
VALUES
(980001, 908001, 'EXPENSE', 'FEED', 320000.0, 'Hmawbi demo feed stock opening balance', 'HMB-FEED-OPEN', 'COMPLETED', NOW()),
(980002, 908001, 'INCOME', 'PIG_SALE', 430000.0, 'Hmawbi market pig demo order', 'HMB-PIG-ORDER', 'PENDING', NOW()),
(980003, 908002, 'EXPENSE', 'FEED', 360000.0, 'Bago demo feed stock opening balance', 'BGO-FEED-OPEN', 'COMPLETED', NOW()),
(980004, 908002, 'INCOME', 'PIG_SALE', 455000.0, 'Bago market pig demo order', 'BGO-PIG-ORDER', 'PENDING', NOW());

-- =========================================================
-- CUSTOMER ORDERS
-- =========================================================

INSERT INTO pig_orders
(id, customer_id, status, total_amount, qr_code_path, order_reference, paid_at, created_at)
VALUES
(990001, 912001, 'PENDING', 430000.0, NULL, 'HMB-PIG-ORDER', NULL, NOW()),
(990002, 912001, 'PENDING', 455000.0, NULL, 'BGO-PIG-ORDER', NULL, NOW());

INSERT INTO pig_order_items
(id, order_id, pig_id, unit_price)
VALUES
(991001, 990001, 950005, 430000.0),
(991002, 990002, 950105, 455000.0);

INSERT INTO semen_orders
(id, customer_id, boar_id, quantity, price_per_unit, total_amount, status, qr_code_path, order_reference, paid_at, created_at)
VALUES
(992001, 912001, 950002, 3, 60000.0, 180000.0, 'PENDING', NULL, 'HMB-SEMEN-ORDER', NULL, NOW()),
(992002, 912001, 950102, 2, 65000.0, 130000.0, 'PENDING', NULL, 'BGO-SEMEN-ORDER', NULL, NOW());

-- =========================================================
-- FINAL FIXES
-- =========================================================

UPDATE daily_tasks
SET status = 'PENDING',
    staff_notes = NULL,
    supervisor_comments = NULL,
    submitted_at = NULL,
    reviewed_at = NULL,
    reviewed_by = NULL
WHERE task_date > CURDATE()
  AND id >= 970001
  AND id <= 970999;

UPDATE pigs
SET listed_for_sale = FALSE,
    listed_for_sale_date = NULL
WHERE status = 'PENDING_SALE_APPROVAL';

UPDATE pigs
SET listed_for_sale = TRUE,
    listed_for_sale_date = COALESCE(listed_for_sale_date, CURDATE())
WHERE status = 'FOR_SALE';

UPDATE pigs
SET listed_for_sale = FALSE,
    listed_for_sale_date = NULL
WHERE status = 'SOLD';

-- =========================================================
-- CHECK RESULT
-- =========================================================

SELECT '2 FARM DEMO DATA READY - READY FOR SELL FLOW ENABLED' AS result;

SHOW COLUMNS FROM pigs LIKE 'status';
SHOW COLUMNS FROM pigs LIKE 'sale_price';
SHOW COLUMNS FROM pigs LIKE 'listed_for_sale';
SHOW COLUMNS FROM pigs LIKE 'listed_for_sale_date';
SHOW COLUMNS FROM pigs LIKE 'sold_date';
SHOW COLUMNS FROM pigs LIKE 'current_weight';
SHOW COLUMNS FROM pigs LIKE 'photo_path';
SHOW COLUMNS FROM pigs LIKE 'notes';

SELECT
    id,
    code,
    status,
    listed_for_sale,
    sale_price,
    current_weight,
    photo_path,
    listed_for_sale_date,
    sold_date,
    notes
FROM pigs
WHERE status IN ('PENDING_SALE_APPROVAL', 'FOR_SALE', 'SOLD')
ORDER BY id DESC;

SELECT id, task_date, assigned_staff_id, task_name, status, submitted_at, reviewed_at, reviewed_by
FROM daily_tasks
WHERE id BETWEEN 970001 AND 970999
ORDER BY task_date, assigned_staff_id, id;

SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;