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

ALTER TABLE m_fund ADD COLUMN mintos_api_token VARCHAR (100);

UPDATE m_fund SET mintos_api_token = 'e2331896e55daffbcb57af77c0578ceb928742b8441f0617b9d6e7d407179a8b'
WHERE name = 'FireOF DM';

UPDATE m_fund SET mintos_api_token = '9fb73c141ed7162b65dea54d7646afa5ea318c43a139065b32277cfc3597c68e'
WHERE name = 'Collateral Investment';

ALTER TABLE Mintos ADD COLUMN loan_creation_response TEXT;