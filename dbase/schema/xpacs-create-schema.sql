-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema xpacs
-- -----------------------------------------------------
-- Xtra PACS database

-- -----------------------------------------------------
-- Schema xpacs
--
-- Xtra PACS database
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `xpacs` DEFAULT CHARACTER SET utf8 ;
USE `xpacs` ;

-- -----------------------------------------------------
-- Table `xpacs`.`PATIENT_HISTORY`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `xpacs`.`PATIENT_HISTORY` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `PACS_patient_id` VARCHAR(250) NULL,
  `date` DATETIME NULL,
  `next_id` BIGINT(20) NULL,
  `prev_id` BIGINT(20) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_PATIENT_HISTORY_PATIENT_HISTORY1_idx` (`next_id` ASC),
  INDEX `fk_PATIENT_HISTORY_PATIENT_HISTORY2_idx` (`prev_id` ASC),
  CONSTRAINT `fk_PATIENT_HISTORY_PATIENT_HISTORY1`
    FOREIGN KEY (`next_id`)
    REFERENCES `xpacs`.`PATIENT_HISTORY` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_PATIENT_HISTORY_PATIENT_HISTORY2`
    FOREIGN KEY (`prev_id`)
    REFERENCES `xpacs`.`PATIENT_HISTORY` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `xpacs`.`STUDY_PACS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `xpacs`.`STUDY_PACS` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `PACS_study_iuid` VARCHAR(250) NOT NULL,
  `PACS_patient_id` VARCHAR(250) NOT NULL,
  `start_history_id` BIGINT(20) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_STUDY_PACS_PATIENT_HISTORY1_idx` (`start_history_id` ASC),
  CONSTRAINT `fk_STUDY_PACS_PATIENT_HISTORY1`
    FOREIGN KEY (`start_history_id`)
    REFERENCES `xpacs`.`PATIENT_HISTORY` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `xpacs`.`CAP_MODEL`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `xpacs`.`CAP_MODEL` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `comment` VARCHAR(2500) NULL,
  `metadata` LONGBLOB NULL,
  `xml` LONGBLOB NULL,
  `STUDY_PACS_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`, `STUDY_PACS_id`),
  INDEX `fk_CAP_MODEL_STUDY_PACS1_idx` (`STUDY_PACS_id` ASC),
  CONSTRAINT `fk_CAP_MODEL_STUDY_PACS1`
    FOREIGN KEY (`STUDY_PACS_id`)
    REFERENCES `xpacs`.`STUDY_PACS` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `xpacs`.`AUX_FILE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `xpacs`.`AUX_FILE` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `URI` VARCHAR(255) NULL,
  `filename` VARCHAR(255) NULL,
  `descriptor` VARCHAR(255) NULL,
  `PATIENT_HISTORY_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`, `PATIENT_HISTORY_id`),
  INDEX `fk_AUX_FILE_PATIENT_HISTORY1_idx` (`PATIENT_HISTORY_id` ASC),
  CONSTRAINT `fk_AUX_FILE_PATIENT_HISTORY1`
    FOREIGN KEY (`PATIENT_HISTORY_id`)
    REFERENCES `xpacs`.`PATIENT_HISTORY` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `xpacs`.`CLINICAL_NOTE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `xpacs`.`CLINICAL_NOTE` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `age` FLOAT NULL,
  `weight` FLOAT NULL,
  `height` FLOAT NULL,
  `diagnosis` VARCHAR(255) NULL,
  `procedure` VARCHAR(255) NULL,
  `notes` VARCHAR(2500) NULL,
  `PATIENT_HISTORY_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`, `PATIENT_HISTORY_id`),
  INDEX `fk_CLINICAL_NOTE_PATIENT_HISTORY1_idx` (`PATIENT_HISTORY_id` ASC),
  CONSTRAINT `fk_CLINICAL_NOTE_PATIENT_HISTORY1`
    FOREIGN KEY (`PATIENT_HISTORY_id`)
    REFERENCES `xpacs`.`PATIENT_HISTORY` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
