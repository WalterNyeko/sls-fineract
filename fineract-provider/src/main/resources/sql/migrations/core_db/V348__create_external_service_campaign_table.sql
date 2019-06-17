/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

CREATE TABLE IF NOT EXISTS `external_service_campaign` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `campaign_name` VARCHAR(100) NOT NULL,
  `business_rule_id` INT(11),
  `external_service_url` VARCHAR (250) NOT NULL,
  `external_service_payload` VARCHAR (250) NOT NULL,
  `specific_execution_date` DATE,
  `loan_product_id` BIGINT(20),
  `savings_product_id` BIGINT(20),
  PRIMARY KEY (`id`),
  KEY `business_rule_id` (`business_rule_id`),
  CONSTRAINT `external_service_campaign_stretchy_report` FOREIGN KEY (`business_rule_id`) REFERENCES `stretchy_report` (`id`),
  CONSTRAINT `external_service_campaign_loan_product` FOREIGN KEY (`loan_product_id`) REFERENCES `m_product_loan` (`id`),
  CONSTRAINT `external_service_campaign_savings_product` FOREIGN KEY (`savings_product_id`) REFERENCES `m_savings_product` (`id`)
)ENGINE = InnoDB;

INSERT INTO  `m_permission`
(`grouping` ,`code` ,`entity_name`, `action_name`, `can_maker_checker`) VALUES
('organisation', 'READ_EXTERNAL_SERVICE_CAMPAIGN', 'EXTERNAL_SERVICE_CAMPAIGN', 'READ', '0'),
('organisation', 'CREATE_EXTERNAL_SERVICE_CAMPAIGN', 'EXTERNAL_SERVICE_CAMPAIGN', 'CREATE', '0'),
('organisation', 'UPDATE_EXTERNAL_SERVICE_CAMPAIGN', 'EXTERNAL_SERVICE_CAMPAIGN',  'UPDATE', '0'),
('organisation', 'DELETE_EXTERNAL_SERVICE_CAMPAIGN', 'EXTERNAL_SERVICE_CAMPAIGN', 'DELETE', '0');