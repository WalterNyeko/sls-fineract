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

package org.apache.fineract.infrastructure.campaigns.externalservice.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "external_service_campaign_log")
public class ExternalServiceCampaignLog extends AbstractPersistableCustom<Long> {

	@ManyToOne
	@JoinColumn(name = "external_service_campaign_id", nullable = false)
	private ExternalServiceCampaign externalServiceCampaign;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "loan_id")
	private Loan loan;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "savings_account_id")
	private SavingsAccount savingsAccount;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "loan_transaction_id")
	private LoanTransaction loanTransaction;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "savings_account_transaction_id")
	private SavingsAccountTransaction savingsAccountTransaction;

	@Column(name = "execution_status", nullable = false)
	private String executionStatus;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "client_id")
	private Client client;

	@Column(name = "api_response_status", nullable = false)
	private Integer apiResponseStatus;

	@Column(name = "api_response")
	private String apiResponse;

	@Column(name = "api_response_error")
	private String apiResponseError;

	@Column(name = "number_of_tries")
	private Integer numberOfTries;

	@Column(name = "execution_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date executionTime;

	public ExternalServiceCampaign getExternalServiceCampaign() {
		return externalServiceCampaign;
	}

	public void setExternalServiceCampaign(ExternalServiceCampaign externalServiceCampaign) {
		this.externalServiceCampaign = externalServiceCampaign;
	}

	public Loan getLoan() {
		return loan;
	}

	public void setLoan(Loan loan) {
		this.loan = loan;
	}

	public SavingsAccount getSavingsAccount() {
		return savingsAccount;
	}

	public LoanTransaction getLoanTransaction() {
		return loanTransaction;
	}

	public void setLoanTransaction(LoanTransaction loanTransaction) {
		this.loanTransaction = loanTransaction;
	}

	public SavingsAccountTransaction getSavingsAccountTransaction() {
		return savingsAccountTransaction;
	}

	public void setSavingsAccountTransaction(SavingsAccountTransaction savingsAccountTransaction) {
		this.savingsAccountTransaction = savingsAccountTransaction;
	}

	public void setSavingsAccount(SavingsAccount savingsAccount) {
		this.savingsAccount = savingsAccount;
	}

	public Integer getApiResponseStatus() {
		return apiResponseStatus;
	}

	public void setApiResponseStatus(Integer apiResponseStatus) {
		this.apiResponseStatus = apiResponseStatus;
	}

	public String getApiResponse() {
		return apiResponse;
	}

	public void setApiResponse(String apiResponse) {
		this.apiResponse = apiResponse;
	}

	public String getApiResponseError() {
		return apiResponseError;
	}

	public void setApiResponseError(String apiResponseError) {
		this.apiResponseError = apiResponseError;
	}

	public Integer getNumberOfTries() {
		return numberOfTries;
	}

	public void setNumberOfTries(Integer numberOfTries) {
		this.numberOfTries = numberOfTries;
	}

	public Date getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(Date executionTime) {
		this.executionTime = executionTime;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(String executionStatus) {
		this.executionStatus = executionStatus;
	}
}
