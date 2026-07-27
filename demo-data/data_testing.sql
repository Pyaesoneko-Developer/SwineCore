-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: swinecore_testing
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `attendance`
--

DROP TABLE IF EXISTS `attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `attended` bit(1) NOT NULL,
  `clock_in_time` datetime(6) DEFAULT NULL,
  `clock_out_time` datetime(6) DEFAULT NULL,
  `clocked_out` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `early_departure_reason` varchar(255) DEFAULT NULL,
  `status` enum('CONFIRMED_PENALTY','EARLY_DEPARTURE','EXCUSED_EARLY_LEAVE','NORMAL') NOT NULL,
  `wage_deduction_amount` double DEFAULT NULL,
  `work_date` date NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_att_user_date` (`user_id`,`work_date`),
  CONSTRAINT `FKjcaqd29v2qy723owsdah2t8vx` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=961001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance`
--

LOCK TABLES `attendance` WRITE;
/*!40000 ALTER TABLE `attendance` DISABLE KEYS */;
INSERT INTO `attendance` VALUES (960001,_binary '','2026-07-24 08:00:00.000000',NULL,_binary '\0','2026-07-24 10:02:50.000000','sick','EARLY_DEPARTURE',1.1833333333333333,'2026-07-24',910005),(960002,_binary '','2026-07-24 08:04:00.000000',NULL,_binary '\0','2026-07-24 10:02:50.000000',NULL,'NORMAL',NULL,'2026-07-24',910007),(960003,_binary '','2026-07-24 08:02:00.000000',NULL,_binary '\0','2026-07-24 10:02:50.000000',NULL,'NORMAL',NULL,'2026-07-24',910010),(960004,_binary '','2026-07-24 08:06:00.000000',NULL,_binary '\0','2026-07-24 10:02:50.000000',NULL,'NORMAL',NULL,'2026-07-24',910012),(960005,_binary '','2026-07-23 08:00:00.000000','2026-07-23 17:02:00.000000',_binary '','2026-07-24 10:02:50.000000',NULL,'NORMAL',0,'2026-07-23',910005),(960006,_binary '','2026-07-23 08:05:00.000000','2026-07-23 17:00:00.000000',_binary '','2026-07-24 10:02:50.000000',NULL,'NORMAL',0,'2026-07-23',910007),(960007,_binary '','2026-07-23 08:03:00.000000','2026-07-23 16:20:00.000000',_binary '','2026-07-24 10:02:50.000000','Family matter demo reason','EARLY_DEPARTURE',1500,'2026-07-23',910010),(960008,_binary '','2026-07-23 08:10:00.000000','2026-07-23 17:10:00.000000',_binary '','2026-07-24 10:02:50.000000',NULL,'NORMAL',0,'2026-07-23',910012),(961000,_binary '','2026-07-24 08:13:50.031755',NULL,_binary '\0','2026-07-24 08:13:50.037573',NULL,'NORMAL',NULL,'2026-07-24',910004);
/*!40000 ALTER TABLE `attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `birth_form_rotation`
--

