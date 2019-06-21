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

package org.apache.fineract.infrastructure.campaigns.externalservice.data;

import org.joda.time.LocalDate;

public class SavingsAccountReportData {

	private Long savingsAccountId;
	private String accountNumber;
	private String nubanAccountNumber;
	private LocalDate submittedOnDate;
	private String clientDisplayName;
	private String clientFirstName;
	private String clientEmail;
	private String clientPhoneNumber;
	private String savingsProductName;

	public Long getSavingsAccountId() {
		return savingsAccountId;
	}

	public void setSavingsAccountId(Long savingsAccountId) {
		this.savingsAccountId = savingsAccountId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getNubanAccountNumber() {
		return nubanAccountNumber;
	}

	public void setNubanAccountNumber(String nubanAccountNumber) {
		this.nubanAccountNumber = nubanAccountNumber;
	}

	public LocalDate getSubmittedOnDate() {
		return submittedOnDate;
	}

	public void setSubmittedOnDate(LocalDate submittedOnDate) {
		this.submittedOnDate = submittedOnDate;
	}

	public String getClientDisplayName() {
		return clientDisplayName;
	}

	public void setClientDisplayName(String clientDisplayName) {
		this.clientDisplayName = clientDisplayName;
	}

	public String getClientFirstName() {
		return clientFirstName;
	}

	public void setClientFirstName(String clientFirstName) {
		this.clientFirstName = clientFirstName;
	}

	public String getClientEmail() {
		return clientEmail;
	}

	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}

	public String getClientPhoneNumber() {
		return clientPhoneNumber;
	}

	public void setClientPhoneNumber(String clientPhoneNumber) {
		this.clientPhoneNumber = clientPhoneNumber;
	}

	public String getSavingsProductName() {
		return savingsProductName;
	}

	public void setSavingsProductName(String savingsProductName) {
		this.savingsProductName = savingsProductName;
	}
}
