-- MySQL dump 10.13  Distrib 8.0.43, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: resto_roulette
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `account_id` int NOT NULL AUTO_INCREMENT,
  `login` varchar(100) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `user_info_id` int NOT NULL,
  `verification_token` varchar(16) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `login` (`login`),
  KEY `account_ibfk_1` (`user_info_id`),
  CONSTRAINT `account_ibfk_1` FOREIGN KEY (`user_info_id`) REFERENCES `user_info` (`user_info_id`)
) ENGINE=InnoDB AUTO_INCREMENT=232 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,'fan2resto','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',42,'2LH7DVU5','2025-10-07 08:02:44',0),(177,'chloe.richard','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',99,'A1B2C3D4E5F6G7H8','2025-03-19 08:32:00',1),(178,'hugo.durand','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',100,'W9E8R7T6Y5U4I3O2','2025-03-28 17:55:00',0),(179,'camille.dubois','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',101,'Q1W2E3R4T5Y6U7I8','2025-04-07 12:19:00',0),(180,'arthur.moreau','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',102,'Z8X7C6V5B4N3M2A1','2025-04-16 20:11:00',0),(181,'jade.laurent','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',103,'P5O4I3U2Y1T6R7E8','2025-04-22 09:42:00',0),(182,'louis.simon','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',104,'F3G2H1J9K8L7M6N5','2025-04-27 14:30:00',0),(183,'lina.michel','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',105,'B9C8D7E6F5G4H3I2','2025-05-03 11:15:00',0),(184,'ethan.lefebvre','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',106,'T6Y5U4I3O2P1A9S8','2025-05-09 08:56:00',0),(185,'anna.leroy','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',107,'L8K7J6H5G4F3D2S1','2025-05-15 10:33:00',0),(186,'adam.roux','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',108,'R9T8Y7U6I5O4P3Q2','2025-05-22 17:22:00',0),(187,'romane.david','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',109,'M2N3B4V5C6X7Z8A9','2025-05-28 19:44:00',0),(188,'nathan.bertrand','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',110,'C3V2B1N4M5Q6W7E8','2025-06-02 09:13:00',0),(189,'manon.morel','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',111,'X1Z2C3V4B5N6M7L8','2025-06-06 15:00:00',0),(190,'paul.fournier','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',112,'D4S5A6Q7W8E9R1T2','2025-06-12 10:47:00',0),(191,'zoe.girard','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',113,'U5I6O7P8A9S1D2F3','2025-06-17 13:05:00',0),(192,'tom.bonnet','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',114,'G9H8J7K6L5M4N3B2','2025-06-24 09:58:00',0),(193,'clara.dupuis','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',115,'A2B3C4D5E6F7G8H9','2025-07-02 11:35:00',0),(194,'leo.lambert','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',116,'P4O3I2U1Y6T5R8E9','2025-07-08 14:20:00',0),(195,'sarah.fontaine','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',117,'M9N8B7V6C5X4Z3A2','2025-07-14 16:09:00',0),(196,'enzo.chevalier','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',118,'R1T2Y3U4I5O6P7A8','2025-07-20 12:48:00',0),(197,'alice.renaud','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',119,'K5L6M7N8O9P1Q2R3','2025-07-26 18:41:00',0),(198,'noe.gautier','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',120,'E7R6T5Y4U3I2O1P9','2025-07-31 10:03:00',0),(199,'eva.lopez','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',121,'W8Q9A1Z2S3X4D5C6','2025-08-06 17:54:00',0),(200,'mael.henry','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',122,'T7R8E9W1Q2A3S4D5','2025-08-11 20:20:00',0),(201,'ines.masson','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',123,'Y5U6I7O8P9A1S2D3','2025-08-18 09:45:00',0),(202,'nolan.garnier','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',124,'F2G3H4J5K6L7M8N9','2025-08-25 11:27:00',0),(203,'louna.marchand','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',125,'A9B8C7D6E5F4G3H2','2025-09-01 08:15:00',0),(204,'eden.morin','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',126,'P2O3I4U5Y6T7R8E9','2025-09-06 13:35:00',0),(205,'clemence.brun','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',127,'N3M4B5V6C7X8Z9A1','2025-09-10 10:00:00',0),(206,'theo.blanc','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',128,'E1R2T3Y4U5I6O7P8','2025-09-13 09:30:00',0),(207,'ambre.guerin','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',129,'K7L8M9N1B2V3C4X5','2025-09-17 18:50:00',0),(208,'mathis.boyer','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',130,'D5S4A3Q2W1E6R7T8','2025-09-22 12:22:00',0),(209,'romy.gallet','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',131,'J1K2L3M4N5O6P7Q8','2025-09-26 08:33:00',0),(210,'eliott.perrot','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',132,'W9E8R7T6Y5U4I3O2','2025-09-28 19:59:00',0),(211,'mila.benoit','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',133,'C1V2B3N4M5A6S7D8','2025-09-30 11:10:00',0),(212,'noemie.meyer','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',134,'T8Y7U6I5O4P3A2S1','2025-10-01 15:27:00',0),(213,'aaron.lemoine','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',135,'Z2X3C4V5B6N7M8L9','2025-10-02 09:18:00',0),(214,'jeanne.faure','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',136,'G8H7J6K5L4M3N2B1','2025-10-03 13:40:00',0),(215,'axel.barbier','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',137,'A9S8D7F6G5H4J3K2','2025-10-04 10:12:00',0),(216,'lucie.dupuy','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',138,'L1K2J3H4G5F6D7S8','2025-10-04 11:48:00',0),(217,'julien.renard','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',139,'E9R8T7Y6U5I4O3P2','2025-10-05 08:33:00',0),(218,'maya.huet','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',140,'M3N2B1V4C5X6Z7A8','2025-10-05 14:27:00',0),(219,'gaspard.carpentier','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',141,'R6T7Y8U9I1O2P3Q4','2025-10-06 09:59:00',0),(220,'rose.blanchard','$2a$10$btmaFnhdMp5esoKUB8xV5.OpY/YhUqweo6P82JUEuTLjY.U2FcCwG',142,'H3G2F1E4D5C6B7A8','2025-10-06 21:15:00',0),(221,'fan2restoa','$2a$10$1yngrhU1YAqLtkOK0vsgsuR5nv8tERyeEgGA6uTCf/qGuo/EkWX06',143,'EDLTSACH','2025-10-07 12:19:42',0),(224,'Rosalia73','$2a$10$jSMDRPRvhL5JoUNa8y0m2e7hTt5YI8LMMQDEeADcIDzQwPzeyR2D2',146,'4OM3PJUS','2025-10-20 08:28:04',0),(228,'restoroulette.app@gmail.com',NULL,153,'HS5EL7AU','2025-10-20 08:59:00',0),(229,'fan2zazazaresto','$2a$10$hde3BDQaqQ.40P0QocEuQee9dcpli9xnOM0MdbBG45y1g94vzxdU6',154,'C6QNWQ46','2025-10-21 07:44:06',0),(230,'arthur','$2a$10$7x3WEMPDhnlmG86/Ut/A9uKR5ILwn15f4pYKYzR2TOrK7TNXLC5HC',155,'6LS9167E','2025-10-21 07:49:18',0),(231,'fazazan2resto','$2a$10$W8CTh1RGznkhDbXm17oSVerGftmAxUoZlEJpGX/yFqmFx/1UiZlY2',156,'JLB7QWT1','2025-10-31 16:05:48',0);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `business_hour`
--

