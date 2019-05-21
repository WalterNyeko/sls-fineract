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

DROP TABLE IF EXISTS `nuban_account_pool`;

CREATE TABLE IF NOT EXISTS `nuban_account_pool` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `nuban_account_number` CHAR(10) NOT NULL,
  `available` TINYINT(1) NOT NULL DEFAULT '1',
  `savings_account_number` VARCHAR (20) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `nuban_account_number_UNIQUE` (`nuban_account_number` ASC),
  UNIQUE INDEX `savings_account_number_UNIQUE` (`savings_account_number` ASC)
)ENGINE = InnoDB;

UPDATE m_savings_account SET `nuban_account_number` = NULL;

