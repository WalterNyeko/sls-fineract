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

INSERT INTO m_provision_category(category_name, description) VALUES('PASS AND WATCH', 'Pass and watch');
ALTER TABLE m_provision_category ADD COLUMN ordinal int(100) default 1;
UPDATE m_provision_category SET ordinal=2 WHERE id=5;
UPDATE m_provision_category SET ordinal=3 WHERE id=2;
UPDATE m_provision_category SET ordinal=4 WHERE id=3;
UPDATE m_provision_category SET ordinal=5 WHERE id=4;

DELETE FROM m_savings_product WHERE name = 'SLS Save';
