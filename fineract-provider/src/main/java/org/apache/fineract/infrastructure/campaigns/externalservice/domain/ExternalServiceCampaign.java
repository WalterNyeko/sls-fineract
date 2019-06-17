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
import org.apache.fineract.infrastructure.dataqueries.domain.Report;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.savings.domain.SavingsProduct;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "external_service_campaign")
public class ExternalServiceCampaign extends AbstractPersistableCustom<Long> {

	@Column(name = "campaign_name", nullable = false)
	private String campaignName;

	@ManyToOne
	@JoinColumn(name = "business_rule_id")
	private Report businessRule;

	@Column(name = "external_service_url", nullable = false)
	private String url;

	@Column(name = "external_service_payload", nullable = false)
	private String payload;

	@Column(name = "specific_execution_date")
	private Date specificExecutionDate;

	@ManyToOne
	@JoinColumn(name = "loan_product_id")
	private LoanProduct loanProduct;

	@ManyToOne
	@JoinColumn(name = "savings_product_id")
	private SavingsProduct savingsProduct;

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public Report getBusinessRule() {
		return businessRule;
	}

	public void setBusinessRule(Report businessRule) {
		this.businessRule = businessRule;
	}

	public LoanProduct getLoanProduct() {
		return loanProduct;
	}

	public void setLoanProduct(LoanProduct loanProduct) {
		this.loanProduct = loanProduct;
	}

	public SavingsProduct getSavingsProduct() {
		return savingsProduct;
	}

	public void setSavingsProduct(SavingsProduct savingsProduct) {
		this.savingsProduct = savingsProduct;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public Date getSpecificExecutionDate() {
		return specificExecutionDate;
	}

	public void setSpecificExecutionDate(Date specificExecutionDate) {
		this.specificExecutionDate = specificExecutionDate;
	}
}
