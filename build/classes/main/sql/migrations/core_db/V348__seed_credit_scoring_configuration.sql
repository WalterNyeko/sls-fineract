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
-- Data for table `loan_scoring_configuration`
-- -----------------------------------------------------
INSERT INTO `loan_scoring_configuration` (`id`, `name`) VALUES (1, 'default');


-- -----------------------------------------------------
-- Data for table `loan_scoring_data_group`
-- -----------------------------------------------------
INSERT INTO `loan_scoring_data_group` (`id`, `name`, `loan_scoring_configuration_id`, `points`) VALUES (1, 'Burrower', 1, 300);
INSERT INTO `loan_scoring_data_group` (`id`, `name`, `loan_scoring_configuration_id`, `points`) VALUES (2, 'Loan', 1, 350);
INSERT INTO `loan_scoring_data_group` (`id`, `name`, `loan_scoring_configuration_id`, `points`) VALUES (3, 'Collateral', 1, 350);


-- -----------------------------------------------------
-- Data for table `loan_scoring_variable`
-- -----------------------------------------------------
INSERT INTO `loan_scoring_variable` (`id`, `name`, `description`, `group`, `value`, `points`, `loan_scoring_data_group_id`) VALUES (1, 'AGE', 'Age of the borrower', 'Profile', 0, 70, 1);
INSERT INTO `loan_scoring_variable` (`id`, `name`, `description`, `group`, `value`, `points`, `loan_scoring_data_group_id`) VALUES (2, 'EMPLOYMENT', 'Type of the current employment', 'Profile', 0, 70, 1);
INSERT INTO `loan_scoring_variable` (`id`, `name`, `description`, `group`, `value`, `points`, `loan_scoring_data_group_id`) VALUES (3, 'DTI RATIO', 'Indebtedness ratio', 'Profile', 0, 20, 1);
INSERT INTO `loan_scoring_variable` (`id`, `name`, `description`, `group`, `value`, `points`, `loan_scoring_data_group_id`) VALUES (4, 'PAYMENT INCIDENTS', 'Kind of payment incidents that the borrower has had', 'Profile', 0, 20, 1);
INSERT INTO `loan_scoring_variable` (`id`, `name`, `description`, `group`, `value`, `points`, `loan_scoring_data_group_id`) VALUES (5, 'MONTHLY INCOME', 'Amount of the monthly income', 'Profile', 0, 70, 1);
INSERT INTO `loan_scoring_variable` (`id`, `name`, `description`, `group`, `value`, `points`, `loan_scoring_data_group_id`) VALUES (6, 'ASSET THAT CAN BE LIQUIDATED', 'Is there any asset that can be liquidated?', 'Extra (Optional)', 0, 25, 1);


-- -----------------------------------------------------
-- Data for table `loan_scoring_qualification`
-- -----------------------------------------------------
INSERT INTO `loan_scoring_qualification` (`id`, `min`, `max`, `qualification`, `loan_scoring_configuration_id`) VALUES (1, 0, 330, 'N/A', 1);
INSERT INTO `loan_scoring_qualification` (`id`, `min`, `max`, `qualification`, `loan_scoring_configuration_id`) VALUES (2, 331, 450, 'B', 1);
INSERT INTO `loan_scoring_qualification` (`id`, `min`, `max`, `qualification`, `loan_scoring_configuration_id`) VALUES (3, 451, 500, 'BB', 1);
INSERT INTO `loan_scoring_qualification` (`id`, `min`, `max`, `qualification`, `loan_scoring_configuration_id`) VALUES (4, 501, 550, 'BBB', 1);
INSERT INTO `loan_scoring_qualification` (`id`, `min`, `max`, `qualification`, `loan_scoring_configuration_id`) VALUES (5, 551, 700, 'A', 1);
INSERT INTO `loan_scoring_qualification` (`id`, `min`, `max`, `qualification`, `loan_scoring_configuration_id`) VALUES (6, 701, 850, 'AA', 1);
INSERT INTO `loan_scoring_qualification` (`id`, `min`, `max`, `qualification`, `loan_scoring_configuration_id`) VALUES (7, 851, 1000, 'AAAA', 1);


-- -----------------------------------------------------
-- Data for table `loan_scoring_variable_group`
-- -----------------------------------------------------
INSERT INTO `loan_scoring_variable_group` (`id`, `name`, `points`, `variables`, `loan_scoring_configuration_id`) VALUES (1, 'Profile', 360, '', 1);
INSERT INTO `loan_scoring_variable_group` (`id`, `name`, `points`, `variables`, `loan_scoring_configuration_id`) VALUES (2, 'Return', 150, '', 1);
INSERT INTO `loan_scoring_variable_group` (`id`, `name`, `points`, `variables`, `loan_scoring_configuration_id`) VALUES (3, 'Risk', 200, '', 1);
INSERT INTO `loan_scoring_variable_group` (`id`, `name`, `points`, `variables`, `loan_scoring_configuration_id`) VALUES (4, 'Liquidity', 290, '', 1);