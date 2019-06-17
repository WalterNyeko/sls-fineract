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

import org.apache.fineract.infrastructure.campaigns.email.data.EmailBusinessRulesData;
import org.apache.fineract.infrastructure.campaigns.externalservice.domain.ExternalServiceCampaign;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.savings.data.SavingsProductData;
import org.joda.time.LocalDate;

import java.util.Collection;

public class ExternalServiceCampaignData {

	private Long id;
	private String campaignName;
	private Long businessRuleId;
	private String businessRuleName;
	private String url;
	private String payload;
	private LocalDate specificExecutionDate;
	private Long loanProductId;
	private String loanProductName;
	private Long savingsProductId;
	private String savingsProductName;
	private Collection<LoanProductData> loanProducts;
	private Collection<SavingsProductData> savingsProducts;
	private Collection<EmailBusinessRulesData> businessRulesOptions;

	public ExternalServiceCampaignData() {
	}

	public ExternalServiceCampaignData(ExternalServiceCampaign campaign) {
		this.id = campaign.getId();
		this.url = campaign.getUrl();
		this.payload = campaign.getPayload();
		this.campaignName = campaign.getCampaignName();
		this.businessRuleId = campaign.getBusinessRule().getId();
		this.businessRuleName = campaign.getBusinessRule().getReportName();
		if (campaign.getSpecificExecutionDate() != null) {
			this.specificExecutionDate = LocalDate.fromDateFields(campaign.getSpecificExecutionDate());
		}
		if (campaign.getLoanProduct() != null) {
			this.loanProductId = campaign.getLoanProduct().getId();
			this.loanProductName = campaign.getLoanProduct().productName();
		}
		if (campaign.getSavingsProduct() != null) {
			this.savingsProductId = campaign.getSavingsProduct().getId();
			this.savingsProductName = campaign.getSavingsProduct().getName();
		}
	}

	public void setBusinessRulesOptions(Collection<EmailBusinessRulesData> businessRulesOptions) {
		this.businessRulesOptions = businessRulesOptions;
	}

	public void setLoanProducts(Collection<LoanProductData> loanProducts) {
		this.loanProducts = loanProducts;
	}

	public void setSavingsProducts(Collection<SavingsProductData> savingsProducts) {
		this.savingsProducts = savingsProducts;
	}
}
