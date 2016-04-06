/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CAPAdministration` (
  `ENV` varchar(50) NOT NULL,
  `VALUE` varchar(500) DEFAULT NULL,
  `ATTACHMENT` blob,
  PRIMARY KEY (`ENV`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Instance` (
  `instance_id` varchar(150) NOT NULL,
  `series_id` varchar(150) NOT NULL,
  `dicom_metadata` blob COMMENT 'The xml associated with dicom header',
  PRIMARY KEY (`instance_id`,`series_id`),
  KEY `fk_Instance_series_idx` (`series_id`),
  CONSTRAINT `fk_Instance_series` FOREIGN KEY (`series_id`) REFERENCES `Series` (`series_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `InstanceView` (
  `instance_id` tinyint NOT NULL,
  `subject_id` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Model` (
  `model_id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` varchar(150) NOT NULL,
  `model_name` varchar(255) NOT NULL,
  `model_comments` varchar(2500) DEFAULT NULL,
  `model_metadata` longblob,
  `model_xml` longblob,
  `model_exarchive` longblob,
  `model_vtparchive` longblob,
  PRIMARY KEY (`model_id`),
  UNIQUE KEY `model_name_UNIQUE` (`model_name`),
  KEY `fk_Model_Study_idx` (`study_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `ModelView` (
  `subject_id` tinyint NOT NULL,
  `subject_name` tinyint NOT NULL,
  `subject_birthdate` tinyint NOT NULL,
  `subject_gender` tinyint NOT NULL,
  `study_id` tinyint NOT NULL,
  `study_modalities` tinyint NOT NULL,
  `study_date` tinyint NOT NULL,
  `study_description` tinyint NOT NULL,
  `model_id` tinyint NOT NULL,
  `model_name` tinyint NOT NULL,
  `model_comments` tinyint NOT NULL,
  `model_hasMetaData` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Notifications` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `event` varchar(255) NOT NULL,
  `message` varchar(1500) DEFAULT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Series` (
  `series_id` varchar(150) NOT NULL,
  `study_id` varchar(150) NOT NULL,
  `series_modality` varchar(255) NOT NULL,
  PRIMARY KEY (`series_id`,`study_id`),
  KEY `fk_Series_Study_idx` (`study_id`),
  CONSTRAINT `fk_Series_Study` FOREIGN KEY (`study_id`) REFERENCES `Study` (`study_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Study` (
  `study_id` varchar(150) NOT NULL,
  `subject_id` varchar(45) NOT NULL,
  `study_modalities` varchar(25) DEFAULT NULL,
  `study_date` varchar(45) DEFAULT NULL,
  `study_description` varchar(500) DEFAULT NULL,
  `study_metadata` longblob,
  `study_json` longblob COMMENT 'Contains the json structure that should be appended to a search result',
  PRIMARY KEY (`study_id`,`subject_id`),
  KEY `fk_Study_Subject_idx` (`subject_id`),
  CONSTRAINT `fk_Study_Subject` FOREIGN KEY (`subject_id`) REFERENCES `Subject` (`subject_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `StudyMetaData` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` varchar(150) NOT NULL,
  `descriptor` varchar(255) NOT NULL,
  `filename` varchar(255) NOT NULL,
  `data` longblob NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_StudyMetaData_1_idx` (`study_id`),
  CONSTRAINT `fk_StudyMetaData_1` FOREIGN KEY (`study_id`) REFERENCES `Study` (`study_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `StudyPACSData` (
  `study_id` varchar(150) NOT NULL,
  `study_pacsdata` longblob NOT NULL,
  PRIMARY KEY (`study_id`),
  CONSTRAINT `fk_StudyPACSData_1` FOREIGN KEY (`study_id`) REFERENCES `Study` (`study_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `StudyView` (
  `subject_id` tinyint NOT NULL,
  `subject_name` tinyint NOT NULL,
  `subject_birthdate` tinyint NOT NULL,
  `subject_gender` tinyint NOT NULL,
  `study_id` tinyint NOT NULL,
  `study_modalities` tinyint NOT NULL,
  `study_date` tinyint NOT NULL,
  `study_description` tinyint NOT NULL,
  `study_metadata` tinyint NOT NULL,
  `study_json` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Subject` (
  `subject_id` varchar(45) NOT NULL,
  `subject_name` varchar(255) NOT NULL,
  `subject_birthdate` varchar(45) DEFAULT NULL,
  `subject_gender` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`subject_id`),
  UNIQUE KEY `subject_id_UNIQUE` (`subject_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50001 DROP TABLE IF EXISTS `InstanceView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `InstanceView` AS select `Instance`.`instance_id` AS `instance_id`,`Subject`.`subject_id` AS `subject_id` from (((`Instance` join `Series`) join `Study`) join `Subject`) where ((`Instance`.`series_id` = `Series`.`series_id`) and (`Series`.`study_id` = `Study`.`study_id`) and (`Study`.`subject_id` = `Subject`.`subject_id`)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!50001 DROP TABLE IF EXISTS `ModelView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `ModelView` AS select `Subject`.`subject_id` AS `subject_id`,`Subject`.`subject_name` AS `subject_name`,`Subject`.`subject_birthdate` AS `subject_birthdate`,`Subject`.`subject_gender` AS `subject_gender`,`Study`.`study_id` AS `study_id`,`Study`.`study_modalities` AS `study_modalities`,`Study`.`study_date` AS `study_date`,`Study`.`study_description` AS `study_description`,`Model`.`model_id` AS `model_id`,`Model`.`model_name` AS `model_name`,`Model`.`model_comments` AS `model_comments`,isnull(`Model`.`model_comments`) AS `model_hasMetaData` from (`Model` join (`Subject` join `Study`)) where ((`Study`.`study_id` = `Model`.`study_id`) and (`Subject`.`subject_id` = `Study`.`subject_id`)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!50001 DROP TABLE IF EXISTS `StudyView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `StudyView` AS select `Subject`.`subject_id` AS `subject_id`,`Subject`.`subject_name` AS `subject_name`,`Subject`.`subject_birthdate` AS `subject_birthdate`,`Subject`.`subject_gender` AS `subject_gender`,`Study`.`study_id` AS `study_id`,`Study`.`study_modalities` AS `study_modalities`,`Study`.`study_date` AS `study_date`,`Study`.`study_description` AS `study_description`,`Study`.`study_metadata` AS `study_metadata`,`Study`.`study_json` AS `study_json` from (`Subject` join `Study`) where (`Subject`.`subject_id` = `Study`.`subject_id`) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
