-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema xpacs
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema xpacs
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `xpacs` DEFAULT CHARACTER SET utf8 ;
USE `xpacs` ;

-- -----------------------------------------------------
-- Table `xpacs`.`PATIENT_INFO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `xpacs`.`PATIENT_INFO` (
  `patient_id` VARCHAR(255) NOT NULL,
  `cohort` VARCHAR(255) NOT NULL,
  `ethnicity` VARCHAR(255) NULL DEFAULT NULL,
  `gender` VARCHAR(255) NOT NULL,
  `primary_diagnosis` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`patient_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `xpacs`.`AUX_FILE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `xpacs`.`AUX_FILE` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `created_date` DATE NOT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `filename` VARCHAR(255) NOT NULL,
  `uri` VARCHAR(255) NOT NULL,
  `patient_id` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK_AUX_FILE_PATIENT_INFO_Id` (`patient_id` ASC),
  CONSTRAINT `FK_AUX_FILE_PATIENT_INFO_Id`
    FOREIGN KEY (`patient_id`)
    REFERENCES `xpacs`.`PATIENT_INFO` (`patient_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `xpacs`.`BASELINE_DIAGNOSIS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `xpacs`.`BASELINE_DIAGNOSIS` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `age` FLOAT NULL DEFAULT NULL,
  `dbp` VARCHAR(255) NULL DEFAULT NULL,
  `diagnosis_date` DATE NOT NULL,
  `heart_rate` VARCHAR(255) NULL DEFAULT NULL,
  `height` VARCHAR(255) NULL DEFAULT NULL,
  `history_of_alcohol` VARCHAR(255) NULL DEFAULT NULL,
  `history_of_diabetes` VARCHAR(255) NULL DEFAULT NULL,
  `history_of_hypertension` VARCHAR(255) NULL DEFAULT NULL,
  `history_of_smoking` VARCHAR(255) NULL DEFAULT NULL,
  `sbp` VARCHAR(255) NULL DEFAULT NULL,
  `weight` VARCHAR(255) NULL DEFAULT NULL,
  `patient_id` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK_BASELINE_DIAGNOSIS_PATIENT_INFO_Id` (`patient_id` ASC),
  CONSTRAINT `FK_BASELINE_DIAGNOSIS_PATIENT_INFO_Id`
    FOREIGN KEY (`patient_id`)
    REFERENCES `xpacs`.`PATIENT_INFO` (`patient_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `xpacs`.`CAP_MODEL`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `xpacs`.`CAP_MODEL` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `comment` VARCHAR(255) NULL DEFAULT NULL,
  `created_date` DATE NOT NULL,
  `model_data` LONGBLOB NULL DEFAULT NULL,
  `name` VARCHAR(255) NOT NULL,
  `type` VARCHAR(255) NULL DEFAULT NULL,
  `xml_data` LONGBLOB NULL DEFAULT NULL,
  `patient_id` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK_CAP_MODEL_PATIENT_INFO_Id` (`patient_id` ASC),
  CONSTRAINT `FK_CAP_MODEL_PATIENT_INFO_Id`
    FOREIGN KEY (`patient_id`)
    REFERENCES `xpacs`.`PATIENT_INFO` (`patient_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `xpacs`.`CLINICAL_NOTE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `xpacs`.`CLINICAL_NOTE` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `age` FLOAT NULL DEFAULT NULL,
  `assessment_date` DATE NOT NULL,
  `diagnosis` VARCHAR(255) NULL DEFAULT NULL,
  `height` VARCHAR(255) NULL DEFAULT NULL,
  `notes` VARCHAR(255) NOT NULL,
  `weight` VARCHAR(255) NULL DEFAULT NULL,
  `patient_id` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK_CLINICAL_NOTE_PATIENT_INFO_Id` (`patient_id` ASC),
  CONSTRAINT `FK_CLINICAL_NOTE_PATIENT_INFO_Id`
    FOREIGN KEY (`patient_id`)
    REFERENCES `xpacs`.`PATIENT_INFO` (`patient_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `xpacs`.`USER`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `xpacs`.`USER` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `enabled` BIT(1) NULL DEFAULT NULL,
  `password` VARCHAR(255) NOT NULL,
  `role` VARCHAR(255) NOT NULL,
  `username` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
