-- MySQL dump 10.13  Distrib 8.0.23, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: pann_financial_system
-- ------------------------------------------------------
-- Server version	8.0.23

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
-- Table structure for table `admins`
--

DROP TABLE IF EXISTS `admins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admins` (
  `admin_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `student_number` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `department_id` bigint DEFAULT NULL,
  `is_super_admin` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`admin_id`),
  UNIQUE KEY `student_number` (`student_number`),
  UNIQUE KEY `uk_admin_student_number` (`student_number`),
  KEY `idx_admin_user_id` (`user_id`),
  KEY `idx_admin_department_id` (`department_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admins`
--

LOCK TABLES `admins` WRITE;
/*!40000 ALTER TABLE `admins` DISABLE KEYS */;
INSERT INTO `admins` VALUES (1,1,'202311110001','系统管理员','xt123456','123456789@qq.com',NULL,1);
/*!40000 ALTER TABLE `admins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `announcements`
--

DROP TABLE IF EXISTS `announcements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcements` (
  `announcement_id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `publisher_id` bigint NOT NULL,
  `published_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`announcement_id`),
  KEY `idx_announcement_publisher` (`publisher_id`),
  KEY `idx_announcement_published_at` (`published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcements`
--

LOCK TABLES `announcements` WRITE;
/*!40000 ALTER TABLE `announcements` DISABLE KEYS */;
/*!40000 ALTER TABLE `announcements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `departments`
--

DROP TABLE IF EXISTS `departments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `departments` (
  `department_id` bigint NOT NULL AUTO_INCREMENT,
  `department_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`department_id`),
  UNIQUE KEY `department_name` (`department_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `departments`
--

LOCK TABLES `departments` WRITE;
/*!40000 ALTER TABLE `departments` DISABLE KEYS */;
INSERT INTO `departments` VALUES (1,'新闻部'),(2,'编辑部'),(3,'运营部');
/*!40000 ALTER TABLE `departments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `editorial_department_monthly`
--

DROP TABLE IF EXISTS `editorial_department_monthly`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `editorial_department_monthly` (
  `record_id` bigint NOT NULL AUTO_INCREMENT,
  `user_ids` json NOT NULL,
  `real_names` json NOT NULL,
  `student_numbers` json NOT NULL,
  `article_title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `article_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fee_amount` decimal(10,2) NOT NULL,
  `statistical_month` varchar(7) COLLATE utf8mb4_unicode_ci NOT NULL,
  `department_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`),
  KEY `idx_editorial_statistical_month` (`statistical_month`),
  KEY `idx_editorial_department_id` (`department_id`),
  KEY `idx_editorial_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `editorial_department_monthly`
--

LOCK TABLES `editorial_department_monthly` WRITE;
/*!40000 ALTER TABLE `editorial_department_monthly` DISABLE KEYS */;
/*!40000 ALTER TABLE `editorial_department_monthly` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee_monthly_summary`
--

DROP TABLE IF EXISTS `fee_monthly_summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fee_monthly_summary` (
  `summary_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `student_number` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `department_id` bigint NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `statistical_month` varchar(7) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`summary_id`),
  KEY `idx_summary_user_month` (`user_id`,`statistical_month`),
  KEY `idx_summary_department_month` (`department_id`,`statistical_month`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fee_monthly_summary`
--

LOCK TABLES `fee_monthly_summary` WRITE;
/*!40000 ALTER TABLE `fee_monthly_summary` DISABLE KEYS */;
INSERT INTO `fee_monthly_summary` VALUES (1,2,'张三','202311110002',1,600.00,'2025-12','2025-12-18 13:04:30','2026-01-09 07:31:57');
/*!40000 ALTER TABLE `fee_monthly_summary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedbacks`
--

DROP TABLE IF EXISTS `feedbacks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedbacks` (
  `feedback_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `reply_content` text COLLATE utf8mb4_unicode_ci,
  `replied_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feedback_id`),
  KEY `idx_feedback_user_id` (`user_id`),
  KEY `idx_feedback_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedbacks`
--

LOCK TABLES `feedbacks` WRITE;
/*!40000 ALTER TABLE `feedbacks` DISABLE KEYS */;
/*!40000 ALTER TABLE `feedbacks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login_logs`
--

DROP TABLE IF EXISTS `login_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login_logs` (
  `log_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `ip_address` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL,
  `login_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`log_id`),
  KEY `idx_login_user_id` (`user_id`),
  KEY `idx_login_at` (`login_at`),
  KEY `idx_login_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_logs`
--

LOCK TABLES `login_logs` WRITE;
/*!40000 ALTER TABLE `login_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `login_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news_department_monthly`
--

DROP TABLE IF EXISTS `news_department_monthly`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news_department_monthly` (
  `record_id` bigint NOT NULL AUTO_INCREMENT,
  `user_ids` json NOT NULL,
  `real_names` json NOT NULL,
  `student_numbers` json NOT NULL,
  `article_title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `article_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fee_amount` decimal(10,2) NOT NULL,
  `statistical_month` varchar(7) COLLATE utf8mb4_unicode_ci NOT NULL,
  `department_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`),
  KEY `idx_news_statistical_month` (`statistical_month`),
  KEY `idx_news_department_id` (`department_id`),
  KEY `idx_news_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news_department_monthly`
--

LOCK TABLES `news_department_monthly` WRITE;
/*!40000 ALTER TABLE `news_department_monthly` DISABLE KEYS */;
INSERT INTO `news_department_monthly` VALUES (3,'[2]','[\"张三\"]','[\"202311110002\"]','测试更新删除稿件','测试类型',100.00,'2025-12',1,'2025-12-18 13:09:22','2026-01-09 07:31:46'),(4,'[2]','[\"张三\"]','[\"202311110002\"]','测试新增稿件','新闻稿',500.00,'2025-12',1,'2025-12-18 13:09:22','2026-01-09 07:31:46');
/*!40000 ALTER TABLE `news_department_monthly` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissions` (
  `permission_id` bigint NOT NULL AUTO_INCREMENT,
  `permission_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `permission_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`permission_id`),
  UNIQUE KEY `permission_code` (`permission_code`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
INSERT INTO `permissions` VALUES (1,'USER_READ','用户查看','查看用户信息权限','2025-11-29 07:59:21','2025-11-29 07:59:21'),(2,'USER_WRITE','用户管理','管理用户信息权限','2025-11-29 07:59:21','2025-11-29 07:59:21'),(3,'FEE_READ','稿费查看','查看稿费信息权限','2025-11-29 07:59:21','2025-11-29 07:59:21'),(4,'FEE_WRITE','稿费管理','管理稿费信息权限','2025-11-29 07:59:21','2025-11-29 07:59:21'),(5,'REPORT_READ','报表查看','查看统计报表权限','2025-11-29 07:59:21','2025-11-29 07:59:21'),(6,'SYSTEM_MANAGE','系统管理','系统管理权限','2025-11-29 07:59:21','2025-11-29 07:59:21');
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proxy_receipt_records`
--

DROP TABLE IF EXISTS `proxy_receipt_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proxy_receipt_records` (
  `proxy_id` bigint NOT NULL AUTO_INCREMENT,
  `fee_record_id` bigint NOT NULL,
  `department_id` bigint NOT NULL,
  `original_user_id` bigint NOT NULL,
  `original_real_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `original_student_number` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `proxy_user_id` bigint NOT NULL,
  `proxy_real_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `proxy_student_number` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `article_title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fee_amount` decimal(10,2) NOT NULL,
  `proxy_month` varchar(7) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`proxy_id`),
  KEY `idx_original_user_id` (`original_user_id`),
  KEY `idx_proxy_user_id` (`proxy_user_id`),
  KEY `idx_proxy_month` (`proxy_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proxy_receipt_records`
--

LOCK TABLES `proxy_receipt_records` WRITE;
/*!40000 ALTER TABLE `proxy_receipt_records` DISABLE KEYS */;
/*!40000 ALTER TABLE `proxy_receipt_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_permissions`
--

DROP TABLE IF EXISTS `user_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_permission_user` (`user_id`),
  KEY `idx_user_permission_permission` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_permissions`
--

LOCK TABLES `user_permissions` WRITE;
/*!40000 ALTER TABLE `user_permissions` DISABLE KEYS */;
INSERT INTO `user_permissions` VALUES (1,1,3,'2025-11-29 08:03:06'),(2,1,4,'2025-11-29 08:03:06'),(3,1,5,'2025-11-29 08:03:06'),(4,1,6,'2025-11-29 08:03:06'),(5,1,1,'2025-11-29 08:03:06'),(6,1,2,'2025-11-29 08:03:06');
/*!40000 ALTER TABLE `user_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `student_number` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `student_number` (`student_number`),
  UNIQUE KEY `uk_student_number` (`student_number`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'202311110001','系统管理员','xt123456','123456789@qq.com'),(2,'202311110002','张三','zs123456','234567891@qq.com');
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

-- Dump completed on 2026-01-09 17:06:04