DROP TABLE IF EXISTS `business_hour`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `business_hour` (
  `resto_id` int NOT NULL,
  `opening_hour` time NOT NULL,
  `closing_hour` time NOT NULL,
  `week_day` int NOT NULL DEFAULT '1',
  KEY `business_hour_resto_info_resto_id_fk` (`resto_id`),
  CONSTRAINT `business_hour_resto_info_resto_id_fk` FOREIGN KEY (`resto_id`) REFERENCES `resto_info` (`resto_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `business_hour`
--

LOCK TABLES `business_hour` WRITE;
/*!40000 ALTER TABLE `business_hour` DISABLE KEYS */;
INSERT INTO `business_hour` VALUES (17,'12:00:00','14:00:00',1),(17,'12:00:00','14:00:00',2),(17,'12:00:00','14:00:00',3),(17,'12:00:00','14:00:00',4),(17,'12:00:00','14:00:00',5),(17,'12:30:00','15:16:00',6),(17,'19:45:00','23:00:00',6);
/*!40000 ALTER TABLE `business_hour` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `text` text NOT NULL,
  `account_id` int NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`comment_id`),
  KEY `account_id` (`account_id`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `follower`
--

DROP TABLE IF EXISTS `follower`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `follower` (
  `ask_account_id` int NOT NULL,
  `asked_account_id` int NOT NULL,
  `accepted_date` date DEFAULT (now()),
  PRIMARY KEY (`ask_account_id`,`asked_account_id`),
  KEY `asked_account_id` (`asked_account_id`),
  CONSTRAINT `follower_ibfk_1` FOREIGN KEY (`ask_account_id`) REFERENCES `account` (`account_id`),
  CONSTRAINT `follower_ibfk_2` FOREIGN KEY (`asked_account_id`) REFERENCES `account` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follower`
--

LOCK TABLES `follower` WRITE;
/*!40000 ALTER TABLE `follower` DISABLE KEYS */;
/*!40000 ALTER TABLE `follower` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `interaction`
--

DROP TABLE IF EXISTS `interaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `interaction` (
  `interaction_id` int NOT NULL AUTO_INCREMENT,
  `resto_id` int NOT NULL,
  `account_id` int NOT NULL,
  `has_liked` tinyint(1) DEFAULT '0',
  `comment_id` int DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`interaction_id`),
  KEY `resto_id` (`resto_id`),
  KEY `account_id` (`account_id`),
  KEY `comment_id` (`comment_id`),
  CONSTRAINT `interaction_ibfk_1` FOREIGN KEY (`resto_id`) REFERENCES `resto` (`resto_id`),
  CONSTRAINT `interaction_ibfk_2` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`),
  CONSTRAINT `interaction_ibfk_3` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `interaction`
--

LOCK TABLES `interaction` WRITE;
/*!40000 ALTER TABLE `interaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `interaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `meal`
--

DROP TABLE IF EXISTS `meal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `meal` (
  `meal_id` int NOT NULL AUTO_INCREMENT,
  `resto_id` int NOT NULL,
  `name` varchar(150) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`meal_id`),
  KEY `resto_id` (`resto_id`),
  CONSTRAINT `meal_ibfk_1` FOREIGN KEY (`resto_id`) REFERENCES `resto_info` (`resto_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `meal`
--

LOCK TABLES `meal` WRITE;
/*!40000 ALTER TABLE `meal` DISABLE KEYS */;
/*!40000 ALTER TABLE `meal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resto`
--

DROP TABLE IF EXISTS `resto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resto` (
  `resto_id` int NOT NULL AUTO_INCREMENT,
  `display_name` varchar(150) NOT NULL,
  `owner_id` int DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`resto_id`),
  KEY `account_id` (`owner_id`),
  CONSTRAINT `resto_ibfk_1` FOREIGN KEY (`owner_id`) REFERENCES `account` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resto`
--

LOCK TABLES `resto` WRITE;
/*!40000 ALTER TABLE `resto` DISABLE KEYS */;
INSERT INTO `resto` VALUES (17,'Burger king',1,'2025-11-03 00:00:00'),(19,'Fraud-cdo',230,'2025-11-05 08:21:58'),(28,'Ka-éfssé',1,'2025-11-05 00:00:00'),(29,'tata',177,'2025-11-07 00:00:00');
/*!40000 ALTER TABLE `resto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resto_info`
--

DROP TABLE IF EXISTS `resto_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resto_info` (
  `resto_id` int NOT NULL,
  `name` varchar(150) NOT NULL,
  `address` varchar(255) NOT NULL,
  `lon` decimal(10,7) DEFAULT NULL,
  `lat` decimal(10,7) DEFAULT NULL,
  PRIMARY KEY (`resto_id`),
  CONSTRAINT `resto_info_ibfk_1` FOREIGN KEY (`resto_id`) REFERENCES `resto` (`resto_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resto_info`
--

LOCK TABLES `resto_info` WRITE;
/*!40000 ALTER TABLE `resto_info` DISABLE KEYS */;
INSERT INTO `resto_info` VALUES (17,'Burger king','1 avenue du Roi du burger',0.0000000,0.0000000),(19,'Fraud-cdo','17 rue de la miserable daube',0.0000000,0.0000000),(28,'Kanteucky frit chkipoulet','12 rue de philou le coquinou',0.1000000,0.1000000),(29,'tata','tata',0.4000000,0.3000000);
/*!40000 ALTER TABLE `resto_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resto_label`
--

DROP TABLE IF EXISTS `resto_label`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resto_label` (
  `label_id` int NOT NULL AUTO_INCREMENT,
  `label_name` varchar(255) NOT NULL,
  PRIMARY KEY (`label_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resto_label`
--

LOCK TABLES `resto_label` WRITE;
/*!40000 ALTER TABLE `resto_label` DISABLE KEYS */;
INSERT INTO `resto_label` VALUES (1,'végé'),(2,'végan'),(3,'halal'),(4,'fait maison'),(5,'Fastfood certifié bien degeu'),(6,'Gluten free');
/*!40000 ALTER TABLE `resto_label` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resto_resto_labels`
--

DROP TABLE IF EXISTS `resto_resto_labels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resto_resto_labels` (
  `resto_id` int NOT NULL,
  `label_id` int NOT NULL,
  PRIMARY KEY (`label_id`,`resto_id`),
  KEY `resto_resto_labels_resto_resto_id_fk` (`resto_id`),
  CONSTRAINT `resto_resto_labels_label_label_id_fk` FOREIGN KEY (`label_id`) REFERENCES `resto_label` (`label_id`),
  CONSTRAINT `resto_resto_labels_resto_resto_id_fk` FOREIGN KEY (`resto_id`) REFERENCES `resto` (`resto_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resto_resto_labels`
--

LOCK TABLES `resto_resto_labels` WRITE;
/*!40000 ALTER TABLE `resto_resto_labels` DISABLE KEYS */;
INSERT INTO `resto_resto_labels` VALUES (17,4),(19,1),(19,2);
/*!40000 ALTER TABLE `resto_resto_labels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resto_resto_types`
--

DROP TABLE IF EXISTS `resto_resto_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resto_resto_types` (
  `resto_id` int DEFAULT NULL,
  `type_id` int DEFAULT NULL,
  KEY `resto_resto_types_resto_resto_id_fk` (`resto_id`),
  KEY `resto_resto_types_resto_type_id_fk` (`type_id`),
  CONSTRAINT `resto_resto_types_resto_resto_id_fk` FOREIGN KEY (`resto_id`) REFERENCES `resto` (`resto_id`) ON DELETE CASCADE,
  CONSTRAINT `resto_resto_types_resto_type_id_fk` FOREIGN KEY (`type_id`) REFERENCES `resto_type` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resto_resto_types`
--

LOCK TABLES `resto_resto_types` WRITE;
/*!40000 ALTER TABLE `resto_resto_types` DISABLE KEYS */;
INSERT INTO `resto_resto_types` VALUES (17,1),(19,1),(28,8),(29,8);
/*!40000 ALTER TABLE `resto_resto_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resto_type`
--

DROP TABLE IF EXISTS `resto_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resto_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `food_type` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resto_type`
--

LOCK TABLES `resto_type` WRITE;
/*!40000 ALTER TABLE `resto_type` DISABLE KEYS */;
INSERT INTO `resto_type` VALUES (1,'BURGER'),(2,'INDIAN'),(7,'PIZZA'),(8,'AMERICAN');
/*!40000 ALTER TABLE `resto_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stat_wheel`
--

DROP TABLE IF EXISTS `stat_wheel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stat_wheel` (
  `id` int NOT NULL AUTO_INCREMENT,
  `launched_by` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `launched_by_fk` (`launched_by`),
  CONSTRAINT `launched_by_fk` FOREIGN KEY (`launched_by`) REFERENCES `account` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stat_wheel`
--

LOCK TABLES `stat_wheel` WRITE;
/*!40000 ALTER TABLE `stat_wheel` DISABLE KEYS */;
/*!40000 ALTER TABLE `stat_wheel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_info` (
  `user_info_id` int NOT NULL AUTO_INCREMENT,
  `last_name` varchar(100) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `type_id` int NOT NULL,
  `email_verified` tinyint(1) DEFAULT '0',
  `wheel_launched` int DEFAULT '0',
  `last_login_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_info_id`),
  UNIQUE KEY `mail` (`email`),
  KEY `type_id` (`type_id`),
  CONSTRAINT `user_info_ibfk_1` FOREIGN KEY (`type_id`) REFERENCES `user_type` (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=157 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info`
--

LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
INSERT INTO `user_info` VALUES (42,'d\'resto','fan','arthur.pascal33@gmail.com.',4,0,0,'2025-10-31 13:32:36'),(93,'Dupont','Marie','zazaza',4,1,0,'2025-10-31 13:32:36'),(94,'Martin','Lucas','lucas.martin@example.com',2,1,1,'2025-10-31 13:32:36'),(96,'Thomas','Noah','noah.thomas@example.com',1,1,1,'2025-10-31 13:32:36'),(97,'Petit','Léa','lea.petit@example.com',1,0,0,'2025-10-31 13:32:36'),(99,'Richard','Chloé','chloe.richard@example.com',1,1,0,'2025-10-31 13:32:36'),(100,'Durand','Hugo','hugo.durand@example.com',1,0,1,'2025-10-31 13:32:36'),(101,'Dubois','Camille','camille.dubois@example.com',1,1,0,'2025-10-31 13:32:36'),(102,'Moreau','Arthur','arthur.moreau@example.com',1,0,1,'2025-10-31 13:32:36'),(103,'Laurent','Jade','jade.laurent@example.com',1,1,0,'2025-10-31 13:32:36'),(104,'Simon','Louis','louis.simon@example.com',1,0,1,'2025-10-31 13:32:36'),(105,'Michel','Lina','lina.michel@example.com',1,1,0,'2025-10-31 13:32:36'),(106,'Lefebvre','Ethan','ethan.lefebvre@example.com',1,1,1,'2025-10-31 13:32:36'),(107,'Leroy','Anna','anna.leroy@example.com',1,0,0,'2025-10-31 13:32:36'),(108,'Roux','Adam','adam.roux@example.com',1,1,0,'2025-10-31 13:32:36'),(109,'David','Romane','romane.david@example.com',1,1,1,'2025-10-31 13:32:36'),(110,'Bertrand','Nathan','nathan.bertrand@example.com',1,0,0,'2025-10-31 13:32:36'),(111,'Morel','Manon','manon.morel@example.com',1,1,1,'2025-10-31 13:32:36'),(112,'Fournier','Paul','paul.fournier@example.com',1,1,0,'2025-10-31 13:32:36'),(113,'Girard','Zoé','zoe.girard@example.com',1,1,0,'2025-10-31 13:32:36'),(114,'Bonnet','Tom','tom.bonnet@example.com',1,0,1,'2025-10-31 13:32:36'),(115,'Dupuis','Clara','clara.dupuis@example.com',1,1,0,'2025-10-31 13:32:36'),(116,'Lambert','Léo','leo.lambert@example.com',1,0,1,'2025-10-31 13:32:36'),(117,'Fontaine','Sarah','sarah.fontaine@example.com',2,1,1,'2025-10-31 13:32:36'),(118,'Chevalier','Enzo','enzo.chevalier@example.com',1,1,0,'2025-10-31 13:32:36'),(119,'Renaud','Alice','alice.renaud@example.com',1,0,1,'2025-10-31 13:32:36'),(120,'Gautier','Noé','noe.gautier@example.com',1,1,0,'2025-10-31 13:32:36'),(121,'Lopez','Eva','eva.lopez@example.com',1,1,1,'2025-10-31 13:32:36'),(122,'Henry','Maël','mael.henry@example.com',1,0,1,'2025-10-31 13:32:36'),(123,'Masson','Inès','ines.masson@example.com',1,1,0,'2025-10-31 13:32:36'),(124,'Garnier','Nolan','nolan.garnier@example.com',1,1,1,'2025-10-31 13:32:36'),(125,'Marchand','Louna','louna.marchand@example.com',1,1,0,'2025-10-31 13:32:36'),(126,'Morin','Eden','eden.morin@example.com',1,0,1,'2025-10-31 13:32:36'),(127,'Brun','Clémence','clemence.brun@example.com',1,1,1,'2025-10-31 13:32:36'),(128,'Blanc','Théo','theo.blanc@example.com',1,1,0,'2025-10-31 13:32:36'),(129,'Guérin','Ambre','ambre.guerin@example.com',1,0,1,'2025-10-31 13:32:36'),(130,'Boyer','Mathis','mathis.boyer@example.com',1,1,0,'2025-10-31 13:32:36'),(131,'Gallet','Romy','romy.gallet@example.com',1,1,1,'2025-10-31 13:32:36'),(132,'Perrot','Eliott','eliott.perrot@example.com',1,1,0,'2025-10-31 13:32:36'),(133,'Benoit','Mila','mila.benoit@example.com',1,0,1,'2025-10-31 13:32:36'),(134,'Meyer','Noémie','noemie.meyer@example.com',1,1,1,'2025-10-31 13:32:36'),(135,'Lemoine','Aaron','aaron.lemoine@example.com',1,0,1,'2025-10-31 13:32:36'),(136,'Faure','Jeanne','jeanne.faure@example.com',1,1,0,'2025-10-31 13:32:36'),(137,'Barbier','Axel','axel.barbier@example.com',1,1,1,'2025-10-31 13:32:36'),(138,'Dupuy','Lucie','lucie.dupuy@example.com',1,0,1,'2025-10-31 13:32:36'),(139,'Renard','Julien','julien.renard@example.com',1,1,0,'2025-10-31 13:32:36'),(140,'Huet','Maya','maya.huet@example.com',1,1,1,'2025-10-31 13:32:36'),(141,'Carpentier','Gaspard','gaspard.carpentier@example.com',1,1,0,'2025-10-31 13:32:36'),(142,'Blanchard','Rose','rose.blanchard@example.com',1,0,1,'2025-10-31 13:32:36'),(143,'d\'resto','fan','fan@aresto.com',1,0,0,'2025-10-31 13:32:36'),(144,'le boss','Phillipe','no-reply@resto.com',4,0,0,'2025-10-31 13:32:36'),(146,'Upton','Aurelia','Genevieve.Murphy@yahoo.com',1,0,0,'2025-10-31 13:32:36'),(153,'Resto-roulette','Admin','restoroulette.app@gmail.com',4,0,0,'2025-10-31 13:32:36'),(154,'d\'resto','fan','fan@resto.comzazaza',1,0,0,'2025-10-31 13:32:36'),(155,'pascal','arthur','arthur.pascal33@gmail.com',1,0,0,'2025-10-31 13:32:36'),(156,'d\'resto','fan','fan@reszazazato.com',1,0,0,'2025-10-31 15:05:48');
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_type`
--

DROP TABLE IF EXISTS `user_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_type` (
  `type_id` int NOT NULL AUTO_INCREMENT,
  `role` varchar(50) NOT NULL,
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_type`
--

LOCK TABLES `user_type` WRITE;
/*!40000 ALTER TABLE `user_type` DISABLE KEYS */;
INSERT INTO `user_type` VALUES (1,'USER'),(2,'ROLE_RESTAURANT_OWNER'),(3,'ROLE_MODERATOR'),(4,'ADMIN');
/*!40000 ALTER TABLE `user_type` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-13 14:20:16
