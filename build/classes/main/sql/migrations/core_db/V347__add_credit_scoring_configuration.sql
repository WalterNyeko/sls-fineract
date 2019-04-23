--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements. See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership. The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License. You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied. See the License for the
-- specific language governing permissions and limitations
-- under the License.
--

-- -----------------------------------------------------
-- Table `loan_scoring_configuration`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `loan_scoring_configuration` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `loan_scoring_data_group`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `loan_scoring_data_group` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `loan_scoring_configuration_id` INT NOT NULL,
  `points` INT NOT NULL,
  PRIMARY KEY (`id`, `loan_scoring_configuration_id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  CONSTRAINT `fk_loan_scoring_data_group_loan_score_configuration`
    FOREIGN KEY (`loan_scoring_configuration_id`)
    REFERENCES `loan_scoring_configuration` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `loan_scoring_variable`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `loan_scoring_variable` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(45) NULL,
  `group` VARCHAR(45) NOT NULL,
  `value` INT NOT NULL DEFAULT 0,
  `points` INT NOT NULL DEFAULT 0,
  `loan_scoring_data_group_id` INT NOT NULL,
  PRIMARY KEY (`id`, `loan_scoring_data_group_id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_loan_scoring_variables_loan_scoring_data_group1_idx` (`loan_scoring_data_group_id` ASC),
  CONSTRAINT `fk_loan_scoring_variables_loan_scoring_data_group1`
    FOREIGN KEY (`loan_scoring_data_group_id`)
    REFERENCES `loan_scoring_data_group` (`loan_scoring_configuration_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `loan_scoring_variable_data`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `loan_scoring_variable_data` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `loan_scoring_variable_id` INT NOT NULL,
  `loan_scoring_variable_loan_scoring_data_group_id` INT NOT NULL,
  `data` VARCHAR(45) NOT NULL,
  `american` INT NOT NULL,
  `french` INT NOT NULL,
  PRIMARY KEY (`id`, `loan_scoring_variable_id`, `loan_scoring_variable_loan_scoring_data_group_id`),
  INDEX `fk_loan_scoring_variable_data_loan_scoring_variable1_idx` (`loan_scoring_variable_id` ASC, `loan_scoring_variable_loan_scoring_data_group_id` ASC),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  CONSTRAINT `fk_loan_scoring_variable_data_loan_scoring_variable1`
    FOREIGN KEY (`loan_scoring_variable_id` , `loan_scoring_variable_loan_scoring_data_group_id`)
    REFERENCES `loan_scoring_variable` (`id` , `loan_scoring_data_group_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `loan_scoring_qualification`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `loan_scoring_qualification` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `min` INT NOT NULL,
  `max` INT NOT NULL,
  `qualification` VARCHAR(45) NOT NULL,
  `loan_scoring_configuration_id` INT NOT NULL,
  PRIMARY KEY (`id`, `loan_scoring_configuration_id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_loan_scoring_result_loan_score_configuration1_idx` (`loan_scoring_configuration_id` ASC),
  CONSTRAINT `fk_loan_scoring_result_loan_score_configuration1`
    FOREIGN KEY (`loan_scoring_configuration_id`)
    REFERENCES `loan_scoring_configuration` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `loan_scoring_variable_group`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `loan_scoring_variable_group` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `points` INT NOT NULL,
  `variables` VARCHAR(45) NOT NULL,
  `loan_scoring_configuration_id` INT NOT NULL,
  PRIMARY KEY (`id`, `loan_scoring_configuration_id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC),
  INDEX `fk_loan_scoring_variable_group_loan_scoring_configuration1_idx` (`loan_scoring_configuration_id` ASC),
  CONSTRAINT `fk_loan_scoring_variable_group_loan_scoring_configuration1`
    FOREIGN KEY (`loan_scoring_configuration_id`)
    REFERENCES `loan_scoring_configuration` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;