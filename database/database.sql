-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: login_schema
-- ------------------------------------------------------
-- Server version	8.0.43

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
-- Table structure for table `blood_stock_table`
--

DROP TABLE IF EXISTS `blood_stock_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blood_stock_table` (
  `stock_id` int NOT NULL AUTO_INCREMENT,
  `camp_id` int DEFAULT NULL,
  `blood_group` varchar(45) DEFAULT NULL,
  `quantity` int DEFAULT '0',
  PRIMARY KEY (`stock_id`),
  KEY `campid_idx` (`camp_id`),
  CONSTRAINT `id` FOREIGN KEY (`camp_id`) REFERENCES `camp_table` (`camp_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blood_stock_table`
--

LOCK TABLES `blood_stock_table` WRITE;
/*!40000 ALTER TABLE `blood_stock_table` DISABLE KEYS */;
INSERT INTO `blood_stock_table` VALUES (1,4,'B+',20),(3,1,'B+',20),(4,1,'C+',32),(5,1,'O+',20),(6,5,'O+',35);
/*!40000 ALTER TABLE `blood_stock_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `camp_table`
--

DROP TABLE IF EXISTS `camp_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `camp_table` (
  `camp_ID` int NOT NULL AUTO_INCREMENT,
  `camp_name` varchar(100) DEFAULT NULL,
  `location` varchar(45) DEFAULT NULL,
  `date` date DEFAULT NULL,
  PRIMARY KEY (`camp_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `camp_table`
--

LOCK TABLES `camp_table` WRITE;
/*!40000 ALTER TABLE `camp_table` DISABLE KEYS */;
INSERT INTO `camp_table` VALUES (1,'nss camp','scms','2025-11-14'),(2,'nss camp','scms','2025-10-02'),(3,'nss camp','scms','2025-10-14'),(4,'blood camp','scms','2025-09-29'),(5,'hospital camp','scms','2025-10-08');
/*!40000 ALTER TABLE `camp_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `donor_table`
--

DROP TABLE IF EXISTS `donor_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `donor_table` (
  `donor_id` int NOT NULL AUTO_INCREMENT,
  `camp_id` int DEFAULT NULL,
  `donor_name` varchar(45) DEFAULT NULL,
  `age` int DEFAULT NULL,
  `gender` varchar(45) DEFAULT NULL,
  `blood_group` varchar(45) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `last_donation_date` date DEFAULT NULL,
  PRIMARY KEY (`donor_id`),
  KEY `camp_id_idx` (`camp_id`),
  CONSTRAINT `camp_id` FOREIGN KEY (`camp_id`) REFERENCES `camp_table` (`camp_ID`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `donor_table`
--

LOCK TABLES `donor_table` WRITE;
/*!40000 ALTER TABLE `donor_table` DISABLE KEYS */;
INSERT INTO `donor_table` VALUES (1,1,'aswani',18,'Female','A+','1234567890','2024-08-10'),(2,1,'deva',20,'Female','O+','345780052','2023-02-20'),(3,5,'hanna',19,'Female','O+','9037026671','2024-02-03'),(4,4,'aswani k b',18,'Female','A+','7143575846','2023-12-24');
/*!40000 ALTER TABLE `donor_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login_table`
--

DROP TABLE IF EXISTS `login_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login_table` (
  `login_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `role` enum('Admin','Organization') NOT NULL,
  PRIMARY KEY (`login_id`),
  UNIQUE KEY `new_tablecol_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_table`
--

LOCK TABLES `login_table` WRITE;
/*!40000 ALTER TABLE `login_table` DISABLE KEYS */;
INSERT INTO `login_table` VALUES (1,'aswani','aswani@14','Admin'),(3,'hanna','hanna@14','Admin'),(4,'nsschalakudy','nss@123','Organization'),(7,'nssunit','nss@123','Organization'),(8,'nssangamaly12','nss123','Organization'),(9,'weunit','we2123','Organization'),(10,'krishnaunit','krishna@123','Organization');
/*!40000 ALTER TABLE `login_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization_table`
--

DROP TABLE IF EXISTS `organization_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `organization_table` (
  `org_ID` int NOT NULL AUTO_INCREMENT,
  `login_ID` int NOT NULL,
  `OrgName` varchar(45) NOT NULL,
  PRIMARY KEY (`org_ID`),
  KEY `login_id_idx` (`login_ID`),
  CONSTRAINT `login_id` FOREIGN KEY (`login_ID`) REFERENCES `login_table` (`login_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization_table`
--

LOCK TABLES `organization_table` WRITE;
/*!40000 ALTER TABLE `organization_table` DISABLE KEYS */;
INSERT INTO `organization_table` VALUES (2,7,'nss chalakudy'),(3,8,'nss angamaly'),(4,9,'wee'),(5,10,'krishna');
/*!40000 ALTER TABLE `organization_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `request_form_table`
--

DROP TABLE IF EXISTS `request_form_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `request_form_table` (
  `request_id` int NOT NULL AUTO_INCREMENT,
  `org_id` int DEFAULT NULL,
  `camp_id` int DEFAULT NULL,
  `blood_group` varchar(45) DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `status` enum('pending','Accepted','Rejected') DEFAULT 'pending',
  `request_date` date DEFAULT NULL,
  PRIMARY KEY (`request_id`),
  KEY `orgid_idx` (`org_id`),
  KEY `camp_idx` (`camp_id`),
  CONSTRAINT `camp` FOREIGN KEY (`camp_id`) REFERENCES `camp_table` (`camp_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `orgid` FOREIGN KEY (`org_id`) REFERENCES `organization_table` (`org_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `request_form_table`
--

LOCK TABLES `request_form_table` WRITE;
/*!40000 ALTER TABLE `request_form_table` DISABLE KEYS */;
INSERT INTO `request_form_table` VALUES (1,2,NULL,'A+',2,'Accepted','2025-10-02'),(2,2,NULL,'A+',2,'Rejected','2025-10-09'),(3,2,NULL,'B+',2,'Accepted','2025-12-25'),(4,2,NULL,'A+',3,'Rejected','2025-12-03'),(10,2,NULL,'A+',20,'pending','2025-11-14'),(11,2,NULL,'A+',20,'Accepted','2025-11-14'),(12,5,NULL,'O+',20,'pending','2025-11-14'),(13,2,NULL,'A+',20,'Accepted','2025-10-10');
/*!40000 ALTER TABLE `request_form_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `volunteer_table`
--

DROP TABLE IF EXISTS `volunteer_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `volunteer_table` (
  `volunteer_id` int NOT NULL AUTO_INCREMENT,
  `volunteer_name` varchar(45) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `role` varchar(45) DEFAULT NULL,
  `camp_ID` int DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`volunteer_id`),
  KEY `camp_ID_idx` (`camp_ID`),
  CONSTRAINT `campid` FOREIGN KEY (`camp_ID`) REFERENCES `camp_table` (`camp_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `volunteer_table`
--

LOCK TABLES `volunteer_table` WRITE;
/*!40000 ALTER TABLE `volunteer_table` DISABLE KEYS */;
INSERT INTO `volunteer_table` VALUES (1,'aswani','1234567891',NULL,1,NULL),(3,'hanna','1326849271',NULL,4,NULL),(4,'kannan','1234567890',NULL,4,NULL);
/*!40000 ALTER TABLE `volunteer_table` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-07 23:05:15
