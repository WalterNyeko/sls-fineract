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

import org.apache.fineract.infrastructure.campaigns.externalservice.domain.ExternalServiceCampaignLog;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

public class ExternalServiceCampaignLogData {

	private String clientDisplayName;
	private String campaignName;
	private String businessRule;
	private String campaignUrl;
	private String loanAccountNumber;
	private String savingsAccountNumber;
	private Long transactionId;
	private Integer apiResponseStatus;
	private String executionStatus;
	private String apiResponse;
	private String apiResponseError;
	private Integer numberOfTries;
	private LocalDateTime executionTime;

	public ExternalServiceCampaignLogData(ExternalServiceCampaignLog externalServiceCampaignLog) {
		this.setProperties(externalServiceCampaignLog);
	}

	private void setProperties(ExternalServiceCampaignLog serviceCampaignLog) {
		this.apiResponse = serviceCampaignLog.getApiResponse();
		this.numberOfTries = serviceCampaignLog.getNumberOfTries();
		this.executionStatus = serviceCampaignLog.getExecutionStatus();
		this.apiResponseError = serviceCampaignLog.getApiResponseError();
		this.apiResponseStatus = serviceCampaignLog.getApiResponseStatus();
		this.campaignUrl = serviceCampaignLog.getExternalServiceCampaign().getUrl();
		this.campaignName = serviceCampaignLog.getExternalServiceCampaign().getCampaignName();
		this.businessRule = serviceCampaignLog.getExternalServiceCampaign().getBusinessRule().getReportName();

		if (serviceCampaignLog.getClient() != null) {
			this.clientDisplayName = serviceCampaignLog.getClient().getDisplayName();
		}
		if (serviceCampaignLog.getLoan() != null) {
			this.loanAccountNumber = serviceCampaignLog.getLoan().getAccountNumber();
		}
		if (serviceCampaignLog.getSavingsAccount() != null) {
			this.savingsAccountNumber = serviceCampaignLog.getSavingsAccount().getNubanAccountNumber();
		}
		if (serviceCampaignLog.getLoanTransaction() != null) {
			this.transactionId = serviceCampaignLog.getLoanTransaction().getId();
			this.loanAccountNumber = serviceCampaignLog.getLoanTransaction().getLoan().getAccountNumber();
		}
		if (serviceCampaignLog.getSavingsAccountTransaction() != null) {
			this.transactionId = serviceCampaignLog.getSavingsAccountTransaction().getId();
			this.savingsAccountNumber = serviceCampaignLog.getSavingsAccountTransaction().getSavingsAccount().getNubanAccountNumber();
		}
		this.executionTime = LocalDateTime.fromDateFields(serviceCampaignLog.getExecutionTime());
	}
}
