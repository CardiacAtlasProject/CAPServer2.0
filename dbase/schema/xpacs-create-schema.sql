CREATE DATABASE  IF NOT EXISTS `xpacs` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `xpacs`;
-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: 130.216.161.114    Database: xpacs
-- ------------------------------------------------------
-- Server version	5.7.17

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `DATABASECHANGELOG`
--

DROP TABLE IF EXISTS `DATABASECHANGELOG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DATABASECHANGELOG` (
  `ID` varchar(255) NOT NULL,
  `AUTHOR` varchar(255) NOT NULL,
  `FILENAME` varchar(255) NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int(11) NOT NULL,
  `EXECTYPE` varchar(10) NOT NULL,
  `MD5SUM` varchar(35) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `COMMENTS` varchar(255) DEFAULT NULL,
  `TAG` varchar(255) DEFAULT NULL,
  `LIQUIBASE` varchar(20) DEFAULT NULL,
  `CONTEXTS` varchar(255) DEFAULT NULL,
  `LABELS` varchar(255) DEFAULT NULL,
  `DEPLOYMENT_ID` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DATABASECHANGELOG`
--

LOCK TABLES `DATABASECHANGELOG` WRITE;
/*!40000 ALTER TABLE `DATABASECHANGELOG` DISABLE KEYS */;
INSERT INTO `DATABASECHANGELOG` VALUES ('00000000000001','jhipster','classpath:config/liquibase/changelog/00000000000000_initial_schema.xml','2017-03-13 16:35:05',1,'EXECUTED','7:a91966e379c49b41dd5b60b86be5e082','createTable tableName=jhi_user; createIndex indexName=idx_user_login, tableName=jhi_user; createIndex indexName=idx_user_email, tableName=jhi_user; createTable tableName=jhi_authority; createTable tableName=jhi_user_authority; addPrimaryKey tableN...','',NULL,'3.5.3',NULL,NULL,'9376105049'),('20170219223541-1','jhipster','classpath:config/liquibase/changelog/20170219223541_added_entity_PatientInfo.xml','2017-03-13 16:35:05',2,'EXECUTED','7:f4f7c25d3b6c8400cf53f96ba4bf8191','createTable tableName=patient_info','',NULL,'3.5.3',NULL,NULL,'9376105049'),('20170221212346-1','jhipster','classpath:config/liquibase/changelog/20170221212346_added_entity_CapModel.xml','2017-03-13 16:35:05',3,'EXECUTED','7:3e605397a7745f4d586dc725ca04cec3','createTable tableName=cap_model','',NULL,'3.5.3',NULL,NULL,'9376105049'),('20170221213343-1','jhipster','classpath:config/liquibase/changelog/20170221213343_added_entity_AuxFile.xml','2017-03-13 16:35:05',4,'EXECUTED','7:6349f713cea54e648850c030bee39f36','createTable tableName=aux_file','',NULL,'3.5.3',NULL,NULL,'9376105049'),('20170221214051-1','jhipster','classpath:config/liquibase/changelog/20170221214051_added_entity_BaselineDiagnosis.xml','2017-03-13 16:35:06',5,'EXECUTED','7:354d90b0116c5afd59180614cbfbf643','createTable tableName=baseline_diagnosis','',NULL,'3.5.3',NULL,NULL,'9376105049'),('20170221095825-1','jhipster','classpath:config/liquibase/changelog/20170221095825_added_entity_ClinicalNote.xml','2017-03-13 16:35:06',6,'EXECUTED','7:de2224f12fcf550e0774867800950c19','createTable tableName=clinical_note','',NULL,'3.5.3',NULL,NULL,'9376105049'),('20170221212346-2','jhipster','classpath:config/liquibase/changelog/20170221212346_added_entity_constraints_CapModel.xml','2017-03-13 16:35:06',7,'EXECUTED','7:faae908cd07ffc92df72d30e63c54d9e','addForeignKeyConstraint baseTableName=cap_model, constraintName=fk_cap_model_patient_infofk_id, referencedTableName=patient_info','',NULL,'3.5.3',NULL,NULL,'9376105049'),('20170221213343-2','jhipster','classpath:config/liquibase/changelog/20170221213343_added_entity_constraints_AuxFile.xml','2017-03-13 16:35:06',8,'EXECUTED','7:5986b3863361a24943edfde5f04d6dd5','addForeignKeyConstraint baseTableName=aux_file, constraintName=fk_aux_file_patient_infofk_id, referencedTableName=patient_info','',NULL,'3.5.3',NULL,NULL,'9376105049'),('20170221214051-2','jhipster','classpath:config/liquibase/changelog/20170221214051_added_entity_constraints_BaselineDiagnosis.xml','2017-03-13 16:35:06',9,'EXECUTED','7:53e1dd55a04524a192f1653476658ef2','addForeignKeyConstraint baseTableName=baseline_diagnosis, constraintName=fk_baseline_diagnosis_patient_infofk_id, referencedTableName=patient_info','',NULL,'3.5.3',NULL,NULL,'9376105049'),('20170221095825-2','jhipster','classpath:config/liquibase/changelog/20170221095825_added_entity_constraints_ClinicalNote.xml','2017-03-13 16:35:06',10,'EXECUTED','7:6689397856fc52daf194b56c1a820c73','addForeignKeyConstraint baseTableName=clinical_note, constraintName=fk_clinical_note_patient_infofk_id, referencedTableName=patient_info','',NULL,'3.5.3',NULL,NULL,'9376105049');
/*!40000 ALTER TABLE `DATABASECHANGELOG` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `DATABASECHANGELOGLOCK`
--

DROP TABLE IF EXISTS `DATABASECHANGELOGLOCK`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DATABASECHANGELOGLOCK` (
  `ID` int(11) NOT NULL,
  `LOCKED` bit(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DATABASECHANGELOGLOCK`
--

LOCK TABLES `DATABASECHANGELOGLOCK` WRITE;
/*!40000 ALTER TABLE `DATABASECHANGELOGLOCK` DISABLE KEYS */;
INSERT INTO `DATABASECHANGELOGLOCK` VALUES (1,'\0',NULL,NULL);
/*!40000 ALTER TABLE `DATABASECHANGELOGLOCK` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aux_file`
--

DROP TABLE IF EXISTS `aux_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aux_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` date NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `file` longblob NOT NULL,
  `file_content_type` varchar(255) NOT NULL,
  `patient_infofk_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_aux_file_patient_infofk_id` (`patient_infofk_id`),
  CONSTRAINT `fk_aux_file_patient_infofk_id` FOREIGN KEY (`patient_infofk_id`) REFERENCES `patient_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aux_file`
--

LOCK TABLES `aux_file` WRITE;
/*!40000 ALTER TABLE `aux_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `aux_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `baseline_diagnosis`
--

DROP TABLE IF EXISTS `baseline_diagnosis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `baseline_diagnosis` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `diagnosis_date` date NOT NULL,
  `age` float DEFAULT NULL,
  `height` varchar(255) DEFAULT NULL,
  `weight` varchar(255) DEFAULT NULL,
  `heart_rate` varchar(255) DEFAULT NULL,
  `dbp` varchar(255) DEFAULT NULL,
  `sbp` varchar(255) DEFAULT NULL,
  `history_of_alcohol` varchar(255) DEFAULT NULL,
  `history_of_diabetes` varchar(255) DEFAULT NULL,
  `history_of_hypertension` varchar(255) DEFAULT NULL,
  `history_of_smoking` varchar(255) DEFAULT NULL,
  `patient_infofk_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_baseline_diagnosis_patient_infofk_id` (`patient_infofk_id`),
  CONSTRAINT `fk_baseline_diagnosis_patient_infofk_id` FOREIGN KEY (`patient_infofk_id`) REFERENCES `patient_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `baseline_diagnosis`
--

LOCK TABLES `baseline_diagnosis` WRITE;
/*!40000 ALTER TABLE `baseline_diagnosis` DISABLE KEYS */;
/*!40000 ALTER TABLE `baseline_diagnosis` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cap_model`
--

DROP TABLE IF EXISTS `cap_model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cap_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` date NOT NULL,
  `name` varchar(255) NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `model_file` longblob,
  `model_file_content_type` varchar(255) DEFAULT NULL,
  `xml_file` longblob,
  `xml_file_content_type` varchar(255) DEFAULT NULL,
  `patient_infofk_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_cap_model_patient_infofk_id` (`patient_infofk_id`),
  CONSTRAINT `fk_cap_model_patient_infofk_id` FOREIGN KEY (`patient_infofk_id`) REFERENCES `patient_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cap_model`
--

LOCK TABLES `cap_model` WRITE;
/*!40000 ALTER TABLE `cap_model` DISABLE KEYS */;
/*!40000 ALTER TABLE `cap_model` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clinical_note`
--

DROP TABLE IF EXISTS `clinical_note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clinical_note` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assessment_date` date NOT NULL,
  `age` float DEFAULT NULL,
  `height` varchar(255) DEFAULT NULL,
  `weight` varchar(255) DEFAULT NULL,
  `diagnosis` varchar(255) DEFAULT NULL,
  `note` longtext,
  `patient_infofk_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_clinical_note_patient_infofk_id` (`patient_infofk_id`),
  CONSTRAINT `fk_clinical_note_patient_infofk_id` FOREIGN KEY (`patient_infofk_id`) REFERENCES `patient_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clinical_note`
--

LOCK TABLES `clinical_note` WRITE;
/*!40000 ALTER TABLE `clinical_note` DISABLE KEYS */;
/*!40000 ALTER TABLE `clinical_note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jhi_authority`
--

DROP TABLE IF EXISTS `jhi_authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jhi_authority` (
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jhi_authority`
--

LOCK TABLES `jhi_authority` WRITE;
/*!40000 ALTER TABLE `jhi_authority` DISABLE KEYS */;
INSERT INTO `jhi_authority` VALUES ('ROLE_ADMIN'),('ROLE_USER');
/*!40000 ALTER TABLE `jhi_authority` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jhi_persistent_audit_event`
--

DROP TABLE IF EXISTS `jhi_persistent_audit_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jhi_persistent_audit_event` (
  `event_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `principal` varchar(50) NOT NULL,
  `event_date` timestamp NULL DEFAULT NULL,
  `event_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`event_id`),
  KEY `idx_persistent_audit_event` (`principal`,`event_date`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jhi_persistent_audit_event`
--

LOCK TABLES `jhi_persistent_audit_event` WRITE;
/*!40000 ALTER TABLE `jhi_persistent_audit_event` DISABLE KEYS */;
/*!40000 ALTER TABLE `jhi_persistent_audit_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jhi_persistent_audit_evt_data`
--

DROP TABLE IF EXISTS `jhi_persistent_audit_evt_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jhi_persistent_audit_evt_data` (
  `event_id` bigint(20) NOT NULL,
  `name` varchar(150) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`event_id`,`name`),
  KEY `idx_persistent_audit_evt_data` (`event_id`),
  CONSTRAINT `fk_evt_pers_audit_evt_data` FOREIGN KEY (`event_id`) REFERENCES `jhi_persistent_audit_event` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jhi_persistent_audit_evt_data`
--

LOCK TABLES `jhi_persistent_audit_evt_data` WRITE;
/*!40000 ALTER TABLE `jhi_persistent_audit_evt_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `jhi_persistent_audit_evt_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jhi_persistent_token`
--

DROP TABLE IF EXISTS `jhi_persistent_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jhi_persistent_token` (
  `series` varchar(20) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `token_value` varchar(20) NOT NULL,
  `token_date` date DEFAULT NULL,
  `ip_address` varchar(39) DEFAULT NULL,
  `user_agent` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`series`),
  KEY `fk_user_persistent_token` (`user_id`),
  CONSTRAINT `fk_user_persistent_token` FOREIGN KEY (`user_id`) REFERENCES `jhi_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jhi_persistent_token`
--

LOCK TABLES `jhi_persistent_token` WRITE;
/*!40000 ALTER TABLE `jhi_persistent_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `jhi_persistent_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jhi_user`
--

DROP TABLE IF EXISTS `jhi_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jhi_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `login` varchar(50) NOT NULL,
  `password_hash` varchar(60) DEFAULT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `image_url` varchar(256) DEFAULT NULL,
  `activated` bit(1) NOT NULL,
  `lang_key` varchar(5) DEFAULT NULL,
  `activation_key` varchar(20) DEFAULT NULL,
  `reset_key` varchar(20) DEFAULT NULL,
  `created_by` varchar(50) NOT NULL,
  `created_date` timestamp NOT NULL,
  `reset_date` timestamp NULL DEFAULT NULL,
  `last_modified_by` varchar(50) DEFAULT NULL,
  `last_modified_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `login` (`login`),
  UNIQUE KEY `idx_user_login` (`login`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `idx_user_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jhi_user`
--

LOCK TABLES `jhi_user` WRITE;
/*!40000 ALTER TABLE `jhi_user` DISABLE KEYS */;
INSERT INTO `jhi_user` VALUES (1,'system','$2a$10$mE.qmcV0mFU5NcKh73TZx.z4ueI/.bDWbj0T1BYyqP481kGGarKLG','System','System','system@localhost','','','en',NULL,NULL,'system','2017-03-13 03:35:05',NULL,'system',NULL),(2,'anonymoususer','$2a$10$j8S5d7Sr7.8VTOYNviDPOeWX8KcYILUVJBsYV83Y5NtECayypx9lO','Anonymous','User','anonymous@localhost','','','en',NULL,NULL,'system','2017-03-13 03:35:05',NULL,'system',NULL),(3,'admin','$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC','Administrator','Administrator','admin@localhost','','','en',NULL,NULL,'system','2017-03-13 03:35:05',NULL,'system',NULL),(4,'user','$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K','User','User','user@localhost','','','en',NULL,NULL,'system','2017-03-13 03:35:05',NULL,'system',NULL);
/*!40000 ALTER TABLE `jhi_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jhi_user_authority`
--

DROP TABLE IF EXISTS `jhi_user_authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jhi_user_authority` (
  `user_id` bigint(20) NOT NULL,
  `authority_name` varchar(50) NOT NULL,
  PRIMARY KEY (`user_id`,`authority_name`),
  KEY `fk_authority_name` (`authority_name`),
  CONSTRAINT `fk_authority_name` FOREIGN KEY (`authority_name`) REFERENCES `jhi_authority` (`name`),
  CONSTRAINT `fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `jhi_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jhi_user_authority`
--

LOCK TABLES `jhi_user_authority` WRITE;
/*!40000 ALTER TABLE `jhi_user_authority` DISABLE KEYS */;
INSERT INTO `jhi_user_authority` VALUES (1,'ROLE_ADMIN'),(3,'ROLE_ADMIN'),(1,'ROLE_USER'),(3,'ROLE_USER'),(4,'ROLE_USER');
/*!40000 ALTER TABLE `jhi_user_authority` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient_info`
--

DROP TABLE IF EXISTS `patient_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `patient_id` varchar(255) NOT NULL,
  `cohort` varchar(255) DEFAULT NULL,
  `ethnicity` varchar(255) DEFAULT NULL,
  `gender` varchar(255) NOT NULL,
  `primary_diagnosis` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient_info`
--

LOCK TABLES `patient_info` WRITE;
/*!40000 ALTER TABLE `patient_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `patient_info` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-03-13 16:36:08
