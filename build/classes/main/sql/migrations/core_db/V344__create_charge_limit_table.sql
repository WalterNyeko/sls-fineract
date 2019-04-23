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

CREATE TABLE m_charge_limit(
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  charge_id BIGINT(20) NOT NULL,
  min DOUBLE NULL DEFAULT NULL,
  max DOUBLE NULL DEFAULT NULL,
  charge_calculation_enum SMALLINT(5) NOT NULL,
  amount DOUBLE NOT NULL,
  PRIMARY KEY(id),
  CONSTRAINT m_charge_limit__charge_id FOREIGN KEY(charge_id) REFERENCES m_charge(id) ON DELETE CASCADE ON UPDATE CASCADE
);

ALTER TABLE m_charge MODIFY COLUMN amount DECIMAL(19) NULL DEFAULT NULL;