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

ALTER TABLE `ADDITIONAL INFORMATION` DROP COLUMN `Account Name`;

CREATE TABLE `external_service_campaign_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `external_service_campaign_id` bigint(20) NOT NULL,
  `loan_id`  bigint(20),
  `savings_account_id`  bigint(20),
  `loan_transaction_id`  bigint(20),
  `savings_account_transaction_id`  bigint(20),
  `api_response_status` int(11) NOT NULL,
  `api_response` TEXT,
  `api_response_error` varchar(250),
  `number_of_tries` int(11),
  `execution_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `external_service_campaign_id` (`external_service_campaign_id`),
  KEY `external_service_campaign_log_loan_id` (`loan_id`),
  KEY `external_service_campaign_log_savings_account_id` (`savings_account_id`),
  CONSTRAINT `external_service_campaign_log_external_service_campaign` FOREIGN KEY (`external_service_campaign_id`) REFERENCES `external_service_campaign` (`id`),
  CONSTRAINT `external_service_campaign_log_m_loan` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`),
  CONSTRAINT `external_service_campaign_log_m_savings_account` FOREIGN KEY (`savings_account_id`) REFERENCES `m_savings_account` (`id`),
  CONSTRAINT `external_service_campaign_log_m_loan_transaction` FOREIGN KEY (`loan_transaction_id`) REFERENCES `m_loan_transaction` (`id`),
  CONSTRAINT `external_service_campaign_log_m_savings_account_transaction` FOREIGN KEY (`savings_account_transaction_id`) REFERENCES `m_savings_account_transaction` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

INSERT INTO `c_configuration` (`id`, `name`, `value`, `enabled`, `description`) VALUES (32, 'external-service-campaign-max-retries', 3, 1, 'Maximum retries for failed API calls to external services');

INSERT INTO `c_configuration` (`id`, `name`, `value`, `enabled`, `description`) VALUES (33, 'external-service-campaign-retry-delay', 180000, 1, 'Amount of time (in milliseconds) to delay the next API call after timeout');