DROP TABLE IF EXISTS `birth_form_rotation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `birth_form_rotation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assigned_date` date DEFAULT NULL,
  `submitted_today` bit(1) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `building_id` bigint NOT NULL,
  `current_staff_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK7ekcp8vattrx397wyhvxrcuaa` (`building_id`),
  KEY `FKpdm93l3c8jn6qxjvfy0m4wh5k` (`current_staff_id`),
  CONSTRAINT `FK3ae7d3pb11uu7yc1gsg8mh5ey` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`),
  CONSTRAINT `FKpdm93l3c8jn6qxjvfy0m4wh5k` FOREIGN KEY (`current_staff_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `birth_form_rotation`
--

LOCK TABLES `birth_form_rotation` WRITE;
/*!40000 ALTER TABLE `birth_form_rotation` DISABLE KEYS */;
/*!40000 ALTER TABLE `birth_form_rotation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `birth_records`
--

DROP TABLE IF EXISTS `birth_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `birth_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `alive_piglets` int NOT NULL,
  `birth_date` date DEFAULT NULL,
  `confirmed_by_staff` bit(1) NOT NULL,
  `confirmed_by_supervisor` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `dead_piglets` int NOT NULL,
  `litter_size` int NOT NULL,
  `building_id` bigint NOT NULL,
  `mother_id` bigint NOT NULL,
  `recorded_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqlsp803jq67edngs8g4ukyvhd` (`building_id`),
  KEY `FKn2yfa3rydqawsxqf8sowyeqo3` (`mother_id`),
  KEY `FKfotn5ko9cq2m9cdoh9xnrgsxy` (`recorded_by`),
  CONSTRAINT `FKfotn5ko9cq2m9cdoh9xnrgsxy` FOREIGN KEY (`recorded_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKn2yfa3rydqawsxqf8sowyeqo3` FOREIGN KEY (`mother_id`) REFERENCES `pigs` (`id`),
  CONSTRAINT `FKqlsp803jq67edngs8g4ukyvhd` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=956001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `birth_records`
--

LOCK TABLES `birth_records` WRITE;
/*!40000 ALTER TABLE `birth_records` DISABLE KEYS */;
INSERT INTO `birth_records` VALUES (955001,4,'2026-07-24',_binary '',_binary '\0','2026-07-24 10:02:50.000000',1,5,909001,950001,910005),(955002,4,'2026-07-24',_binary '',_binary '\0','2026-07-24 10:02:50.000000',1,5,909003,950201,910010),(955003,6,'2026-07-23',_binary '',_binary '','2026-07-24 10:02:50.000000',0,6,909001,950002,910005),(955004,6,'2026-07-23',_binary '',_binary '','2026-07-24 10:02:50.000000',1,7,909003,950202,910010),(956000,10,'2026-07-24',_binary '',_binary '\0','2026-07-24 08:34:09.004117',3,13,909001,950004,910005);
/*!40000 ALTER TABLE `birth_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `breeding_records`
--

DROP TABLE IF EXISTS `breeding_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `breeding_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `breeding_date` date NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `notes` varchar(1000) DEFAULT NULL,
  `birth_record_id` bigint DEFAULT NULL,
  `building_id` bigint NOT NULL,
  `father_genetics_id` bigint NOT NULL,
  `recorded_by` bigint DEFAULT NULL,
  `sow_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKmnudb4b7jw8vur2o5aqybybbb` (`birth_record_id`),
  KEY `idx_breeding_record_sow` (`sow_id`),
  KEY `idx_breeding_record_building` (`building_id`),
  KEY `FKac81qia0nhiw1n36bvrtna0g7` (`father_genetics_id`),
  KEY `FK7mt06mxao9q7h4gxl8xigjnlo` (`recorded_by`),
  CONSTRAINT `FK7mt06mxao9q7h4gxl8xigjnlo` FOREIGN KEY (`recorded_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKac81qia0nhiw1n36bvrtna0g7` FOREIGN KEY (`father_genetics_id`) REFERENCES `genetics` (`id`),
  CONSTRAINT `FKjxtbjkgwxk1ioop5nhgrovep1` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`),
  CONSTRAINT `FKngd1xde0r0meeebpy0aef7cgv` FOREIGN KEY (`birth_record_id`) REFERENCES `birth_records` (`id`),
  CONSTRAINT `FKpbx7b7483lxh1xtyug7ga88qg` FOREIGN KEY (`sow_id`) REFERENCES `pigs` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `breeding_records`
--

LOCK TABLES `breeding_records` WRITE;
/*!40000 ALTER TABLE `breeding_records` DISABLE KEYS */;
/*!40000 ALTER TABLE `breeding_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `buildings`
--

DROP TABLE IF EXISTS `buildings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `buildings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `birth_form_rotation_index` int NOT NULL,
  `code` varchar(4) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `farm_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_building_farm` (`farm_id`),
  CONSTRAINT `FK9863dsp96kueukwywj8ie4e7o` FOREIGN KEY (`farm_id`) REFERENCES `farms` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=910002 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `buildings`
--

LOCK TABLES `buildings` WRITE;
/*!40000 ALTER TABLE `buildings` DISABLE KEYS */;
INSERT INTO `buildings` VALUES (909001,0,'HB1','2026-07-24 10:02:50.000000','Sows, boars, mating pens, farrowing crates and piglets','Hmawbi Breeding House A',908001),(909002,0,'HG2','2026-07-24 10:02:50.000000','Grower, finisher and market-ready pigs','Hmawbi Grower House B',908001),(909003,0,'BB1','2026-07-24 10:02:50.000000','Breeding sows, boars and newborn piglets','Bago Breeding House A',908002),(909004,0,'BG2','2026-07-24 10:02:50.000000','Grower, finisher and market-ready pigs','Bago Grower House B',908002),(910000,0,'B001','2026-07-25 06:25:02.026489',NULL,'Mandalay Breeding Unit A',909000),(910001,0,'B002','2026-07-25 06:30:22.958502',NULL,'Taunggyi Breeding Unit A',909001);
/*!40000 ALTER TABLE `buildings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_accounts`
--

DROP TABLE IF EXISTS `customer_accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_accounts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `profile_image_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKa2cop768ij76qwvh1t4lpprfa` (`email`),
  KEY `idx_cust_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=913001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_accounts`
--

LOCK TABLES `customer_accounts` WRITE;
/*!40000 ALTER TABLE `customer_accounts` DISABLE KEYS */;
INSERT INTO `customer_accounts` VALUES (912001,'Bayintnaung Market, Mayangone Township, Yangon','2026-07-24 10:02:50.000000','buyer.yangon@swinecore.mm',_binary '','Yangon Meat Shop Buyer','$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS','09455300001','/images/logo.png'),(913000,NULL,'2026-07-25 15:54:12.479250','ckeeyar@gmail.com',_binary '','Wutyi Hlaing','$2a$12$oaqfeHNGmd6Jor1LbplyA.vVwVNH.lI/bfRuB1k5aRT1VQ.mbML1S','+9542473002','/uploads/customers/60ded85b-0c05-4bcd-b1f7-0dc271ae4559.jpg');
/*!40000 ALTER TABLE `customer_accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `daily_tasks`
--

DROP TABLE IF EXISTS `daily_tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `daily_tasks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `is_standard_task` bit(1) NOT NULL,
  `reviewed_at` datetime(6) DEFAULT NULL,
  `staff_notes` varchar(255) DEFAULT NULL,
  `status` enum('APPROVED','IN_PROGRESS','PENDING','REJECTED','SUBMITTED') NOT NULL,
  `submitted_at` datetime(6) DEFAULT NULL,
  `supervisor_comments` varchar(255) DEFAULT NULL,
  `task_date` date NOT NULL,
  `task_description` varchar(255) DEFAULT NULL,
  `task_name` varchar(255) NOT NULL,
  `assigned_staff_id` bigint DEFAULT NULL,
  `building_id` bigint NOT NULL,
  `pig_id` bigint DEFAULT NULL,
  `reviewed_by` bigint DEFAULT NULL,
  `rule_schedule_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_task_staff_date` (`assigned_staff_id`,`task_date`),
  KEY `idx_task_building_date` (`building_id`,`task_date`),
  KEY `FKsiikkwy3r5caq9at8ib68d2ch` (`pig_id`),
  KEY `FKm3uf9thdt7667319gs4qr6bee` (`reviewed_by`),
  KEY `FKougiu3jgno5lwsgwauaj30grx` (`rule_schedule_id`),
  CONSTRAINT `FKbtcq7afyvy7kslvb2hmsqxuit` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`),
  CONSTRAINT `FKm3uf9thdt7667319gs4qr6bee` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKougiu3jgno5lwsgwauaj30grx` FOREIGN KEY (`rule_schedule_id`) REFERENCES `rule_schedules` (`id`),
  CONSTRAINT `FKqxawhl19jn5mxy0v5myfax700` FOREIGN KEY (`assigned_staff_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKsiikkwy3r5caq9at8ib68d2ch` FOREIGN KEY (`pig_id`) REFERENCES `pigs` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=971000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `daily_tasks`
--

LOCK TABLES `daily_tasks` WRITE;
/*!40000 ALTER TABLE `daily_tasks` DISABLE KEYS */;
INSERT INTO `daily_tasks` VALUES (970001,'2026-07-24 10:02:50.000000',_binary '','2026-07-24 09:12:46.425834','Well','APPROVED','2026-07-24 09:11:53.890127',NULL,'2026-07-24','Check sow pens, piglet pens, water lines, temperature, hygiene and general health.','Daily Care Routine',910005,909001,NULL,910004,NULL),(970002,'2026-07-24 10:02:50.000000',_binary '\0','2026-07-24 09:12:48.240885','Good job','APPROVED','2026-07-24 09:12:09.327006',NULL,'2026-07-24','Administer Iron Supplement 2 ml to HMB-PIGLET-001.','MEDICATION: Hmawbi Day 1 Iron Supplement',910005,909001,950006,910004,920001),(970003,'2026-07-24 10:02:50.000000',_binary '\0','2026-07-24 09:12:49.707991','Vaccination completed. Piglet active and eating well.','APPROVED','2026-07-24 09:02:50.000000',NULL,'2026-07-24','Vaccination test task for HMB-PIGLET-002.','VACCINATION: Hmawbi Day 30 PRRS Vaccination',910005,909001,950007,910004,920002),(970004,'2026-07-24 10:02:50.000000',_binary '\0','2026-07-24 08:32:50.000000','Medication completed.','APPROVED','2026-07-24 08:02:50.000000','-','2026-07-24','Approved history demo for supervisor report.','MEDICATION: Hmawbi Piglet Check',910005,909001,950008,910004,920001),(970005,'2026-07-24 10:02:50.000000',_binary '\0','2026-07-24 08:02:50.000000','Piglet checked.','REJECTED','2026-07-24 07:02:50.000000','Please add temperature and feeding condition.','2026-07-24','Rejected history demo for supervisor correction button.','Piglet Health Follow-up',910005,909001,950009,910004,NULL),(970011,'2026-07-24 10:02:50.000000',_binary '',NULL,NULL,'PENDING',NULL,NULL,'2026-07-24','Check grower house water, feed line, ventilation and sick pigs.','Grower House Daily Check',910007,909002,NULL,NULL,NULL),(970012,'2026-07-24 10:02:50.000000',_binary '\0',NULL,NULL,'PENDING',NULL,NULL,'2026-07-24','Feed GROWER_FEED 2.5 kg for HMB-GROWER-B-001.','FEEDING: Hmawbi Grower Feed Program',910007,909002,950101,NULL,920003),(970013,'2026-07-24 10:02:50.000000',_binary '\0',NULL,'Finisher feed completed. Water supply normal.','SUBMITTED','2026-07-24 09:12:50.000000',NULL,'2026-07-24','Feed FINISHER_FEED 3.2 kg for HMB-FINISHER-B-001.','FEEDING: Hmawbi Finisher Feed Program',910007,909002,950109,NULL,920004),(970014,'2026-07-24 10:02:50.000000',_binary '\0',NULL,NULL,'PENDING',NULL,NULL,'2026-07-24','Check weight, health, body condition and sale readiness for HMB-READY-SELL-001.','Ready To Sell Check',910007,909002,950118,NULL,NULL),(970021,'2026-07-24 10:02:50.000000',_binary '',NULL,NULL,'PENDING',NULL,NULL,'2026-07-24','Check sow pens, boar pen, newborn piglets, water and hygiene.','Bago Breeding Daily Care',910010,909003,NULL,NULL,NULL),(970022,'2026-07-24 10:02:50.000000',_binary '\0',NULL,NULL,'PENDING',NULL,NULL,'2026-07-24','Administer Iron Supplement 2 ml to BGO-PIGLET-001.','MEDICATION: Bago Day 1 Iron Supplement',910010,909003,950206,NULL,920005),(970023,'2026-07-24 10:02:50.000000',_binary '\0',NULL,'Mycoplasma vaccination completed. Piglet condition normal.','SUBMITTED','2026-07-24 09:02:50.000000',NULL,'2026-07-24','Vaccination test task for BGO-PIGLET-002.','VACCINATION: Bago Day 28 Mycoplasma Vaccination',910010,909003,950207,NULL,920006),(970024,'2026-07-24 10:02:50.000000',_binary '\0','2026-07-24 08:42:50.000000','Piglet checked and healthy.','APPROVED','2026-07-24 08:02:50.000000','-','2026-07-24','Approved history demo for Bago supervisor.','Bago Piglet Health Review',910010,909003,950208,910009,NULL),(970031,'2026-07-24 10:02:50.000000',_binary '',NULL,NULL,'PENDING',NULL,NULL,'2026-07-24','Check grower house feed, water, temperature and general health.','Bago Grower House Daily Check',910012,909004,NULL,NULL,NULL),(970032,'2026-07-24 10:02:50.000000',_binary '\0',NULL,NULL,'PENDING',NULL,NULL,'2026-07-24','Feed GROWER_FEED 2.6 kg for BGO-GROWER-B-001.','FEEDING: Bago Grower Feed Program',910012,909004,950301,NULL,920007),(970033,'2026-07-24 10:02:50.000000',_binary '\0',NULL,'Finisher feed completed. No sick pigs found.','SUBMITTED','2026-07-24 09:17:50.000000',NULL,'2026-07-24','Feed FINISHER_FEED 3.3 kg for BGO-FINISHER-B-001.','FEEDING: Bago Finisher Feed Program',910012,909004,950309,NULL,920008),(970034,'2026-07-24 10:02:50.000000',_binary '\0',NULL,NULL,'PENDING',NULL,NULL,'2026-07-24','Check weight, health and sale readiness for BGO-READY-SELL-001.','Ready To Sell Check',910012,909004,950318,NULL,NULL),(970101,'2026-07-24 10:02:50.000000',_binary '',NULL,NULL,'PENDING',NULL,NULL,'2026-07-25','SHOW: tomorrow task appears for Hmawbi breeding staff.','Tomorrow Daily Care Routine',910005,909001,NULL,NULL,NULL),(970102,'2026-07-24 10:02:50.000000',_binary '\0',NULL,NULL,'PENDING',NULL,NULL,'2026-07-25','SHOW: PRRS Vaccine 2 ml task appears tomorrow for piglet.','TOMORROW VACCINATION: Hmawbi Day 30 PRRS',910005,909001,950007,NULL,920002),(970103,'2026-07-24 10:02:50.000000',_binary '\0',NULL,NULL,'PENDING',NULL,NULL,'2026-07-25','SHOW: piglet medication/health check task appears tomorrow.','Tomorrow Piglet Health Check',910005,909001,950006,NULL,920001),(970111,'2026-07-24 10:02:50.000000',_binary '',NULL,NULL,'PENDING',NULL,NULL,'2026-07-25','SHOW: tomorrow task appears for Hmawbi grower staff.','Tomorrow Grower House Check',910007,909002,NULL,NULL,NULL),(970112,'2026-07-24 10:02:50.000000',_binary '\0',NULL,NULL,'PENDING',NULL,NULL,'2026-07-25','SHOW: grower feeding task appears tomorrow.','TOMORROW FEEDING: Hmawbi Grower Feed',910007,909002,950102,NULL,920003),(970113,'2026-07-24 10:02:50.000000',_binary '\0',NULL,NULL,'PENDING',NULL,NULL,'2026-07-25','SHOW: ready-to-sell testing task appears tomorrow.','Tomorrow Ready To Sell Check',910007,909002,950119,NULL,NULL),(970121,'2026-07-24 10:02:50.000000',_binary '',NULL,NULL,'PENDING',NULL,NULL,'2026-07-25','SHOW: tomorrow task appears for Bago breeding staff.','Tomorrow Bago Breeding Check',910010,909003,NULL,NULL,NULL),(970122,'2026-07-24 10:02:50.000000',_binary '\0',NULL,NULL,'PENDING',NULL,NULL,'2026-07-25','SHOW: Mycoplasma Vaccine 2 ml task appears tomorrow for piglet.','TOMORROW VACCINATION: Bago Mycoplasma',910010,909003,950207,NULL,920006),(970123,'2026-07-24 10:02:50.000000',_binary '\0',NULL,NULL,'PENDING',NULL,NULL,'2026-07-25','SHOW: piglet medication/health check task appears tomorrow.','Tomorrow Bago Piglet Health Check',910010,909003,950206,NULL,920005),(970131,'2026-07-24 10:02:50.000000',_binary '',NULL,NULL,'PENDING',NULL,NULL,'2026-07-25','SHOW: tomorrow task appears for Bago grower staff.','Tomorrow Bago Grower Check',910012,909004,NULL,NULL,NULL),(970132,'2026-07-24 10:02:50.000000',_binary '\0',NULL,NULL,'PENDING',NULL,NULL,'2026-07-25','SHOW: finisher feeding task appears tomorrow.','TOMORROW FEEDING: Bago Finisher Feed',910012,909004,950310,NULL,920008),(970133,'2026-07-24 10:02:50.000000',_binary '\0',NULL,NULL,'PENDING',NULL,NULL,'2026-07-25','SHOW: ready-to-sell testing task appears tomorrow.','Tomorrow Ready To Sell Check',910012,909004,950319,NULL,NULL),(970201,'2026-07-24 10:02:50.000000',_binary '',NULL,NULL,'PENDING',NULL,NULL,'2026-07-26','Next day preview task for report/calendar testing.','Next Day Breeding Routine',910005,909001,NULL,NULL,NULL),(970202,'2026-07-24 10:02:50.000000',_binary '',NULL,NULL,'PENDING',NULL,NULL,'2026-07-26','Next day preview task for report/calendar testing.','Next Day Grower Routine',910007,909002,NULL,NULL,NULL),(970203,'2026-07-24 10:02:50.000000',_binary '',NULL,NULL,'PENDING',NULL,NULL,'2026-07-26','Next day preview task for report/calendar testing.','Next Day Bago Breeding Routine',910010,909003,NULL,NULL,NULL),(970204,'2026-07-24 10:02:50.000000',_binary '',NULL,NULL,'PENDING',NULL,NULL,'2026-07-26','Next day preview task for report/calendar testing.','Next Day Bago Grower Routine',910012,909004,NULL,NULL,NULL);
/*!40000 ALTER TABLE `daily_tasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `farm_ads`
--

DROP TABLE IF EXISTS `farm_ads`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `farm_ads` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `category` enum('FARM_PROMOTIONS','LIVE_LIVESTOCK','SEMEN_SUPPLY') NOT NULL,
  `contact_info` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `farm_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKb78ohtxseom7g6jomfmu3iqpi` (`created_by`),
  KEY `FKfesmtlwovxdxw72so6tsrrv0u` (`farm_id`),
  CONSTRAINT `FKb78ohtxseom7g6jomfmu3iqpi` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKfesmtlwovxdxw72so6tsrrv0u` FOREIGN KEY (`farm_id`) REFERENCES `farms` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `farm_ads`
--

LOCK TABLES `farm_ads` WRITE;
/*!40000 ALTER TABLE `farm_ads` DISABLE KEYS */;
INSERT INTO `farm_ads` VALUES (1,_binary '','SEMEN_SUPPLY','manager.hmawbi@swinecore.mm','2026-07-25 16:09:05.243486','Certified premium boar semen available for artificial insemination. Top genetics for healthy and strong offspring.','/uploads/ads/0660cedb-ab8e-4594-aac6-65bf8357d511.jpg','Premium Breeding Stock',910002,908001),(2,_binary '','LIVE_LIVESTOCK','0987654321','2026-07-25 16:12:09.448256','Top-quality Pietrain breeding stock semen. High lean meat yield and strong genetics for maximum performance.','/uploads/ads/3e1d9769-d92b-455d-b52b-b82ca8a595b4.jpg','Premium Breeding Stock',910002,908002),(3,_binary '','FARM_PROMOTIONS','09890776453','2026-07-25 16:18:06.336809','Special farm promotion on high-quality Landrace breeding stock and semen. Known for excellent maternal traits, high prolificacy, and fast growth rates. Order now for exclusive farm offers.','/uploads/ads/2b5e7f6b-6ed6-4275-86e1-b7257b10c8c7.jpg','Premium Landrace Breeding ',910002,909000),(4,_binary '','LIVE_LIVESTOCK','manager.taunggyi@swinecore.mm','2026-07-25 16:23:11.232467','High-performance Duroc breeding stock and semen known for rapid growth, exceptional feed efficiency, and superior meat quality with excellent marbling. Contact us today for special farm promotions','/uploads/ads/4d914ba4-042a-4671-ac4e-7e8b8df73d38.jpg','Duroc Breeding Stock',910002,909001);
/*!40000 ALTER TABLE `farm_ads` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `farms`
--

DROP TABLE IF EXISTS `farms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `farms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(4) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK3214ianrcn3ndqcuta6d7nq2f` (`code`),
  UNIQUE KEY `UK5eca4s3pnujm1syymfk36pkgu` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=909002 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `farms`
--

LOCK TABLES `farms` WRITE;
/*!40000 ALTER TABLE `farms` DISABLE KEYS */;
INSERT INTO `farms` VALUES (908001,'HMB1','2026-07-24 10:02:50.000000','Show demo farm with breeding, grower, sale approval, tasks, reports, inventory and customer marketplace data.','/images/farms/hmawbi-swine-farm.jpg',17.1074,'Hmawbi Township, Yangon Region',96.0451,'Hmawbi Integrated Swine Farm'),(908002,'BGO2','2026-07-24 10:02:50.000000','Second show demo farm for multi-farm testing, staff records, tomorrow tasks and sale approval.','/images/farms/bago-swine-farm.jpg',17.3352,'Bago Township, Bago Region',96.481,'Bago Golden Valley Swine Farm'),(909000,'F001','2026-07-25 06:25:01.978379','Creation farm',NULL,NULL,'Pyin Oo Lwin Township, Mandalay Region',NULL,'Mandalay Highland'),(909001,'F002','2026-07-25 06:30:22.952767','creation farm',NULL,NULL,'Taunggyi Township, Shan State',NULL,'Taunggyi Green Valley Swine Farm');
/*!40000 ALTER TABLE `farms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feed_shipments`
--

DROP TABLE IF EXISTS `feed_shipments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feed_shipments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `dispatch_quantity_kg` double DEFAULT NULL,
  `feed_type` varchar(255) DEFAULT NULL,
  `invoice_number` varchar(255) DEFAULT NULL,
  `manager_confirmed` bit(1) NOT NULL,
  `manager_override_reason` varchar(255) DEFAULT NULL,
  `manager_rejected` bit(1) NOT NULL,
  `override_status` enum('AUTO_APPROVED','CONFIRMED','PENDING_MANAGER_OVERRIDE','PENDING_VERIFICATION','REJECTED') NOT NULL,
  `payment_released` bit(1) NOT NULL,
  `price_per_kg` double DEFAULT NULL,
  `quantity_kg` double DEFAULT NULL,
  `shipment_date` date DEFAULT NULL,
  `supplier_name` varchar(255) DEFAULT NULL,
  `total_amount` double DEFAULT NULL,
  `verified` bit(1) NOT NULL,
  `farm_id` bigint NOT NULL,
  `received_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9n31uwmq4yhlspoi7x7vlt4pv` (`farm_id`),
  KEY `FKyuxbmehv8pe944fjdd3gnlsk` (`received_by`),
  CONSTRAINT `FK9n31uwmq4yhlspoi7x7vlt4pv` FOREIGN KEY (`farm_id`) REFERENCES `farms` (`id`),
  CONSTRAINT `FKyuxbmehv8pe944fjdd3gnlsk` FOREIGN KEY (`received_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=941000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feed_shipments`
--

LOCK TABLES `feed_shipments` WRITE;
/*!40000 ALTER TABLE `feed_shipments` DISABLE KEYS */;
INSERT INTO `feed_shipments` VALUES (940001,'2026-07-24 10:02:50.000000',500,'909001::SOW_FEED','HMB-FEED-001',_binary '\0',NULL,_binary '\0','AUTO_APPROVED',_binary '\0',1550,500,'2026-07-24','Yangon Premium Feed Mill',775000,_binary '\0',908001,910004),(940002,'2026-07-24 10:02:50.000000',500,'909002::GROWER_FEED','HMB-FEED-002',_binary '\0',NULL,_binary '\0','PENDING_VERIFICATION',_binary '\0',1420,NULL,'2026-07-24','Yangon Premium Feed Mill',710000,_binary '\0',908001,NULL),(940003,'2026-07-24 10:02:50.000000',600,'909002::FINISHER_FEED','HMB-FEED-003',_binary '',NULL,_binary '\0','CONFIRMED',_binary '',1500,600,'2026-07-23','Yangon Premium Feed Mill',900000,_binary '',908001,910003),(940004,'2026-07-24 10:02:50.000000',500,'909001::PIGLET_FEED','HMB-FEED-004',_binary '\0','Quantity mismatch demo for manager reject testing.',_binary '','REJECTED',_binary '\0',1700,300,'2026-07-22','Yangon Premium Feed Mill',510000,_binary '',908001,910003),(940101,'2026-07-24 10:02:50.000000',400,'909003::PIGLET_FEED','BGO-FEED-001',_binary '\0',NULL,_binary '\0','PENDING_VERIFICATION',_binary '\0',1680,NULL,'2026-07-24','Bago Agro Feed Factory',672000,_binary '\0',908002,NULL),(940102,'2026-07-24 10:02:50.000000',600,'909004::FINISHER_FEED','BGO-FEED-002',_binary '\0',NULL,_binary '\0','PENDING_VERIFICATION',_binary '\0',1390,NULL,'2026-07-24','Bago Agro Feed Factory',834000,_binary '\0',908002,NULL),(940103,'2026-07-24 10:02:50.000000',700,'909004::GROWER_FEED','BGO-FEED-003',_binary '',NULL,_binary '\0','CONFIRMED',_binary '',1450,700,'2026-07-23','Bago Agro Feed Factory',1015000,_binary '',908002,910008),(940104,'2026-07-24 10:02:50.000000',450,'909003::SOW_FEED','BGO-FEED-004',_binary '\0','Damaged feed bags demo for manager reject testing.',_binary '','REJECTED',_binary '\0',1580,350,'2026-07-22','Bago Agro Feed Factory',553000,_binary '',908002,910008);
/*!40000 ALTER TABLE `feed_shipments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `finance_transactions`
--

DROP TABLE IF EXISTS `finance_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `finance_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` double DEFAULT NULL,
  `category` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `reference_id` varchar(255) DEFAULT NULL,
  `status` enum('COMPLETED','FAILED','PENDING','REFUNDED') NOT NULL,
  `type` varchar(255) NOT NULL,
  `farm_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_fin_farm` (`farm_id`),
  KEY `idx_fin_type` (`type`),
  CONSTRAINT `FKo423hbdgl5ko5oscjyk7bg8ru` FOREIGN KEY (`farm_id`) REFERENCES `farms` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=981008 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `finance_transactions`
--

LOCK TABLES `finance_transactions` WRITE;
/*!40000 ALTER TABLE `finance_transactions` DISABLE KEYS */;
INSERT INTO `finance_transactions` VALUES (980001,775000,'FEED','2026-07-24 10:02:50.000000','Hmawbi sow feed shipment pending verification.','HMB-FEED-001','PENDING','EXPENSE',908001),(980002,710000,'FEED','2026-07-24 10:02:50.000000','Hmawbi grower feed shipment pending verification.','HMB-FEED-002','PENDING','EXPENSE',908001),(980003,900000,'FEED','2026-07-24 10:02:50.000000','Hmawbi confirmed finisher feed shipment.','HMB-FEED-003','COMPLETED','EXPENSE',908001),(980004,545000,'PIG_SALE','2026-07-24 10:02:50.000000','Hmawbi marketplace pig order demo.','HMB-PIG-ORDER-001','PENDING','INCOME',908001),(980005,180000,'SEMEN_SALE','2026-07-24 10:02:50.000000','Hmawbi semen order demo.','HMB-SEMEN-ORDER-001','PENDING','INCOME',908001),(980101,672000,'FEED','2026-07-24 10:02:50.000000','Bago piglet feed shipment pending verification.','BGO-FEED-001','PENDING','EXPENSE',908002),(980102,834000,'FEED','2026-07-24 10:02:50.000000','Bago finisher feed shipment pending verification.','BGO-FEED-002','PENDING','EXPENSE',908002),(980103,1015000,'FEED','2026-07-24 10:02:50.000000','Bago confirmed grower feed shipment.','BGO-FEED-003','COMPLETED','EXPENSE',908002),(980104,560000,'PIG_SALE','2026-07-24 10:02:50.000000','Bago marketplace pig order demo.','BGO-PIG-ORDER-001','PENDING','INCOME',908002),(980105,130000,'SEMEN_SALE','2026-07-24 10:02:50.000000','Bago semen order demo.','BGO-SEMEN-ORDER-001','PENDING','INCOME',908002),(981000,3000000,'SEMEN_SALE','2026-07-24 07:37:10.444485','Semen order SC-064F7B79','SC-064F7B79','COMPLETED','INCOME',908002),(981001,1000000,'SEMEN_SALE','2026-07-24 07:42:31.292577','Semen order SC-18A905E8','SC-18A905E8','COMPLETED','INCOME',908002),(981002,775000,'FEED','2026-07-24 08:32:03.258695','Factory feed shipment expense released. Invoice: HMB-FEED-001','HMB-FEED-001','PENDING','EXPENSE',908001),(981003,780000,'PIG_SALE','2026-07-24 08:42:14.461559','Pig sale order SC-FC16818A','SC-FC16818A','COMPLETED','INCOME',908002),(981004,3395000,'PIG_SALE','2026-07-25 15:57:23.688103','Pig sale order SC-90415BCD','SC-90415BCD','COMPLETED','INCOME',908002),(981005,3000000,'SEMEN_SALE','2026-07-25 15:58:32.048092','Semen order SC-4CF82B92','SC-4CF82B92','COMPLETED','INCOME',908002),(981006,2814000,'PIG_SALE','2026-07-25 16:26:13.819429','Pig sale order SC-8CF1254F','SC-8CF1254F','COMPLETED','INCOME',908001),(981007,360000,'SEMEN_SALE','2026-07-25 16:27:30.780701','Semen order SC-53EC645D','SC-53EC645D','COMPLETED','INCOME',908001);
/*!40000 ALTER TABLE `finance_transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `genetics`
--

DROP TABLE IF EXISTS `genetics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `genetics` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `code` varchar(6) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKt9llqamnngte6xs7qo86siuvc` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=901006 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `genetics`
--

LOCK TABLES `genetics` WRITE;
/*!40000 ALTER TABLE `genetics` DISABLE KEYS */;
INSERT INTO `genetics` VALUES (1,_binary '','Y','Large White — excellent maternal traits','Yorkshire'),(2,_binary '','D','High growth rate, excellent meat quality','Duroc'),(3,_binary '','L','Long body, high litter size','Landrace'),(4,_binary '','P','Lean muscle, stress-susceptible','Pietrain'),(5,_binary '','YL','F1 cross — good mothering','Yorkshire x Landrace'),(6,_binary '','LY','F1 cross — high productivity','Landrace x Yorkshire'),(7,_binary '','DYL','Terminal sire cross','Duroc x Yorkshire x Landrace'),(901005,_binary '','DL','Fast-growing commercial cross','Duroc x Landrace');
/*!40000 ALTER TABLE `genetics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `alert_threshold_kg` double DEFAULT NULL,
  `alert_triggered` bit(1) NOT NULL,
  `feed_type` varchar(255) NOT NULL,
  `quantity_kg` double DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `farm_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_inv_farm_type` (`farm_id`,`feed_type`),
  CONSTRAINT `FK1dqatnqv6kdlsn5ejbxb95dqw` FOREIGN KEY (`farm_id`) REFERENCES `farms` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=931000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory`
--

LOCK TABLES `inventory` WRITE;
/*!40000 ALTER TABLE `inventory` DISABLE KEYS */;
INSERT INTO `inventory` VALUES (930001,5,_binary '\0','Iron Supplement',80,'2026-07-24 10:02:50.000000',908001),(930002,10,_binary '\0','PRRS Vaccine',150,'2026-07-24 10:02:50.000000',908001),(930003,500,_binary '\0','GROWER_FEED',4200,'2026-07-24 10:02:50.000000',908001),(930004,500,_binary '\0','FINISHER_FEED',3800,'2026-07-24 10:02:50.000000',908001),(930005,400,_binary '\0','SOW_FEED',2500,'2026-07-24 10:02:50.000000',908001),(930006,300,_binary '\0','PIGLET_FEED',1600,'2026-07-24 10:02:50.000000',908001),(930101,5,_binary '\0','Iron Supplement',75,'2026-07-24 10:02:50.000000',908002),(930102,10,_binary '\0','Mycoplasma Vaccine',130,'2026-07-24 10:02:50.000000',908002),(930103,500,_binary '\0','GROWER_FEED',4500,'2026-07-24 10:02:50.000000',908002),(930104,600,_binary '\0','FINISHER_FEED',4100,'2026-07-24 10:02:50.000000',908002),(930105,400,_binary '\0','SOW_FEED',2700,'2026-07-24 10:02:50.000000',908002),(930106,300,_binary '\0','PIGLET_FEED',1700,'2026-07-24 10:02:50.000000',908002);
/*!40000 ALTER TABLE `inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pig_order_items`
--

DROP TABLE IF EXISTS `pig_order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pig_order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `unit_price` double DEFAULT NULL,
  `order_id` bigint NOT NULL,
  `pig_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrv5498h0fjllnerqq6sqan92a` (`order_id`),
  KEY `FKfy2dvibli8bmvkkuq8vmocxgx` (`pig_id`),
  CONSTRAINT `FKfy2dvibli8bmvkkuq8vmocxgx` FOREIGN KEY (`pig_id`) REFERENCES `pigs` (`id`),
  CONSTRAINT `FKrv5498h0fjllnerqq6sqan92a` FOREIGN KEY (`order_id`) REFERENCES `pig_orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=992007 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pig_order_items`
--

LOCK TABLES `pig_order_items` WRITE;
/*!40000 ALTER TABLE `pig_order_items` DISABLE KEYS */;
INSERT INTO `pig_order_items` VALUES (991001,545000,990001,950020),(991002,560000,990002,950220),(991003,555000,990003,950117),(991004,565000,990004,950317),(992000,780000,991000,950304),(992001,1600000,991001,950311),(992002,1230000,991001,951017),(992003,565000,991001,950317),(992004,2000000,991002,950120),(992005,254000,991002,950206),(992006,560000,991002,950220);
/*!40000 ALTER TABLE `pig_order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pig_orders`
--

DROP TABLE IF EXISTS `pig_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pig_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `order_reference` varchar(255) DEFAULT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  `qr_code_path` varchar(255) DEFAULT NULL,
  `status` enum('CANCELLED','DISPATCHED','PAID','PENDING','QR_GENERATED') NOT NULL,
  `total_amount` double DEFAULT NULL,
  `customer_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK8su97cu8a4xy2t4l4i92kj41j` (`order_reference`),
  KEY `idx_order_customer` (`customer_id`),
  KEY `idx_order_status` (`status`),
  CONSTRAINT `FKjay5cwgl047ewonpq5h7207w8` FOREIGN KEY (`customer_id`) REFERENCES `customer_accounts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=991003 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pig_orders`
--

LOCK TABLES `pig_orders` WRITE;
/*!40000 ALTER TABLE `pig_orders` DISABLE KEYS */;
INSERT INTO `pig_orders` VALUES (990001,'2026-07-24 10:02:50.000000','HMB-PIG-ORDER-001',NULL,NULL,'PENDING',545000,912001),(990002,'2026-07-24 10:02:50.000000','BGO-PIG-ORDER-001',NULL,NULL,'PENDING',560000,912001),(990003,'2026-07-23 10:02:50.000000','HMB-PIG-ORDER-PAID-001','2026-07-23 10:02:50.000000','/images/qr/demo-paid-hmawbi.png','PAID',555000,912001),(990004,'2026-07-22 10:02:50.000000','BGO-PIG-ORDER-CANCEL-001',NULL,NULL,'CANCELLED',565000,912001),(991000,'2026-07-24 08:40:25.167672','SC-FC16818A','2026-07-24 08:42:14.455558',NULL,'PAID',780000,912001),(991001,'2026-07-25 15:56:43.953468','SC-90415BCD','2026-07-25 15:57:23.672184',NULL,'PAID',3395000,913000),(991002,'2026-07-25 16:23:52.969699','SC-8CF1254F','2026-07-25 16:26:13.819430',NULL,'PAID',2814000,913000);
/*!40000 ALTER TABLE `pig_orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pigs`
--

DROP TABLE IF EXISTS `pigs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pigs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `birth_date` date DEFAULT NULL,
  `code` varchar(64) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `current_weight` double DEFAULT NULL,
  `gender` enum('FEMALE','MALE') NOT NULL,
  `listed_for_sale` bit(1) NOT NULL,
  `listed_for_sale_date` date DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `photo_path` varchar(255) DEFAULT NULL,
  `recorded_date` date DEFAULT NULL,
  `sale_price` double DEFAULT NULL,
  `sold_date` date DEFAULT NULL,
  `status` enum('PIGLET','GROWER','FINISHER','BREEDING_SOW','BREEDING_BOAR','PENDING_SALE_APPROVAL','FOR_SALE','SOLD') NOT NULL,
  `birth_record_id` bigint DEFAULT NULL,
  `building_id` bigint NOT NULL,
  `genetics_id` bigint NOT NULL,
  `mother_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKe4y59l1k3tk13w6be2ojghhn5` (`code`),
  KEY `idx_pig_code` (`code`),
  KEY `idx_pig_building` (`building_id`),
  KEY `idx_pig_status` (`status`),
  KEY `FKjffiry1mrcbed2f2ao91g83ac` (`birth_record_id`),
  KEY `FKnlwxwfy3c9bvhn1grki22v241` (`genetics_id`),
  KEY `FKpyays2120ru7onmmasl471moi` (`mother_id`),
  CONSTRAINT `FKjcq9w7p64kswklka8rmder1lr` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`),
  CONSTRAINT `FKjffiry1mrcbed2f2ao91g83ac` FOREIGN KEY (`birth_record_id`) REFERENCES `birth_records` (`id`),
  CONSTRAINT `FKnlwxwfy3c9bvhn1grki22v241` FOREIGN KEY (`genetics_id`) REFERENCES `genetics` (`id`),
  CONSTRAINT `FKpyays2120ru7onmmasl471moi` FOREIGN KEY (`mother_id`) REFERENCES `pigs` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=951084 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pigs`
--

LOCK TABLES `pigs` WRITE;
/*!40000 ALTER TABLE `pigs` DISABLE KEYS */;
INSERT INTO `pigs` VALUES (950001,'2024-07-24','HMB-SOW-001','2026-07-24 10:02:50.000000',176,'FEMALE',_binary '\0',NULL,'Show demo breeding sow for birth record and breeding workflow.','/uploads/pigs/92bf42e8-751c-4760-9efb-fed545c19658.jpg','2026-07-24',NULL,NULL,'BREEDING_SOW',NULL,909001,5,NULL),(950002,'2024-07-14','HMB-SOW-002','2026-07-24 10:02:50.000000',177,'FEMALE',_binary '\0',NULL,'Show demo breeding sow for birth record and breeding workflow.','/uploads/pigs/5c92bb6e-c754-4a0e-a52a-6390d86b8219.jpg','2026-07-24',NULL,NULL,'BREEDING_SOW',NULL,909001,5,NULL),(950003,'2024-07-04','HMB-SOW-003','2026-07-24 10:02:50.000000',178,'FEMALE',_binary '\0',NULL,'Show demo breeding sow for birth record and breeding workflow.','/uploads/pigs/a88a1700-ec0d-4b41-b997-86aead155d44.jpg','2026-07-24',NULL,NULL,'BREEDING_SOW',NULL,909001,5,NULL),(950004,'2024-06-24','HMB-SOW-004','2026-07-24 10:02:50.000000',179,'FEMALE',_binary '\0',NULL,'Show demo breeding sow for birth record and breeding workflow.\r\n\r\nBirth Record:\r\nDate: 2026-07-24\r\nAlive Piglets: 10\r\nDead Piglets: 3\r\nRecorded By: Ko Nyi Nyi\r\n\r\nBreeding Record:\r\nDate: 2026-07-24\r\nFather Genetics: Yorkshire [Y]\r\nRecorded By: Ko Nyi Nyi','/uploads/pigs/cc6e1517-0f3e-495d-bc52-b33fd0aa2dbd.jpg','2026-07-24',NULL,NULL,'BREEDING_SOW',NULL,909001,5,NULL),(950005,'2024-09-02','HMB-BOAR-001','2026-07-24 10:02:50.000000',240,'MALE',_binary '\0',NULL,'Show demo breeding boar for semen order workflow.','/uploads/pigs/647026c1-53ac-4949-9032-3ce943dfc68d.jpg','2026-07-24',600000,NULL,'BREEDING_BOAR',NULL,909001,2,NULL),(950006,'2026-07-23','HMB-PIGLET-001','2026-07-24 10:02:50.000000',7.2,'FEMALE',_binary '\0',NULL,'Show demo pig record for staff, supervisor and manager testing.','/uploads/pigs/b2818b9f-68be-4a0c-ba89-52505dce5fd1.jpg','2026-07-24',NULL,NULL,'PIGLET',955001,909001,5,950001),(950007,'2026-06-25','HMB-PIGLET-002','2026-07-24 10:02:50.000000',21,'MALE',_binary '\0',NULL,'Tomorrow vaccination preview piglet: becomes day 30 tomorrow.','/uploads/pigs/30bb881d-7317-4aa5-bf25-f0bf23ca7e0d.jpg','2026-07-24',NULL,NULL,'PIGLET',955001,909001,5,950001),(950008,'2026-06-24','HMB-PIGLET-003','2026-07-24 10:02:50.000000',22,'FEMALE',_binary '\0',NULL,'Show demo pig record for staff, supervisor and manager testing.','/uploads/pigs/aa6901ce-f22a-4497-9a34-9d8b48336b6b.jpg','2026-07-24',NULL,NULL,'PIGLET',955001,909001,5,950001),(950009,'2026-06-27','HMB-PIGLET-004','2026-07-24 10:02:50.000000',22,'MALE',_binary '\0',NULL,'Show demo pig record for staff, supervisor and manager testing.','/uploads/pigs/96050428-694e-4e9b-89d5-aadb5000a7d4.jpg','2026-07-24',NULL,NULL,'PIGLET',955001,909001,5,950001),(950010,'2026-06-09','HMB-PIGLET-005','2026-07-24 10:02:50.000000',11.2,'FEMALE',_binary '\0',NULL,'Show demo pig record for staff, supervisor and manager testing.','/uploads/pigs/307c4ab1-db7f-42fa-8734-b3b3a0c72c6f.jpg','2026-07-24',NULL,NULL,'PIGLET',955003,909001,5,950001),(950011,'2026-05-14','HMB-GROWER-001','2026-07-24 10:02:50.000000',51,'MALE',_binary '\0',NULL,'Show demo pig record for staff, supervisor and manager testing.','/uploads/pigs/78727292-56b2-46e2-b4cf-b41222a1c2b5.jpg','2026-07-24',0,NULL,'GROWER',NULL,909001,901005,NULL),(950012,'2026-05-13','HMB-GROWER-002','2026-07-24 10:02:50.000000',52,'FEMALE',_binary '\0',NULL,'Show demo pig record for staff, supervisor and manager testing.','/uploads/pigs/113e0b9a-f129-423d-ad7d-4c271ced5895.jpg','2026-07-24',0,NULL,'GROWER',NULL,909001,901005,NULL),(950013,'2026-05-12','HMB-GROWER-003','2026-07-24 10:02:50.000000',53,'MALE',_binary '\0',NULL,'Show demo pig record for staff, supervisor and manager testing.','/uploads/pigs/5d5f3d2c-329d-41b9-9bdb-9f80ffbcac66.jpg','2026-07-24',0,NULL,'GROWER',NULL,909001,901005,NULL),(950014,'2026-05-11','HMB-GROWER-004','2026-07-24 10:02:50.000000',54,'FEMALE',_binary '\0',NULL,'Show demo pig record for staff, supervisor and manager testing.','/uploads/pigs/0c7dd30a-ebac-4a4e-ab5b-f8d545086c35.jpg','2026-07-24',0,NULL,'GROWER',NULL,909001,901005,NULL),(950015,'2026-05-10','HMB-GROWER-005','2026-07-24 10:02:50.000000',50,'MALE',_binary '','2026-07-24','Show demo pig record for staff, supervisor and manager testing.\n\nReady for Sell Request:\nWeight: 50.0 kg\nPhysical Condition: EXCELLENT','/uploads/pigs/a71ef2ca-d8c8-4ba4-9b94-ea5f37f40224.jpg','2026-07-24',1000000,NULL,'FOR_SALE',NULL,909001,901005,NULL),(950016,'2026-02-13','HMB-FINISHER-001','2026-07-24 10:02:50.000000',104,'FEMALE',_binary '\0',NULL,'Show demo pig record for staff, supervisor and manager testing.','/uploads/pigs/7bcf1f81-ae67-4354-9d9b-59ac1924d0b0.jpg','2026-07-24',0,NULL,'FINISHER',NULL,909001,2,NULL),(950017,'2026-02-12','HMB-FINISHER-002','2026-07-24 10:02:50.000000',95,'MALE',_binary '','2026-07-24','Show demo pig record for staff, supervisor and manager testing.\r\n\r\nReady for Sell Request:\r\nWeight: 95.0 kg\r\nPhysical Condition: EXCELLENT','/uploads/pigs/eb32f411-7001-4274-92c3-c59083f32c83.jpg','2026-07-24',2000000,NULL,'FOR_SALE',NULL,909001,2,NULL),(950018,'2026-02-11','HMB-FINISHER-003','2026-07-24 10:02:50.000000',95,'FEMALE',_binary '','2026-07-24','Show demo pig record for staff, supervisor and manager testing.\n\nReady for Sell Request:\nWeight: 95.0 kg\nPhysical Condition: EXCELLENT','/uploads/pigs/93ddab2d-bbe9-4290-a37f-d468a0fcaedc.jpg','2026-07-24',500000,NULL,'FOR_SALE',NULL,909001,2,NULL),(950019,'2026-02-10','HMB-PENDING-SALE-001','2026-07-24 10:02:50.000000',107,'MALE',_binary '','2026-07-24','Waiting for manager/supervisor sale approval demo. | FINAL SHOW: waiting for sale approval.','/uploads/pigs/24b77b58-e0a5-4966-aea1-5419553366ea.jpg','2026-07-24',535000,NULL,'FOR_SALE',NULL,909001,2,NULL),(950020,'2026-02-09','HMB-FOR-SALE-001','2026-07-24 10:02:50.000000',108,'FEMALE',_binary '','2026-07-24','Already approved for customer marketplace demo. | FINAL SHOW: marketplace approved pig.','/uploads/pigs/5ee244c6-61c4-4ea1-b7bd-dfdae69529fa.jpg','2026-07-24',545000,NULL,'FOR_SALE',NULL,909001,2,NULL),(950101,'2026-05-19','HMB-GROWER-B-001','2026-07-24 10:02:50.000000',37.1,'FEMALE',_binary '\0',NULL,'Hmawbi grower house demo pig.','/uploads/pigs/ba93f380-9952-44a8-8cfa-285a1edead9d.jpg','2026-07-24',0,NULL,'GROWER',NULL,909002,901005,NULL),(950102,'2026-05-18','HMB-GROWER-B-002','2026-07-24 10:02:50.000000',39.2,'MALE',_binary '\0',NULL,'Hmawbi grower house demo pig.','/uploads/pigs/eeb16c60-e9c0-4387-9288-e2b58956c7c0.jpg','2026-07-24',0,NULL,'GROWER',NULL,909002,2,NULL),(950103,'2026-05-17','HMB-GROWER-B-003','2026-07-24 10:02:50.000000',41.3,'FEMALE',_binary '\0',NULL,'Hmawbi grower house demo pig.','/uploads/pigs/b8e8c421-dd36-4e92-b1a5-3bfabc735d09.jpg','2026-07-24',0,NULL,'GROWER',NULL,909002,901005,NULL),(950104,'2026-05-16','HMB-GROWER-B-004','2026-07-24 10:02:50.000000',43.4,'MALE',_binary '\0',NULL,'Hmawbi grower house demo pig.','/uploads/pigs/1fb6d5ce-0e33-48ea-9596-62977fe09e07.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909002,2,NULL),(950105,'2026-05-15','HMB-GROWER-B-005','2026-07-24 10:02:50.000000',45.5,'FEMALE',_binary '\0',NULL,'Hmawbi grower house demo pig.','/uploads/pigs/019556cc-9e8c-4490-b882-2a12c13a122e.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909002,901005,NULL),(950106,'2026-05-14','HMB-GROWER-B-006','2026-07-24 10:02:50.000000',47.6,'MALE',_binary '\0',NULL,'Hmawbi grower house demo pig.','/uploads/pigs/6b196cb7-fca4-4425-9798-2438a34793e2.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909002,2,NULL),(950107,'2026-05-13','HMB-GROWER-B-007','2026-07-24 10:02:50.000000',49.7,'FEMALE',_binary '\0',NULL,'Hmawbi grower house demo pig.','/uploads/pigs/eb380e6d-5021-4fe4-b8c2-2116f1ed33df.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909002,901005,NULL),(950108,'2026-05-12','HMB-GROWER-B-008','2026-07-24 10:02:50.000000',51.8,'MALE',_binary '\0',NULL,'Hmawbi grower house demo pig.','/uploads/pigs/3905ad53-d088-4515-8ebf-6ef8948f510e.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909002,2,NULL),(950109,'2026-02-20','HMB-FINISHER-B-001','2026-07-24 10:02:50.000000',95.3,'FEMALE',_binary '','2026-07-24','Hmawbi grower house demo pig.','/uploads/pigs/9cb95075-caba-49c0-a1da-7e714e562987.jpg','2026-07-24',500000,NULL,'FOR_SALE',NULL,909002,901005,NULL),(950110,'2026-02-19','HMB-FINISHER-B-002','2026-07-24 10:02:50.000000',97,'MALE',_binary '\0',NULL,'Hmawbi grower house demo pig.','/uploads/pigs/bea524a3-7da7-4683-95f8-71076746091d.jpg','2026-07-24',0,NULL,'FINISHER',NULL,909002,2,NULL),(950111,'2026-02-18','HMB-FINISHER-B-003','2026-07-24 10:02:50.000000',98.7,'FEMALE',_binary '\0',NULL,'Hmawbi grower house demo pig.','/uploads/pigs/8cabc06c-dc9a-4ce5-9880-7154a9dbe359.jpg','2026-07-24',0,NULL,'FINISHER',NULL,909002,901005,NULL),(950112,'2026-02-17','HMB-FINISHER-B-004','2026-07-24 10:02:50.000000',100.4,'MALE',_binary '\0',NULL,'Hmawbi grower house demo pig.','/uploads/pigs/3de1defe-9300-425c-a3f3-e99b2b4ff825.jpg','2026-07-24',0,NULL,'FINISHER',NULL,909002,2,NULL),(950113,'2026-02-16','HMB-FINISHER-B-005','2026-07-24 10:02:50.000000',102.1,'FEMALE',_binary '\0',NULL,'Hmawbi grower house demo pig.','/uploads/pigs/032c6ed5-75b7-439d-915c-6618f3f98e7f.jpg','2026-07-24',0,NULL,'FINISHER',NULL,909002,901005,NULL),(950114,'2026-02-15','HMB-FINISHER-B-006','2026-07-24 10:02:50.000000',103.8,'MALE',_binary '\0',NULL,'Hmawbi grower house demo pig.','/uploads/pigs/35ff1550-f047-424e-b850-105e6cbb561c.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909002,2,NULL),(950115,'2026-02-14','HMB-FINISHER-B-007','2026-07-24 10:02:50.000000',105.5,'FEMALE',_binary '\0',NULL,'Hmawbi grower house demo pig. | FINAL SHOW: sold history demo.','/images/pigs/market-hmawbi.jpg','2026-07-24',NULL,'2026-07-23','SOLD',NULL,909002,901005,NULL),(950116,'2026-02-13','HMB-GROWER-PENDING-SALE-001','2026-07-24 10:02:50.000000',107.2,'MALE',_binary '','2026-07-24','Pending sale approval pig for manager/supervisor testing. | FINAL SHOW: waiting for sale approval.','/uploads/pigs/263223b3-4ed1-4a17-ace9-4c69b1655c2a.jpg','2026-07-24',535000,NULL,'FOR_SALE',NULL,909002,2,NULL),(950117,'2026-02-12','HMB-GROWER-FOR-SALE-001','2026-07-24 10:02:50.000000',108.9,'FEMALE',_binary '','2026-07-24','Approved marketplace pig for customer order testing. | FINAL SHOW: marketplace approved pig.','/uploads/pigs/573b2404-ba6f-42ae-b3d6-3f6083673c21.jpg','2026-07-24',555000,NULL,'FOR_SALE',NULL,909002,901005,NULL),(950118,'2026-02-11','HMB-READY-SELL-001','2026-07-24 10:02:50.000000',110.6,'MALE',_binary '\0',NULL,'Ready for supervisor Ready To Sell button testing. | FINAL SHOW: supervisor can click Ready To Sell.','/uploads/pigs/ad3dfd25-f279-42ca-ac6d-9d3af94c016f.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909002,2,NULL),(950119,'2026-02-10','HMB-READY-SELL-002','2026-07-24 10:02:50.000000',97,'FEMALE',_binary '','2026-07-24','Ready for supervisor Ready To Sell button testing. | FINAL SHOW: supervisor can click Ready To Sell.\r\n\r\nReady for Sell Request:\r\nWeight: 97.0 kg\r\nPhysical Condition: EXCELLENT','/uploads/pigs/f616b3a6-cd6e-4af5-a691-7d71b5049995.jpg','2026-07-24',1800000,NULL,'FOR_SALE',NULL,909002,901005,NULL),(950120,'2026-02-09','HMB-MARKET-WEIGHT-001','2026-07-24 10:02:50.000000',98,'MALE',_binary '\0','2026-07-24','Ready for supervisor Ready To Sell button testing. | FINAL SHOW: supervisor can click Ready To Sell.\r\n\r\nReady for Sell Request:\r\nWeight: 98.0 kg\r\nPhysical Condition: EXCELLENT','/uploads/pigs/474f16e0-4d17-48ff-a806-6992a5cb5edb.jpg','2026-07-24',2000000,'2026-07-25','SOLD',NULL,909002,2,NULL),(950201,'2024-07-14','BGO-SOW-001','2026-07-24 10:02:50.000000',171,'FEMALE',_binary '\0',NULL,'Bago show demo breeding sow for birth record workflow.','/uploads/pigs/46ed1c42-6105-41cd-8ba0-2fede55e8b21.jpg','2026-07-24',NULL,NULL,'BREEDING_SOW',NULL,909003,5,NULL),(950202,'2024-07-04','BGO-SOW-002','2026-07-24 10:02:50.000000',172,'FEMALE',_binary '\0',NULL,'Bago show demo breeding sow for birth record workflow.','/uploads/pigs/abf0e3cf-aae0-4954-ba8b-bdf393d7f306.jpg','2026-07-24',NULL,NULL,'BREEDING_SOW',NULL,909003,5,NULL),(950203,'2024-06-24','BGO-SOW-003','2026-07-24 10:02:50.000000',173,'FEMALE',_binary '\0',NULL,'Bago show demo breeding sow for birth record workflow.','/uploads/pigs/73d0d239-29e8-4d3b-8b95-e309f34511a0.jpg','2026-07-24',NULL,NULL,'BREEDING_SOW',NULL,909003,5,NULL),(950204,'2024-06-14','BGO-SOW-004','2026-07-24 10:02:50.000000',174,'FEMALE',_binary '\0',NULL,'Bago show demo breeding sow for birth record workflow.','/uploads/pigs/b4e56435-1ea3-4cb2-8fcb-26b547a3783d.jpg','2026-07-24',NULL,NULL,'BREEDING_SOW',NULL,909003,5,NULL),(950205,'2024-08-23','BGO-BOAR-001','2026-07-24 10:02:50.000000',245,'MALE',_binary '\0',NULL,'Bago show demo boar for semen order workflow.','/uploads/pigs/05190372-55e0-4378-9083-e63012862e8b.jpg','2026-07-24',NULL,NULL,'BREEDING_BOAR',NULL,909003,2,NULL),(950206,'2026-07-23','BGO-PIGLET-001','2026-07-24 10:02:50.000000',20,'FEMALE',_binary '\0','2026-07-24','Bago show demo pig record.\n\nReady for Sell Request:\nWeight: 20.0 kg\nPhysical Condition: EXCELLENT','/uploads/pigs/ffa5ebb0-5a7f-4218-81dc-8dd6ef24d36c.webp','2026-07-24',254000,'2026-07-25','SOLD',955002,909003,3,950201),(950207,'2026-06-27','BGO-PIGLET-002','2026-07-24 10:02:50.000000',8.1,'MALE',_binary '\0',NULL,'Tomorrow vaccination preview piglet: becomes day 28 tomorrow.','/uploads/pigs/857fa64e-3b4f-491d-8118-1091580d9b7f.jpg','2026-07-24',NULL,NULL,'PIGLET',955002,909003,3,950201),(950208,'2026-06-26','BGO-PIGLET-003','2026-07-24 10:02:50.000000',20,'FEMALE',_binary '\0',NULL,'Bago show demo pig record.','/uploads/pigs/ac2e7017-9cd5-4be4-999c-0f726f5cac02.jpg','2026-07-24',NULL,NULL,'PIGLET',955002,909003,3,950201),(950209,'2026-06-25','BGO-PIGLET-004','2026-07-24 10:02:50.000000',22,'MALE',_binary '\0',NULL,'Bago show demo pig record.','/uploads/pigs/9d06e9c2-0e2d-43d2-b946-41cf2f80eb17.jpg','2026-07-24',NULL,NULL,'PIGLET',955002,909003,3,950201),(950210,'2026-06-12','BGO-PIGLET-005','2026-07-24 10:02:50.000000',40,'FEMALE',_binary '','2026-07-24','Bago show demo pig record.\n\nReady for Sell Request:\nWeight: 40.0 kg\nPhysical Condition: EXCELLENT','/uploads/pigs/06d17499-c65a-4b62-96ca-c932c5355beb.jpg','2026-07-24',389000,NULL,'FOR_SALE',955004,909003,3,950201),(950211,'2026-05-12','BGO-GROWER-001','2026-07-24 10:02:50.000000',49,'MALE',_binary '\0',NULL,'Bago show demo pig record.','/uploads/pigs/a7d4e1ba-7244-48d6-8a08-fef90325c012.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909003,901005,NULL),(950212,'2026-05-11','BGO-GROWER-002','2026-07-24 10:02:50.000000',50,'FEMALE',_binary '\0',NULL,'Bago show demo pig record.','/uploads/pigs/32f5d094-e8a7-4e7a-b6f6-5754d90b7070.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909003,901005,NULL),(950213,'2026-05-10','BGO-GROWER-003','2026-07-24 10:02:50.000000',51,'MALE',_binary '\0',NULL,'Bago show demo pig record.','/uploads/pigs/a64035d0-c6c6-4c4e-bf94-a99a41d3c5f2.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909003,901005,NULL),(950214,'2026-05-09','BGO-GROWER-004','2026-07-24 10:02:50.000000',52,'FEMALE',_binary '\0',NULL,'Bago show demo pig record.','/uploads/pigs/8e740f8b-c2dc-4086-80c5-b702687fe813.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909003,901005,NULL),(950215,'2026-05-08','BGO-GROWER-005','2026-07-24 10:02:50.000000',50,'MALE',_binary '','2026-07-24','Bago show demo pig record.\n\nReady for Sell Request:\nWeight: 50.0 kg\nPhysical Condition: EXCELLENT','/uploads/pigs/d76ddf15-3f8d-4915-82a5-263ce41acc40.jpg','2026-07-24',3560000,NULL,'FOR_SALE',NULL,909003,901005,NULL),(950216,'2026-02-16','BGO-FINISHER-001','2026-07-24 10:02:50.000000',102,'FEMALE',_binary '\0',NULL,'Bago show demo pig record.','/uploads/pigs/96c61537-3862-4704-aef6-d395d95a0535.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909003,2,NULL),(950217,'2026-02-15','BGO-FINISHER-002','2026-07-24 10:02:50.000000',103,'MALE',_binary '\0',NULL,'Bago show demo pig record.','/uploads/pigs/ce82f46f-2bab-4cf7-bc46-787a6083e0a2.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909003,2,NULL),(950218,'2026-02-14','BGO-FINISHER-003','2026-07-24 10:02:50.000000',104,'FEMALE',_binary '\0',NULL,'Bago show demo pig record.','/uploads/pigs/98a5628c-fa75-41f6-999c-2c587c9f42a4.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909003,2,NULL),(950219,'2026-02-13','BGO-PENDING-SALE-001','2026-07-24 10:02:50.000000',105,'MALE',_binary '','2026-07-24','Waiting for sale approval demo. | FINAL SHOW: waiting for sale approval.','/uploads/pigs/ce380d4e-b95f-40bf-b7f9-c7791c29661c.jpg','2026-07-24',535000,NULL,'FOR_SALE',NULL,909003,2,NULL),(950220,'2026-02-12','BGO-FOR-SALE-001','2026-07-24 10:02:50.000000',106,'FEMALE',_binary '\0','2026-07-24','Already approved for customer marketplace demo. | FINAL SHOW: marketplace approved pig.','/uploads/pigs/fb16ae21-3487-4da0-b775-9114aa7f2e96.jpg','2026-07-24',560000,'2026-07-25','SOLD',NULL,909003,2,NULL),(950301,'2026-05-16','BGO-GROWER-B-001','2026-07-24 10:02:50.000000',38.2,'FEMALE',_binary '\0',NULL,'Bago grower house demo pig.','/uploads/pigs/15b4794d-1df6-4666-9a23-b36bbfc4f232.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909004,901005,NULL),(950302,'2026-05-15','BGO-GROWER-B-002','2026-07-24 10:02:50.000000',40.4,'MALE',_binary '\0',NULL,'Bago grower house demo pig.','/uploads/pigs/18e23f47-3956-4e92-bbce-7af198272de9.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909004,2,NULL),(950303,'2026-05-14','BGO-GROWER-B-003','2026-07-24 10:02:50.000000',42.6,'FEMALE',_binary '\0',NULL,'Bago grower house demo pig.','/uploads/pigs/62bca084-0b4f-4cb2-8bde-da0e2bca3beb.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909004,901005,NULL),(950304,'2026-05-13','BGO-GROWER-B-004','2026-07-24 10:02:50.000000',67,'MALE',_binary '\0','2026-07-24','Bago grower house demo pig.\n\nReady for Sell Request:\nWeight: 67.0 kg\nPhysical Condition: EXCELLENT','/uploads/pigs/d6cf6f0e-6623-4c5a-a84a-d5a2790aba2b.jpg','2026-07-24',780000,'2026-07-24','SOLD',NULL,909004,2,NULL),(950305,'2026-05-12','BGO-GROWER-B-005','2026-07-24 10:02:50.000000',47,'FEMALE',_binary '\0',NULL,'Bago grower house demo pig.','/uploads/pigs/495fcc61-40d8-4ed3-bedd-e9146a5aec0f.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909004,901005,NULL),(950306,'2026-05-11','BGO-GROWER-B-006','2026-07-24 10:02:50.000000',49.2,'MALE',_binary '\0',NULL,'Bago grower house demo pig.','/uploads/pigs/df476053-76e0-4c38-8d3a-4cd0292eaedb.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909004,2,NULL),(950307,'2026-05-10','BGO-GROWER-B-007','2026-07-24 10:02:50.000000',55,'FEMALE',_binary '','2026-07-24','Bago grower house demo pig.\n\nReady for Sell Request:\nWeight: 55.0 kg\nPhysical Condition: EXCELLENT','/uploads/pigs/a6522f7c-d03b-4f7e-b72e-78645eef399a.jpg','2026-07-24',896000,NULL,'FOR_SALE',NULL,909004,901005,NULL),(950308,'2026-05-09','BGO-GROWER-B-008','2026-07-24 10:02:50.000000',53.6,'MALE',_binary '\0',NULL,'Bago grower house demo pig.','/uploads/pigs/78fbed82-29b8-4158-b18e-3d355e130cfd.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909004,2,NULL),(950309,'2026-02-17','BGO-FINISHER-B-001','2026-07-24 10:02:50.000000',98.2,'FEMALE',_binary '\0',NULL,'Bago grower house demo pig.','/uploads/pigs/413f02ac-ab05-4d8e-a6c7-c9e2c60bb2e1.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909004,901005,NULL),(950310,'2026-02-16','BGO-FINISHER-B-002','2026-07-24 10:02:50.000000',100,'MALE',_binary '\0',NULL,'Bago grower house demo pig.','/uploads/pigs/a4267259-73a4-4d5c-be54-793254d5f62a.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909004,2,NULL),(950311,'2026-02-15','BGO-FINISHER-B-003','2026-07-24 10:02:50.000000',99,'FEMALE',_binary '\0','2026-07-24','Bago grower house demo pig.\n\nReady for Sell Request:\nWeight: 99.0 kg\nPhysical Condition: EXCELLENT','/uploads/pigs/0f988cfe-2837-421a-8a02-edeee90c1f05.jpg','2026-07-24',1600000,'2026-07-25','SOLD',NULL,909004,901005,NULL),(950312,'2026-02-14','BGO-FINISHER-B-004','2026-07-24 10:02:50.000000',103.6,'MALE',_binary '\0',NULL,'Bago grower house demo pig.','/uploads/pigs/f8b72c32-a9b5-4bbe-9984-efd97e8b3b24.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909004,2,NULL),(950313,'2026-02-13','BGO-FINISHER-B-005','2026-07-24 10:02:50.000000',105.4,'FEMALE',_binary '\0',NULL,'Bago grower house demo pig.','/uploads/pigs/599752a9-9ec9-4b46-8746-aa18c20d56df.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909004,901005,NULL),(950314,'2026-02-12','BGO-FINISHER-B-006','2026-07-24 10:02:50.000000',107.2,'MALE',_binary '\0',NULL,'Bago grower house demo pig.','/uploads/pigs/bee6bb05-51f1-43b3-bb68-5fa7c595a840.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909004,2,NULL),(950315,'2026-02-11','BGO-FINISHER-B-007','2026-07-24 10:02:50.000000',109,'FEMALE',_binary '\0',NULL,'Bago grower house demo pig. | FINAL SHOW: sold history demo.','/uploads/pigs/0a527bab-07c0-407a-b98d-87fa98946031.jpg','2026-07-24',NULL,'2026-07-23','SOLD',NULL,909004,901005,NULL),(950316,'2026-02-10','BGO-GROWER-PENDING-SALE-001','2026-07-24 10:02:50.000000',110.8,'MALE',_binary '','2026-07-24','Pending sale approval pig for manager/supervisor testing. | FINAL SHOW: waiting for sale approval.','/uploads/pigs/6b6771b0-af79-47a3-b81a-7f350311f604.jpg','2026-07-24',535000,NULL,'FOR_SALE',NULL,909004,2,NULL),(950317,'2026-02-09','BGO-GROWER-FOR-SALE-001','2026-07-24 10:02:50.000000',112.6,'FEMALE',_binary '\0','2026-07-24','Approved marketplace pig for customer order testing. | FINAL SHOW: marketplace approved pig.','/uploads/pigs/f8a98dec-2ba6-4245-a7ff-f8e72737f079.jpg','2026-07-24',565000,'2026-07-25','SOLD',NULL,909004,901005,NULL),(950318,'2026-02-08','BGO-READY-SELL-001','2026-07-24 10:02:50.000000',114.4,'MALE',_binary '\0',NULL,'Ready for supervisor Ready To Sell button testing. | FINAL SHOW: supervisor can click Ready To Sell.','/uploads/pigs/bbcc6435-0b49-4214-a661-e96863ffc6aa.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909004,2,NULL),(950319,'2026-02-07','BGO-READY-SELL-002','2026-07-24 10:02:50.000000',116.2,'FEMALE',_binary '\0',NULL,'Ready for supervisor Ready To Sell button testing. | FINAL SHOW: supervisor can click Ready To Sell.','/uploads/pigs/d7c45e69-4b76-4716-94ed-20c7e7aae770.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909004,901005,NULL),(950320,'2026-02-06','BGO-MARKET-WEIGHT-001','2026-07-24 10:02:50.000000',118,'MALE',_binary '\0',NULL,'Ready for supervisor Ready To Sell button testing. | FINAL SHOW: supervisor can click Ready To Sell.','/uploads/pigs/3ae471d3-d6b1-4a19-88d7-a370c41f7684.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909004,2,NULL),(951000,'2025-01-24','BGV-BA-001','2026-07-24 07:24:38.876471',180,'MALE',_binary '\0',NULL,'Yorkshire pigs are calm, intelligent, social, and highly adaptable. They are excellent mothers with strong maternal instincts, good feed efficiency, and perform best in a clean, low-stress environment','/uploads/pigs/0e04f134-a7dd-4642-979c-e6da74be21e0.jpg','2026-07-24',NULL,NULL,'BREEDING_BOAR',NULL,909003,1,NULL),(951001,'2024-07-23','BGV-BB-001','2026-07-24 07:30:15.915972',200,'MALE',_binary '\0',NULL,'Landrace pigs are calm, social, intelligent, and active animals. They are excellent mothers with strong maternal instincts, high feed efficiency, and perform best in a low-stress, well-managed environment','/uploads/pigs/b3aed2a2-55a8-471f-b49b-26e24ae067bf.jpg','2026-07-24',NULL,NULL,'BREEDING_BOAR',NULL,909004,3,NULL),(951002,'2025-05-14','BGV-BA-002','2026-07-24 07:33:07.987171',160,'MALE',_binary '\0',NULL,'Pietrain pigs are active, alert, muscular, and efficient feeders. They are known for producing lean meat but can be more stress-sensitive than other breeds, requiring calm handling and a well-managed environment','/uploads/pigs/412954c0-d4b7-4dda-b8de-f78f7487f25e.jpg','2026-07-24',NULL,NULL,'BREEDING_BOAR',NULL,909003,4,NULL),(951003,'2026-07-24','HMHBY042407202601','2026-07-24 08:34:09.035768',19,'FEMALE',_binary '\0',NULL,'Born from HMB-SOW-004','/uploads/pigs/07de8356-f66e-4e35-a20b-c7de496aeda5.jpg','2026-07-24',NULL,NULL,'PIGLET',956000,909001,1,950004),(951004,'2026-07-24','HMHBY042407202602','2026-07-24 08:34:09.038885',21,'MALE',_binary '\0',NULL,'Born from HMB-SOW-004','/uploads/pigs/0c7822e1-7648-4f21-8463-7f28bdb749b8.jpg','2026-07-24',NULL,NULL,'PIGLET',956000,909001,1,950004),(951005,'2026-07-24','HMHBY042407202603','2026-07-24 08:34:09.044399',22,'FEMALE',_binary '\0',NULL,'Born from HMB-SOW-004','/uploads/pigs/63803e42-589c-4172-9f78-44cd9476bbc1.webp','2026-07-24',NULL,NULL,'PIGLET',956000,909001,1,950004),(951006,'2026-07-24','HMHBY042407202604','2026-07-24 08:34:09.044399',20,'MALE',_binary '\0',NULL,'Born from HMB-SOW-004','/uploads/pigs/725f352a-558e-4bca-85bf-3e7edd809eda.jpg','2026-07-24',NULL,NULL,'PIGLET',956000,909001,1,950004),(951007,'2026-07-24','HMHBY042407202605','2026-07-24 08:34:09.044399',20,'FEMALE',_binary '\0',NULL,'Born from HMB-SOW-004','/uploads/pigs/6cc67743-b343-438d-bfc6-e9137703f98d.jpg','2026-07-24',NULL,NULL,'PIGLET',956000,909001,1,950004),(951008,'2026-07-24','HMHBY042407202606','2026-07-24 08:34:09.060810',22,'MALE',_binary '\0',NULL,'Born from HMB-SOW-004','/uploads/pigs/31aa6697-58be-47e5-b44f-8b6729b1416b.jpg','2026-07-24',NULL,NULL,'PIGLET',956000,909001,1,950004),(951009,'2026-07-24','HMHBY042407202607','2026-07-24 08:34:09.060810',22,'FEMALE',_binary '\0',NULL,'Born from HMB-SOW-004','/uploads/pigs/b94db2d5-2eb2-4bbe-90f4-d920b3285033.jpg','2026-07-24',NULL,NULL,'PIGLET',956000,909001,1,950004),(951010,'2026-07-24','HMHBY042407202608','2026-07-24 08:34:09.071823',15,'MALE',_binary '','2026-07-25','Born from HMB-SOW-004\n\nReady for Sell Request:\nWeight: 15.0 kg\nPhysical Condition: EXCELLENT','/uploads/pigs/af201b50-d1ef-4f95-b84f-45b8074825f4.jpg','2026-07-24',200000,NULL,'FOR_SALE',956000,909001,1,950004),(951011,'2026-07-24','HMHBY042407202609','2026-07-24 08:34:09.075869',22,'FEMALE',_binary '\0',NULL,'Born from HMB-SOW-004','/uploads/pigs/85baf66f-0c58-4b65-9641-fcbe3041de72.jpg','2026-07-24',NULL,NULL,'PIGLET',956000,909001,1,950004),(951012,'2026-07-24','HMHBY042407202610','2026-07-24 08:34:09.075869',20,'MALE',_binary '\0',NULL,'Born from HMB-SOW-004','/uploads/pigs/da3e1f4b-dfc5-4ef2-9b5d-3e61e60d5d85.jpg','2026-07-24',NULL,NULL,'PIGLET',956000,909001,1,950004),(951013,'2026-07-08','HMB-BA-003','2026-07-24 14:46:46.013957',45,'FEMALE',_binary '\0',NULL,'Breeding house demo','/uploads/pigs/2d18cc55-cb8d-4aef-b1d9-ddc64bcb1551.jpg','2026-07-24',NULL,NULL,'PIGLET',NULL,909001,1,NULL),(951014,'2026-01-07','HMB-BA-004','2026-07-24 14:51:34.510389',86.5,'FEMALE',_binary '\0',NULL,'Breeding house demo','/uploads/pigs/3fb911e6-f0b4-4294-92f3-a764d0b4035e.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909001,4,NULL),(951015,'2025-06-17','HMB-BA-005','2026-07-24 14:52:47.041910',175.5,'FEMALE',_binary '\0',NULL,'Hmawbi breeding House demo','/uploads/pigs/dfa715eb-1125-4d99-95a9-6777071e3797.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909001,4,NULL),(951016,'2024-09-15','HMB-BA-006','2026-07-24 14:56:05.773312',198,'FEMALE',_binary '\0',NULL,'Female, 198 kg , breeding-sow','/uploads/pigs/8841e428-dee6-4f52-b9e5-611149d918c7.jpg','2026-07-24',NULL,NULL,'BREEDING_SOW',NULL,909001,1,NULL),(951017,'2025-09-15','HMB-BA-007','2026-07-24 14:57:04.352988',178,'FEMALE',_binary '\0','2026-07-25','Ready for Sell Request:\nWeight: 178.0 kg\nPhysical Condition: EXCELLENT','/uploads/pigs/ab9386c7-fbc4-4535-a614-e2b0dafffa97.jpg','2026-07-24',1230000,'2026-07-25','SOLD',NULL,909001,6,NULL),(951018,'2026-07-14','HMB-BA-008','2026-07-24 15:01:29.880516',30,'FEMALE',_binary '\0',NULL,'Hmawbi Breeding House , piglet demo','/uploads/pigs/2fec2796-c43d-461a-9064-c6be219500b4.jpg','2026-07-24',NULL,NULL,'PIGLET',NULL,909001,1,NULL),(951019,'2025-02-02','HMB-BA-009','2026-07-24 15:02:29.551709',180,'FEMALE',_binary '\0',NULL,'Ready for selling , Hmawbi Breeding','/uploads/pigs/d20dff4c-c50e-4ca7-ad52-347da2280f16.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909001,3,NULL),(951020,'2026-06-24','HMB-BA-010','2026-07-24 15:03:51.475193',45,'MALE',_binary '','2026-07-25','Ready for Sell Request:\nWeight: 45.0 kg\nPhysical Condition: EXCELLENT','/uploads/pigs/eb6ea0d7-047b-409c-a37d-d0b98d620915.jpg','2026-07-24',300000,NULL,'FOR_SALE',NULL,909001,4,NULL),(951021,'2024-03-12','HBA-BA-011','2026-07-24 15:04:49.083051',200,'FEMALE',_binary '\0',NULL,'Breeding-boar Duroc, 200kg ','/uploads/pigs/69fd3384-2a4b-40ac-aa9d-1526a591105d.jpg','2026-07-24',NULL,NULL,'BREEDING_BOAR',NULL,909001,7,NULL),(951022,'2026-01-04','HMB-BA-011','2026-07-24 15:05:47.648984',78,'FEMALE',_binary '\0',NULL,'Grower , 78kg  , pietrain demo','/uploads/pigs/1c4da5e3-dfa5-4b1b-9d70-f8c09bd0cf19.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909001,4,NULL),(951023,'2025-11-23','HMB-BB-001','2026-07-24 15:12:17.966128',176,'FEMALE',_binary '\0',NULL,'Yorkshire, female demo','/uploads/pigs/a92571c2-40e0-4511-a016-159cb424b2ce.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909002,1,NULL),(951024,'2024-06-19','HMB-BB-002','2026-07-24 15:13:07.626074',201,'FEMALE',_binary '\0',NULL,'Hmawbi Grower , Landrace demo','/uploads/pigs/158d8e4f-aebb-463c-814e-d8a9b78a9945.jpg','2026-07-24',NULL,NULL,'BREEDING_SOW',NULL,909002,3,NULL),(951025,'2025-12-24','HMB-BB-003','2026-07-24 15:13:54.742646',165,'FEMALE',_binary '\0',NULL,'Landrace and Yorkshire, female and finisher','/uploads/pigs/7d031aa1-69dd-4a73-aa1d-0218796d7ab5.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909002,5,NULL),(951026,'2026-01-12','HMB-BB-004','2026-07-24 15:16:24.658954',78,'FEMALE',_binary '\0',NULL,'Grower Yorkshire, female','/uploads/pigs/572178ae-2abf-4cd1-8bf2-755991d4a66a.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909002,1,NULL),(951027,'2025-08-28','HMB-BB-005','2026-07-24 15:17:30.986788',145,'FEMALE',_binary '\0',NULL,'Duroc , finisher, 145 kg ','/uploads/pigs/01581f1b-c5a7-4106-ae18-46b961961a5d.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909002,2,NULL),(951028,'2024-07-16','HMB-BB-006','2026-07-24 15:21:06.220112',210,'MALE',_binary '\0',NULL,'Hmawbi Grower , Landrace and Yorkshire','/uploads/pigs/b2c42db8-c802-4cae-b804-f0bf1f14b553.jpg','2026-07-24',NULL,NULL,'BREEDING_BOAR',NULL,909002,6,NULL),(951029,'2026-07-01','HMB-BB-007','2026-07-24 15:22:15.036289',43,'FEMALE',_binary '\0',NULL,'female , 43 kg and piglet','/uploads/pigs/b8986b58-1425-4e18-b9f9-0d0aa47e395e.jpg','2026-07-24',NULL,NULL,'PIGLET',NULL,909002,2,NULL),(951030,'2026-07-01','HMB-BB-008','2026-07-24 15:23:30.886424',35,'FEMALE',_binary '\0',NULL,'Hmawbi Grower, piglet','/uploads/pigs/ee402baf-39b9-4a59-bbf9-648a911473fc.jpg','2026-07-24',NULL,NULL,'PIGLET',NULL,909002,1,NULL),(951031,'2026-01-11','HMB-BB-009','2026-07-24 15:28:46.788120',78,'FEMALE',_binary '\0',NULL,'Ready for selling','/uploads/pigs/06938d94-df96-4e65-be88-eb0d96bf5f34.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909002,3,NULL),(951032,'2025-08-27','HMB-BB-010','2026-07-24 15:29:34.754589',190,'FEMALE',_binary '\0',NULL,'190kg and finisher, female','/uploads/pigs/14b67897-322b-410a-9dce-0abf686b106b.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909002,6,NULL),(951033,'2024-03-06','HMB-BB-011','2026-07-24 15:31:09.350979',220,'MALE',_binary '\0',NULL,'Yorkshire, breeding-boar','/uploads/pigs/b3ac429a-f4e5-443a-b167-f67453c15446.jpg','2026-07-24',NULL,NULL,'BREEDING_BOAR',NULL,909002,1,NULL),(951034,'2025-12-12','HMB-BB-012','2026-07-24 15:34:44.099404',130,'FEMALE',_binary '\0',NULL,'130kg for selling, finisher','/uploads/pigs/2d875819-0b29-40c1-90e3-9e8afd5d61be.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909002,3,NULL),(951035,'2026-06-29','HMB-BB-013','2026-07-24 15:35:54.360425',45,'FEMALE',_binary '\0',NULL,'26 days and 45 kg , yorkshire','/uploads/pigs/1595c3e2-e176-4c3f-94a5-064364872b12.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909002,1,NULL),(951036,'2025-05-05','HMB-BB-014','2026-07-24 15:42:40.560589',190,'MALE',_binary '\0',NULL,'Finisher pietrain genetics','/uploads/pigs/7159798b-26f0-46ec-bd74-35c8645a9b82.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909002,4,NULL),(951037,'2026-07-01','HMB-BB-015','2026-07-24 15:43:24.627264',38,'FEMALE',_binary '\0',NULL,'For selling , genetics of Yorkshire and Landrace','/uploads/pigs/d3b4bcc1-947c-4b9b-b44e-c88fab238119.jpg','2026-07-24',NULL,NULL,'PIGLET',NULL,909002,5,NULL),(951038,'2024-06-11','HMB-BB-016','2026-07-24 15:45:30.175262',220,'FEMALE',_binary '\0',NULL,'Duroc , breeding-sow genetics','/uploads/pigs/adef1c59-700e-4e5f-997c-cedbd9da1c7d.jpg','2026-07-24',NULL,NULL,'BREEDING_SOW',NULL,909002,2,NULL),(951039,'2026-03-24','HMB-BB-017','2026-07-24 15:46:27.693236',78,'FEMALE',_binary '\0',NULL,'Pietrain grower','/uploads/pigs/7d2da2e8-3f23-4abb-85f9-2df1489fde3d.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909002,4,NULL),(951040,'2025-09-13','HMB-BB-018','2026-07-24 15:54:57.660837',110,'FEMALE',_binary '\0',NULL,'Landrace and Yorkshire genetics','/uploads/pigs/7a40598c-7871-4456-bcf4-822536e5219e.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909002,6,NULL),(951041,'2026-07-12','HMB-BB-019','2026-07-24 15:55:48.206263',32,'FEMALE',_binary '\0',NULL,'Duroc piglet with 32 kg and 13days.','/uploads/pigs/c7d9c727-05a0-4225-b30a-3d24da0e9423.jpg','2026-07-24',NULL,NULL,'PIGLET',NULL,909002,2,NULL),(951042,'2025-11-04','HMB-BB-020','2026-07-24 15:56:38.963971',156,'FEMALE',_binary '','2026-07-25','Ready for Sell Request:\nWeight: 156.0 kg\nPhysical Condition: EXCELLENT','/uploads/pigs/2f81003d-d47a-43bd-aff6-f90602925485.jpg','2026-07-24',1245000,NULL,'FOR_SALE',NULL,909002,4,NULL),(951043,'2026-07-01','BGO-BA-001','2026-07-24 16:16:30.810401',29,'FEMALE',_binary '\0',NULL,'Ready for selling','/uploads/pigs/efc8be33-4c73-4c27-8a4f-6aaae651ffa6.jpg','2026-07-24',NULL,NULL,'PIGLET',NULL,909003,1,NULL),(951044,'2026-01-25','BGO-BA-002','2026-07-24 16:17:25.532700',70,'FEMALE',_binary '\0',NULL,'grower duroc','/uploads/pigs/229af715-b25b-4d00-bbc6-e2c78a8ff64c.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909003,2,NULL),(951045,'2025-05-01','BGO-BA-003','2026-07-24 16:18:21.519185',130,'MALE',_binary '\0',NULL,'male, finisher, landrace','/uploads/pigs/c54b8003-5c4c-4771-81c9-942da19da98a.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909003,3,NULL),(951046,'2025-08-13','BGO-BA-004','2026-07-24 16:20:42.094209',168,'FEMALE',_binary '\0',NULL,'finisher, pietrain','/uploads/pigs/77988a64-aa4a-435c-88ab-6c978d6dba56.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909003,4,NULL),(951047,'2026-07-04','BGO-BA-005','2026-07-24 16:22:59.993012',28,'FEMALE',_binary '\0',NULL,'small piglet, landrace','/uploads/pigs/f00a6c71-afbe-4456-a2a5-0b7c6693a9d4.jpg','2026-07-24',NULL,NULL,'PIGLET',NULL,909003,3,NULL),(951048,'2026-02-11','BGO-BA-006','2026-07-24 16:24:13.190481',89,'FEMALE',_binary '\0',NULL,'grower, female with 89kg','/uploads/pigs/0a30be2e-ad10-4a9b-b6aa-cb95a98a93db.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909003,6,NULL),(951049,'2025-11-24','BGO-BA-007','2026-07-24 16:26:12.876300',100,'MALE',_binary '\0',NULL,'finisher, male with 100kg','/uploads/pigs/1f70c41a-ec96-4fef-8abc-85d5d71ce035.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909003,1,NULL),(951050,'2026-01-26','BGO-BA-008','2026-07-24 16:27:54.737666',78,'FEMALE',_binary '\0',NULL,'Bago breeding demo','/uploads/pigs/e6f44a28-c964-40b8-ad37-3a17ad474310.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909003,5,NULL),(951051,'2025-01-13','BGO-BB-001','2026-07-24 16:33:27.745119',110,'FEMALE',_binary '\0',NULL,'Yorkshire , finisher, female','/uploads/pigs/27840837-ae94-4567-98f2-2232b874f710.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909004,1,NULL),(951052,'2026-07-01','BGO-BB-002','2026-07-24 16:34:05.317974',23,'FEMALE',_binary '\0',NULL,'Duroc, piglet, female','/uploads/pigs/052e282b-aacb-4bb9-8525-abfa91fffb1d.jpg','2026-07-24',NULL,NULL,'PIGLET',NULL,909004,2,NULL),(951053,'2024-03-25','BGO-BB-003','2026-07-24 16:38:03.781069',220,'FEMALE',_binary '\0',NULL,'breeding-sow , female','/uploads/pigs/b7b51093-72bb-4230-873d-4b0fad328c06.jpg','2026-07-24',NULL,NULL,'BREEDING_SOW',NULL,909004,3,NULL),(951054,'2025-09-22','BGO-BB-004','2026-07-24 16:38:55.613181',190,'FEMALE',_binary '\0',NULL,'Bago house demo','/uploads/pigs/eea86a9e-0d6f-4799-be59-5cb41273cdae.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909004,4,NULL),(951055,'2026-02-22','BGO-BB-005','2026-07-24 16:40:18.908001',88,'MALE',_binary '\0',NULL,'Bago grower house demo','/uploads/pigs/54a5b7af-c8f8-42d8-b863-19602d517ea4.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909004,5,NULL),(951056,'2026-07-02','BGO-BB-006','2026-07-24 16:41:07.063261',30,'FEMALE',_binary '\0',NULL,'Bago grower house demo','/uploads/pigs/eb12f848-79ee-41e3-825a-541156280f0b.jpg','2026-07-24',NULL,NULL,'PIGLET',NULL,909004,3,NULL),(951057,'2024-08-27','BGO-BB-007','2026-07-24 16:41:54.879777',220,'FEMALE',_binary '\0',NULL,'breeding-sow , female','/uploads/pigs/6fb8f3bd-f39d-4135-a45a-5f37448969d9.jpg','2026-07-24',NULL,NULL,'BREEDING_SOW',NULL,909004,901005,NULL),(951058,'2025-05-24','BGO-BB-008','2026-07-24 16:44:02.156355',130,'FEMALE',_binary '\0',NULL,'finisher, Yorkshire with 130kg','/uploads/pigs/2dddf07d-efc1-4a91-b287-4e623003d19f.jpg','2026-07-24',NULL,NULL,'FINISHER',NULL,909004,1,NULL),(951059,'2026-01-11','BGO-BB-009','2026-07-24 16:45:11.694226',97,'FEMALE',_binary '\0',NULL,'grower , female','/uploads/pigs/a5963684-4764-4e72-92c1-29ad1ce7ae81.jpg','2026-07-24',NULL,NULL,'GROWER',NULL,909004,6,NULL),(951060,'2025-01-06','MD-BA-001','2026-07-25 09:54:11.442851',199,'FEMALE',_binary '\0',NULL,'Mandalay breeding demo','/uploads/pigs/d81963b1-7c38-43c4-b764-801df2aada4b.jpg','2026-07-25',NULL,NULL,'FINISHER',NULL,910000,1,NULL),(951061,'2026-02-12','MD-BA-002','2026-07-25 09:55:50.698126',78,'MALE',_binary '\0',NULL,'Duroc, grower, male','/uploads/pigs/67a6aac7-bc00-4a97-82a8-d3422543ff55.jpg','2026-07-25',NULL,NULL,'GROWER',NULL,910000,2,NULL),(951062,'2025-05-14','MD-BA-003','2026-07-25 13:49:51.642156',189,'FEMALE',_binary '','2026-07-25','Landrace, finisher\n\nReady for Sell Request:\nWeight: 189.0 kg\nPhysical Condition: EXCELLENT','/uploads/pigs/fae9ceea-4d82-4312-938d-8c1ebd3709fb.jpg','2026-07-25',1750000,NULL,'FOR_SALE',NULL,910000,3,NULL),(951063,'2024-08-06','MD-BA-004','2026-07-25 13:52:53.802026',220,'FEMALE',_binary '\0',NULL,'female, breeding-sow','/uploads/pigs/4801f68c-bc28-473e-a531-6fbabccc92c6.jpg','2026-07-25',NULL,NULL,'BREEDING_SOW',NULL,910000,6,NULL),(951064,'2026-06-10','MD-BA-005','2026-07-25 13:54:17.838159',60,'FEMALE',_binary '\0',NULL,'female, piglet','/uploads/pigs/d8dde152-8923-4f75-b844-4b2cb4ea6a86.jpg','2026-07-25',NULL,NULL,'PIGLET',NULL,910000,901005,NULL),(951065,'2025-08-08','MD-BA-006','2026-07-25 13:55:50.396697',180,'FEMALE',_binary '\0',NULL,'female, Mandalay breeding demo','/uploads/pigs/60e893f3-efe1-4cf9-826c-dc54b21817b8.jpg','2026-07-25',NULL,NULL,'FINISHER',NULL,910000,3,NULL),(951066,'2025-05-12','MD-BA-007','2026-07-25 13:57:04.321773',190,'MALE',_binary '','2026-07-25','pietrain, finisher with 199kg\n\nReady for Sell Request:\nWeight: 190.0 kg\nPhysical Condition: EXCELLENT','/uploads/pigs/5dd19c86-e7b1-471a-8d8e-9e448cbe8e73.jpg','2026-07-25',1980000,NULL,'FOR_SALE',NULL,910000,4,NULL),(951067,'2024-09-04','MD-BA-008','2026-07-25 13:58:53.977017',230,'FEMALE',_binary '\0',NULL,'landrace, female with 230kg','/uploads/pigs/fc2c32d8-a807-49e9-ad11-eca34719f4f2.jpg','2026-07-25',NULL,NULL,'BREEDING_SOW',NULL,910000,3,NULL),(951068,'2026-06-01','MD-BA-009','2026-07-25 14:00:48.326986',35,'FEMALE',_binary '\0',NULL,'piglet, Yorkshire, female','/uploads/pigs/5f4a583f-f546-4683-99c9-57ef924ed89e.jpg','2026-07-25',NULL,NULL,'PIGLET',NULL,910000,1,NULL),(951069,'2025-05-13','MD-BA-010','2026-07-25 14:02:40.839180',210,'MALE',_binary '\0',NULL,'finisher, Mandalay breeding demo','/uploads/pigs/309b7dff-ce62-4222-8675-507de7de1eaf.jpg','2026-07-25',NULL,NULL,'FINISHER',NULL,910000,6,NULL),(951070,'2024-05-11','MD-BA-011','2026-07-25 14:04:05.321705',220,'MALE',_binary '\0',NULL,'Ready for selling','/uploads/pigs/b1ca4de3-4163-4f7c-b6a9-9124998bdf26.jpg','2026-07-25',NULL,NULL,'BREEDING_BOAR',NULL,910000,3,NULL),(951071,'2024-06-13','MD-BA-012','2026-07-25 14:05:22.230465',200,'MALE',_binary '\0',NULL,'Ready for selling','/uploads/pigs/70c9c874-1418-4c6d-9ddf-93e58af17fe0.jpg','2026-07-25',NULL,NULL,'BREEDING_BOAR',NULL,910000,3,NULL),(951072,'2024-09-13','MD-BA-013','2026-07-25 14:07:59.631653',200,'MALE',_binary '','2026-07-25','Ready for selling , semen genetics\n\nReady for Sell Request:\nWeight: 200.0 kg\nPhysical Condition: EXCELLENT','/uploads/pigs/24c37331-0186-4a92-903f-9ec927ce7aeb.jpg','2026-07-25',1680000,NULL,'FOR_SALE',NULL,910000,2,NULL),(951073,'2025-07-08','TGI-BA-001','2026-07-25 15:19:38.329718',195,'FEMALE',_binary '\0',NULL,'female, finisher with 195kg','/uploads/pigs/0ddaaf8f-9d95-49cd-b18d-2b8fe8a433b2.jpg','2026-07-25',NULL,NULL,'FINISHER',NULL,910001,1,NULL),(951074,'2026-05-12','TGI-BA-002','2026-07-25 15:22:20.765659',30,'FEMALE',_binary '\0',NULL,'pietrain , female with 30kg','/uploads/pigs/c51c6e01-4bc5-47f9-8ccd-e53434d5d2e2.jpg','2026-07-25',NULL,NULL,'PIGLET',NULL,910001,4,NULL),(951075,'2024-05-13','TGI-BA-003','2026-07-25 15:25:23.778863',220,'MALE',_binary '\0',NULL,'breeding-boar, Taunggyi breeding demo','/uploads/pigs/83f8300d-f8ff-4ef3-bbc4-1909af0ea67d.jpg','2026-07-25',NULL,NULL,'BREEDING_BOAR',NULL,910001,6,NULL),(951076,'2025-05-20','TGI-BA-004','2026-07-25 15:29:08.798912',196,'MALE',_binary '\0',NULL,'finisher, male with 196kg','/uploads/pigs/56ab82ff-b246-47de-b672-4b5718439be9.jpg','2026-07-25',NULL,NULL,'FINISHER',NULL,910001,2,NULL),(951077,'2026-04-23','TGI-BA-005','2026-07-25 15:34:44.762014',75,'FEMALE',_binary '\0',NULL,'Taunggyi breeding demo\n\nReady for Sell Request:\nWeight: 75.0 kg\nPhysical Condition: EXCELLENT','/uploads/pigs/01649bc8-0b2a-49d3-b721-f91803c387dd.jpg','2026-07-25',879000,NULL,'PENDING_SALE_APPROVAL',NULL,910001,6,NULL),(951078,'2026-07-02','TGI-BA-006','2026-07-25 15:36:45.349584',26,'FEMALE',_binary '\0',NULL,'Landrace , piglet with 26 kg','/uploads/pigs/17365c81-5af9-41ca-bd9e-739eb940d81e.jpg','2026-07-25',NULL,NULL,'PIGLET',NULL,910001,3,NULL),(951079,'2025-05-05','TGI-BA-007','2026-07-25 15:38:23.975983',196,'FEMALE',_binary '\0',NULL,'Taunggyi breeding unit demo','/uploads/pigs/998f735b-9d87-4b5f-af73-ffb68132f59b.jpg','2026-07-25',NULL,NULL,'FINISHER',NULL,910001,4,NULL),(951080,'2024-05-12','TGI-BA-008','2026-07-25 15:40:41.157081',210,'MALE',_binary '\0',NULL,'Duroc and Landrace with 220kg\n\nReady for Sell Request:\nWeight: 210.0 kg\nPhysical Condition: EXCELLENT','/uploads/pigs/b5c4d2d8-e517-477d-a7ac-a894bf10fa59.jpg','2026-07-25',2100000,NULL,'PENDING_SALE_APPROVAL',NULL,910001,901005,NULL),(951081,'2026-07-04','TGI-BA-009','2026-07-25 15:42:22.060039',25,'FEMALE',_binary '\0',NULL,'female , Landrace with 25kg','/uploads/pigs/2db0067b-2e4e-4498-9c9f-7d496027980b.jpg','2026-07-25',NULL,NULL,'PIGLET',NULL,910001,3,NULL),(951082,'2025-05-18','TGI-BA-010','2026-07-25 15:43:58.410309',200,'FEMALE',_binary '\0',NULL,'ready for selling\n\nReady for Sell Request:\nWeight: 200.0 kg\nPhysical Condition: EXCELLENT','/uploads/pigs/4e65a716-d8da-4aab-aad3-8009170fffed.jpg','2026-07-25',2000000,NULL,'PENDING_SALE_APPROVAL',NULL,910001,901005,NULL),(951083,'2024-05-19','TGI-BA-011','2026-07-25 15:52:03.627823',205,'MALE',_binary '\0',NULL,'Taunggyi breeding unit A demo','/uploads/pigs/2fb19bdd-b1a9-4829-9dd6-330c34d016e1.jpg','2026-07-25',NULL,NULL,'BREEDING_BOAR',NULL,910001,1,NULL);
/*!40000 ALTER TABLE `pigs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rooms`
--

DROP TABLE IF EXISTS `rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rooms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `capacity` int DEFAULT NULL,
  `code` varchar(6) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `building_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKnlgv6b0wpvxer58vhq2b2e8q8` (`building_id`,`code`),
  CONSTRAINT `FKojgn0sxhkfxd7pmmojnem9r4q` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=914002 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rooms`
--

LOCK TABLES `rooms` WRITE;
/*!40000 ALTER TABLE `rooms` DISABLE KEYS */;
INSERT INTO `rooms` VALUES (913001,_binary '',40,'HR1','2026-07-24 10:02:50.000000','Gestating and lactating sows','Hmawbi Sow Pen 1',909001),(913002,_binary '',120,'HR2','2026-07-24 10:02:50.000000','Newborn and weaned piglets','Hmawbi Piglet Pen 1',909001),(913003,_binary '',160,'HR3','2026-07-24 10:02:50.000000','Grower pigs from 60 to 120 days','Hmawbi Grower Pen 1',909002),(913004,_binary '',160,'HR4','2026-07-24 10:02:50.000000','Finishers and sale approval pigs','Hmawbi Finisher Pen 1',909002),(913005,_binary '',45,'BR1','2026-07-24 10:02:50.000000','Breeding sows and boars','Bago Sow Pen 1',909003),(913006,_binary '',130,'BR2','2026-07-24 10:02:50.000000','Piglets and nursery pigs','Bago Piglet Pen 1',909003),(913007,_binary '',170,'BR3','2026-07-24 10:02:50.000000','Grower pigs','Bago Grower Pen 1',909004),(913008,_binary '',170,'BR4','2026-07-24 10:02:50.000000','Finishers and sale approval pigs','Bago Finisher Pen 1',909004),(914000,_binary '',NULL,'R00001','2026-07-25 06:25:02.057313',NULL,'Mandalay room A',910000),(914001,_binary '',NULL,'R00002','2026-07-25 06:30:22.973602',NULL,'Taunggyi room A',910001);
/*!40000 ALTER TABLE `rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rule_schedules`
--

DROP TABLE IF EXISTS `rule_schedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rule_schedules` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `administration_route` varchar(255) DEFAULT NULL,
  `applies_to` varchar(255) DEFAULT NULL,
  `breeding_month` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `day_from_birth` int DEFAULT NULL,
  `day_range_end` int DEFAULT NULL,
  `day_range_start` int DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `dosage` varchar(255) DEFAULT NULL,
  `feed_amount_kg` double DEFAULT NULL,
  `feed_type` varchar(255) DEFAULT NULL,
  `fixed_target_day` int DEFAULT NULL,
  `medication` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `rule_type` enum('BREEDING','FEEDING','GENERAL','MEDICATION','VACCINATION') NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `farm_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_rule_farm` (`farm_id`),
  KEY `FKkyhjf57orhhgsvpm5q8sdd8ig` (`created_by`),
  CONSTRAINT `FKkyhjf57orhhgsvpm5q8sdd8ig` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKm0mxrhpbvmmjo5icuydld2gvn` FOREIGN KEY (`farm_id`) REFERENCES `farms` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=921002 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rule_schedules`
--

LOCK TABLES `rule_schedules` WRITE;
/*!40000 ALTER TABLE `rule_schedules` DISABLE KEYS */;
INSERT INTO `rule_schedules` VALUES (920001,_binary '','ORAL','PIGLET',NULL,'2026-07-24 10:02:50.000000',1,NULL,NULL,'Newborn piglet iron support on day 1.','2 ml',NULL,NULL,1,'Iron Supplement','Hmawbi Day 1 Iron Supplement','MEDICATION',910003,908001),(920002,_binary '','INTRAMUSCULAR','PIGLET',NULL,'2026-07-24 10:02:50.000000',30,NULL,NULL,'Vaccination for piglets at 30 days old.','2 ml',NULL,NULL,30,'PRRS Vaccine','Hmawbi Day 30 PRRS Vaccination','VACCINATION',910003,908001),(920003,_binary '',NULL,'GROWER',NULL,'2026-07-24 10:02:50.000000',NULL,120,60,'Grower daily feed from day 60 to day 120.',NULL,2.5,'GROWER_FEED',NULL,NULL,'Hmawbi Grower Feed Program','FEEDING',910003,908001),(920004,_binary '',NULL,'FINISHER',NULL,'2026-07-24 10:02:50.000000',NULL,180,121,'Finisher daily feed from day 121 to day 180.',NULL,3.2,'FINISHER_FEED',NULL,NULL,'Hmawbi Finisher Feed Program','FEEDING',910003,908001),(920005,_binary '','ORAL','PIGLET',NULL,'2026-07-24 10:02:50.000000',1,NULL,NULL,'Newborn piglet iron support on day 1.','2 ml',NULL,NULL,1,'Iron Supplement','Bago Day 1 Iron Supplement','MEDICATION',910008,908002),(920006,_binary '','INTRAMUSCULAR','PIGLET',NULL,'2026-07-24 10:02:50.000000',28,NULL,NULL,'Vaccination for piglets at 28 days old.','2 ml',NULL,NULL,28,'Mycoplasma Vaccine','Bago Day 28 Mycoplasma Vaccination','VACCINATION',910008,908002),(920007,_binary '',NULL,'GROWER',NULL,'2026-07-24 10:02:50.000000',NULL,120,60,'Grower daily feed from day 60 to day 120.',NULL,2.6,'GROWER_FEED',NULL,NULL,'Bago Grower Feed Program','FEEDING',910008,908002),(920008,_binary '',NULL,'FINISHER',NULL,'2026-07-24 10:02:50.000000',NULL,180,121,'Finisher daily feed from day 121 to day 180.',NULL,3.3,'FINISHER_FEED',NULL,NULL,'Bago Finisher Feed Program','FEEDING',910008,908002),(921000,_binary '',NULL,'BREEDING_BOAR',NULL,'2026-07-24 07:26:08.204482',NULL,NULL,NULL,'Manager-defined semen price per dose (MMK)',NULL,80000,NULL,NULL,NULL,'SYSTEM_SEMEN_PRICE_PER_DOSE','GENERAL',910008,908002),(921001,_binary '',NULL,'BREEDING_BOAR',NULL,'2026-07-25 15:52:43.337873',NULL,NULL,NULL,'Manager-defined semen price per dose (MMK)',NULL,65000,NULL,NULL,NULL,'SYSTEM_SEMEN_PRICE_PER_DOSE','GENERAL',911001,909001);
/*!40000 ALTER TABLE `rule_schedules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `semen_orders`
--

DROP TABLE IF EXISTS `semen_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `semen_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `order_reference` varchar(255) DEFAULT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  `price_per_unit` double DEFAULT NULL,
  `qr_code_path` varchar(255) DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `status` enum('CANCELLED','DISPATCHED','PAID','PENDING','QR_GENERATED') NOT NULL,
  `total_amount` double DEFAULT NULL,
  `boar_id` bigint NOT NULL,
  `customer_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK7hhctqwogsxqkopllyww4ooc0` (`order_reference`),
  KEY `FKa8gwpu4c9h84yugcoinlbvsjg` (`boar_id`),
  KEY `FKhs55nqs77kbhlw33jb4ptiaen` (`customer_id`),
  CONSTRAINT `FKa8gwpu4c9h84yugcoinlbvsjg` FOREIGN KEY (`boar_id`) REFERENCES `pigs` (`id`),
  CONSTRAINT `FKhs55nqs77kbhlw33jb4ptiaen` FOREIGN KEY (`customer_id`) REFERENCES `customer_accounts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=993005 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `semen_orders`
--

LOCK TABLES `semen_orders` WRITE;
/*!40000 ALTER TABLE `semen_orders` DISABLE KEYS */;
INSERT INTO `semen_orders` VALUES (992001,'2026-07-24 10:02:50.000000','HMB-SEMEN-ORDER-001',NULL,60000,NULL,3,'PENDING',180000,950005,912001),(992002,'2026-07-24 10:02:50.000000','BGO-SEMEN-ORDER-001',NULL,65000,NULL,2,'PENDING',130000,950205,912001),(992003,'2026-07-23 10:02:50.000000','HMB-SEMEN-ORDER-PAID-001','2026-07-23 10:02:50.000000',60000,'/images/qr/demo-semen-paid.png',1,'PAID',60000,950005,912001),(993000,'2026-07-24 07:36:40.549331','SC-064F7B79','2026-07-24 07:37:10.444486',1000000,NULL,3,'PAID',3000000,951002,912001),(993001,'2026-07-24 07:42:18.259546','SC-18A905E8','2026-07-24 07:42:31.276585',1000000,NULL,1,'PAID',1000000,951001,912001),(993002,'2026-07-25 15:58:16.433965','SC-4CF82B92','2026-07-25 15:58:32.046263',1000000,NULL,3,'PAID',3000000,951000,913000),(993003,'2026-07-25 16:26:48.330614','SC-57538431',NULL,80000,NULL,1,'QR_GENERATED',80000,951000,913000),(993004,'2026-07-25 16:27:16.129238','SC-53EC645D','2026-07-25 16:27:30.778297',60000,NULL,6,'PAID',360000,950005,913000);
/*!40000 ALTER TABLE `semen_orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shifts`
--

DROP TABLE IF EXISTS `shifts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shifts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `grace_period_minutes` int NOT NULL,
  `minimum_required_hours` double NOT NULL,
  `name` varchar(255) NOT NULL,
  `standard_end` time(6) NOT NULL,
  `standard_start` time(6) NOT NULL,
  `farm_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKigf2mia8ckt04pt40xoe5sowb` (`farm_id`),
  CONSTRAINT `FKigf2mia8ckt04pt40xoe5sowb` FOREIGN KEY (`farm_id`) REFERENCES `farms` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=915000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shifts`
--

LOCK TABLES `shifts` WRITE;
/*!40000 ALTER TABLE `shifts` DISABLE KEYS */;
INSERT INTO `shifts` VALUES (914001,_binary '',15,8,'Hmawbi Day Shift','17:00:00.000000','08:00:00.000000',908001),(914002,_binary '',15,8,'Bago Day Shift','17:00:00.000000','08:00:00.000000',908002);
/*!40000 ALTER TABLE `shifts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `staff_shifts`
--

DROP TABLE IF EXISTS `staff_shifts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staff_shifts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `effective_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  `status` enum('ACTIVE','NIGHT_SHIFT_REST','REST') NOT NULL,
  `shift_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKm4ds2bu2ohbivsdat63rt5t93` (`shift_id`),
  KEY `FKbosqsjb7ikx5eeftsew2gube6` (`user_id`),
  CONSTRAINT `FKbosqsjb7ikx5eeftsew2gube6` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKm4ds2bu2ohbivsdat63rt5t93` FOREIGN KEY (`shift_id`) REFERENCES `shifts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=916000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `staff_shifts`
--

LOCK TABLES `staff_shifts` WRITE;
/*!40000 ALTER TABLE `staff_shifts` DISABLE KEYS */;
INSERT INTO `staff_shifts` VALUES (915001,'2026-07-24',NULL,'ACTIVE',914001,910005),(915002,'2026-07-24',NULL,'ACTIVE',914001,910007),(915003,'2026-07-24',NULL,'ACTIVE',914002,910010),(915004,'2026-07-24',NULL,'ACTIVE',914002,910012);
/*!40000 ALTER TABLE `staff_shifts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `must_change_password` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `password_reset_token` varchar(255) DEFAULT NULL,
  `password_reset_token_expiry` datetime(6) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `profile_image_path` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','CUSTOMER','HR','MANAGER','STAFF','SUPERVISOR') NOT NULL,
  `start_date` date DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `building_id` bigint DEFAULT NULL,
  `farm_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  KEY `idx_user_email` (`email`),
  KEY `idx_user_role` (`role`),
  KEY `FK713jg2mvw2hflfn7g401wh7ec` (`building_id`),
  KEY `FK278kystlk1xtpvgyfjsnhfwl3` (`farm_id`),
  CONSTRAINT `FK278kystlk1xtpvgyfjsnhfwl3` FOREIGN KEY (`farm_id`) REFERENCES `farms` (`id`),
  CONSTRAINT `FK713jg2mvw2hflfn7g401wh7ec` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=911006 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (910001,'2026-07-24 10:02:50.000000','admin@swinecore.mm',_binary '',_binary '\0','System Admin','$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',NULL,NULL,'09400100001','/images/logo.png','ADMIN','2026-07-24','2026-07-24 10:02:50.000000',NULL,NULL),(910002,'2026-07-24 10:02:50.000000','hr@swinecore.mm',_binary '',_binary '\0','HR Officer','$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',NULL,NULL,'09400100002','/images/logo.png','HR','2026-07-24','2026-07-24 10:02:50.000000',NULL,NULL),(910003,'2026-07-24 10:02:50.000000','manager.hmawbi@swinecore.mm',_binary '',_binary '\0','U Aung Kyaw','$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',NULL,NULL,'09420110003','/images/logo.png','MANAGER','2026-07-24','2026-07-24 10:02:50.000000',NULL,908001),(910004,'2026-07-24 10:02:50.000000','supervisor.hmawbi.breeding@swinecore.mm',_binary '',_binary '\0','Daw May Thu','$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',NULL,NULL,'09420110004','/images/logo.png','SUPERVISOR','2026-07-24','2026-07-24 10:02:50.000000',909001,908001),(910005,'2026-07-24 10:02:50.000000','staff.hmawbi.breeding@swinecore.mm',_binary '',_binary '\0','Ko Nyi Nyi','$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',NULL,NULL,'09420110005','/images/logo.png','STAFF','2026-07-24','2026-07-24 07:46:07.710899',909001,908001),(910006,'2026-07-24 10:02:50.000000','supervisor.hmawbi.grower@swinecore.mm',_binary '',_binary '\0','Daw Khin Hnin','$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',NULL,NULL,'09420110006','/images/logo.png','SUPERVISOR','2026-07-24','2026-07-24 10:02:50.000000',909002,908001),(910007,'2026-07-24 10:02:50.000000','staff.hmawbi.grower@swinecore.mm',_binary '',_binary '\0','Ko Zaw Lin','$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',NULL,NULL,'09420110007','/images/logo.png','STAFF','2026-07-24','2026-07-24 10:02:50.000000',909002,908001),(910008,'2026-07-24 10:02:50.000000','manager.bago@swinecore.mm',_binary '',_binary '\0','U Thet Paing','$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',NULL,NULL,'09430220008','/images/logo.png','MANAGER','2026-07-24','2026-07-24 10:02:50.000000',NULL,908002),(910009,'2026-07-24 10:02:50.000000','supervisor.bago.breeding@swinecore.mm',_binary '',_binary '\0','Daw Ei Mon','$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',NULL,NULL,'09430220009','/images/logo.png','SUPERVISOR','2026-07-24','2026-07-24 10:02:50.000000',909003,908002),(910010,'2026-07-24 10:02:50.000000','staff.bago.breeding@swinecore.mm',_binary '',_binary '\0','Ko Hein Htet','$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',NULL,NULL,'09430220010','/images/logo.png','STAFF','2026-07-24','2026-07-24 10:02:50.000000',909003,908002),(910011,'2026-07-24 10:02:50.000000','supervisor.bago.grower@swinecore.mm',_binary '',_binary '\0','Daw Su Mon','$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',NULL,NULL,'09430220011','/images/logo.png','SUPERVISOR','2026-07-24','2026-07-24 10:02:50.000000',909004,908002),(910012,'2026-07-24 10:02:50.000000','staff.bago.grower@swinecore.mm',_binary '',_binary '\0','Ko Nyi Nyi','$2a$12$gYBoxwF21wZ4HgdY.83k.ODZoIc6Yp6LKBtGoRHxQ4WhYNm0QQzkS',NULL,NULL,'09430220012','/images/logo.png','STAFF','2026-07-24','2026-07-24 10:02:50.000000',909004,908002),(911000,'2026-07-25 06:39:31.442204','manager.mandalay@swinecore.mm',_binary '',_binary '','Kyaw Thiha','$2a$12$BglNQvskUNNPWf6SEsZtKuTvh9Eo4CSypkCHGp4d8XsLjH5dhqCbm',NULL,NULL,'0987765543',NULL,'MANAGER','2026-07-25','2026-07-25 06:39:31.442204',NULL,909000),(911001,'2026-07-25 06:42:13.455401','manager.taunggyi@swinecore.mm',_binary '',_binary '','Wai Lin','$2a$12$zo54XUuqZycBbK/6gqbKiee0JlJOMkrfTWlBSrxt5HGWF8h0ZxDyW',NULL,NULL,'0987654321',NULL,'MANAGER','2026-07-25','2026-07-25 06:42:13.455401',NULL,909001),(911002,'2026-07-25 06:46:57.261257','supervisor.mandalay.breeding@swinecore.mm',_binary '',_binary '','Phoo Phoo','$2a$12$2HAWYvAbKyGI3wzWlGHMQOiDhCC1rtoT2pCG5t8U4FP.cQm.D8oB2',NULL,NULL,'0987654321',NULL,'SUPERVISOR','2026-07-25','2026-07-25 06:46:57.261257',910000,909000),(911003,'2026-07-25 06:50:47.219881','staff.mandalay.breeding@swinecore.mm',_binary '',_binary '','Ei Chaw','$2a$12$gL0B91G6x5ukIqGlcZdAxu4WgF3DzE6J8OSJosjZhhmydJyziE5tu',NULL,NULL,'0988776655',NULL,'STAFF','2026-07-25','2026-07-25 06:50:47.219881',910000,909000),(911004,'2026-07-25 06:52:57.904518','supervisor.taunggyi.breeding@swinecore.mm',_binary '',_binary '','Sai Laung Kham','$2a$12$qNHrBYT63Y8eLqvB/LpNHOzOtFtYVGj75Dssx4080wfaB9FrXBFmy',NULL,NULL,'0987654321',NULL,'SUPERVISOR','2026-07-25','2026-07-25 06:52:57.904518',910001,909001),(911005,'2026-07-25 06:53:59.211521','staff.taunggyi.breeding@swinecore.mm',_binary '',_binary '','Nang Shwe Sin','$2a$12$BvoJw8p5o0KDeofnP9fCc.byoYzvTGAaYqpvyaKwbaSe/wqjq6586',NULL,NULL,'0988765443',NULL,'STAFF','2026-07-25','2026-07-25 06:53:59.211521',910001,909001);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-25 23:21:45
