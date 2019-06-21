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
 
UPDATE `stretchy_report` SET `report_sql` = 'SELECT loan.`id` as loanId, p.`name` as loan_product_name, loan.`loan_officer_id` as officerId,loan.`principal_amount_proposed` as loanAmount,loan.`submittedon_date`,loan.`approvedon_date`, loan.`disbursedon_date`, c.display_name as clientDisplayName, c.firstname as clientFirstName, c.`email_address`, c.`mobile_no`, loan.annual_nominal_interest_rate as annualInterestRate, s.firstname as officerFirstname, s.display_name as officerDisplayName FROM `m_loan` loan join m_client c on c.id = loan.client_id join m_product_loan p ON loan.product_id = p.id left join m_staff s on s.id = loan.loan_officer_id where loan.id = ${loanId} or ${loanId} = -1'
WHERE `id` = 188;

UPDATE `stretchy_report` SET `report_sql` = 'SELECT loan.`id` as loanId, p.`name` as loan_product_name, loan.`loan_officer_id` as officerId,loan.`principal_amount_proposed` as loanAmount,loan.`submittedon_date`,loan.`approvedon_date`, loan.`disbursedon_date`, c.display_name as clientDisplayName, c.firstname as clientFirstName, c.`email_address`, c.`mobile_no`, loan.annual_nominal_interest_rate as annualInterestRate, s.firstname as officerFirstname, s.display_name as officerDisplayName FROM `m_loan` loan join m_client c on c.id = loan.client_id join m_product_loan p ON loan.product_id = p.id left join m_staff s on s.id = loan.loan_officer_id where loan.id = ${loanId} or ${loanId} = -1'
WHERE `id` = 189;

UPDATE `stretchy_report` SET `report_sql` = 'SELECT loan.`id` as loanId, p.`name` as loan_product_name, loan.`loan_officer_id` as officerId,loan.`principal_amount_proposed` as loanAmount,loan.`submittedon_date`,loan.`approvedon_date`, loan.`disbursedon_date`, c.display_name as clientDisplayName, c.firstname as clientFirstName, c.`email_address`, c.`mobile_no`, loan.annual_nominal_interest_rate as annualInterestRate, s.firstname as officerFirstname, s.display_name as officerDisplayName FROM `m_loan` loan join m_client c on c.id = loan.client_id join m_product_loan p ON loan.product_id = p.id left join m_staff s on s.id = loan.loan_officer_id where loan.id = ${loanId} or ${loanId} = -1'
WHERE `id` = 190;

UPDATE `stretchy_report` SET `report_sql` = 'SELECT account.`id` as accountId, p.`name` as savings_product_name,account.`account_no` as accountNumber,account.`nuban_account_number` as nubanAccountNumber, account.`submittedon_date`, c.display_name as clientDisplayName, c.firstname as clientFirstName, c.`email_address`, c.`mobile_no` FROM `m_savings_account` account join m_client c on c.id = account.client_id join m_savings_product p ON account.product_id = p.id where account.id = ${savingsId} or ${savingsId} = -1'
WHERE `id` = 191;

UPDATE `stretchy_report` SET `report_sql` = 'SELECT transaction.`id` as transactionId, p.`name` as savings_product_name,account.`account_no` as accountNumber,account.`nuban_account_number` as nubanAccountNumber, transaction.`amount` as depositAmount, transaction.`transaction_date` as transactionDate, transaction.`running_balance_derived` as accountBalance, c.display_name as clientDisplayName, c.firstname as clientFirstName, c.`email_address`, c.`mobile_no` FROM `m_savings_account` account join m_client c on c.id = account.client_id join m_savings_account_transaction `transaction` on account.id = transaction.savings_account_id join m_savings_product p ON account.product_id = p.id where transaction.id = ${savingsTransactionId} or ${savingsTransactionId} = -1'
WHERE `id` = 192;

UPDATE `stretchy_report` SET `report_sql` = 'SELECT transaction.`id` as transactionId, p.`name` as savings_product_name, account.`account_no` as accountNumber,account.`nuban_account_number` as nubanAccountNumber, transaction.`amount` as withdrawalAmount, transaction.`transaction_date` as transactionDate, transaction.`running_balance_derived` as accountBalance, c.display_name as clientDisplayName, c.firstname as clientFirstName, c.`email_address`, c.`mobile_no` FROM `m_savings_account` account join m_client c on c.id = account.client_id join m_savings_account_transaction `transaction` on account.id = transaction.savings_account_id join m_savings_product p ON account.product_id = p.id where transaction.id = ${savingsTransactionId} or ${savingsTransactionId} = -1'
WHERE `id` = 193;

UPDATE `stretchy_report` SET `report_sql` = 'SELECT loan.`id` as loanId, p.`name` as loan_product_name, transaction.`amount` as transactionAmount, transaction.`transaction_date` as transactionDate, transaction.`outstanding_loan_balance_derived` as outstandingBalance, c.display_name as clientDisplayName, c.firstname as clientFirstName, c.`email_address`, c.`mobile_no` FROM `m_loan` loan join m_client c on c.id = loan.client_id join m_loan_transaction transaction on loan.id = transaction.loan_id join m_product_loan p ON loan.product_id = p.id where transaction.id = ${transactionId} or ${transactionId} = -1'
WHERE `id` = 194;

UPDATE `stretchy_report` SET `report_sql` = 'SELECT c.display_name as clientDisplayName, c.firstname as clientFirstName, c.date_of_birth as dateOfBirth, c.`email_address`, c.`mobile_no` FROM m_client c where c.id = ${clientId} or ${clientId} = -1'
WHERE `id` = 195;

UPDATE `stretchy_report` SET `report_sql` = 'SELECT c.display_name as clientDisplayName, c.firstname as clientFirstName, c.date_of_birth as dateOfBirth, c.`email_address`, c.`mobile_no` FROM m_client c where c.id = ${clientId} or ${clientId} = -1'
WHERE `id` = 196;