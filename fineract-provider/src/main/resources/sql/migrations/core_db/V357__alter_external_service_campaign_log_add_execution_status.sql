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

ALTER TABLE `external_service_campaign_log` ADD COLUMN execution_status VARCHAR (100) NOT NULL DEFAULT 'Completed' AFTER savings_account_transaction_id;

UPDATE `external_service_campaign_log` SET execution_status = 'Failed' WHERE api_response_status != 200;

UPDATE `external_service_campaign_log` AS l, (SELECT client_id, id FROM m_loan) src
SET l.client_id = src.client_id
WHERE l.loan_id = src.id;

UPDATE `external_service_campaign_log` AS l, (SELECT client_id, id FROM m_savings_account) src
SET l.client_id = src.client_id
WHERE l.savings_account_id = src.id;

UPDATE `external_service_campaign_log` AS l, (SELECT ln.client_id, t.id FROM m_loan_transaction t INNER JOIN m_loan ln ON t.loan_id = ln.id) src
SET l.client_id = src.client_id
WHERE l.loan_transaction_id = src.id;

UPDATE `external_service_campaign_log` AS l, (SELECT s.client_id, t.id FROM m_savings_account_transaction t INNER JOIN m_savings_account s ON t.savings_account_id = s.id) src
SET l.client_id = src.client_id
WHERE l.savings_account_transaction_id = src.id;