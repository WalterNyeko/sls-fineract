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
 
CREATE TABLE `external_service_campaign_api_key` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(250) NOT NULL,
  `api_key`  VARCHAR(250) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

ALTER TABLE `external_service_campaign` ADD COLUMN api_key_id BIGINT(20);

ALTER TABLE `external_service_campaign` ADD CONSTRAINT external_service_campaign_to_api_key FOREIGN KEY (api_key_id) REFERENCES external_service_campaign(id) ON DELETE SET NULL;

INSERT INTO  `m_permission`
(`grouping` ,`code` ,`entity_name`, `action_name`, `can_maker_checker`) VALUES
('organisation', 'READ_EXTERNAL_SERVICE_CAMPAIGN_API_KEY', 'EXTERNAL_SERVICE_CAMPAIGN_API_KEY', 'READ', '0'),
('organisation', 'CREATE_EXTERNAL_SERVICE_CAMPAIGN_API_KEY', 'EXTERNAL_SERVICE_CAMPAIGN_API_KEY', 'CREATE', '0'),
('organisation', 'UPDATE_EXTERNAL_SERVICE_CAMPAIGN_API_KEY', 'EXTERNAL_SERVICE_CAMPAIGN_API_KEY',  'UPDATE', '0'),
('organisation', 'DELETE_EXTERNAL_SERVICE_CAMPAIGN_API_KEY', 'EXTERNAL_SERVICE_CAMPAIGN_API_KEY', 'DELETE', '0');