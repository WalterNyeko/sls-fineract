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

package org.apache.fineract.infrastructure.campaigns.externalservice.service;

import org.apache.fineract.infrastructure.campaigns.constants.CampaignType;
import org.apache.fineract.infrastructure.campaigns.email.data.EmailBusinessRulesData;
import org.apache.fineract.infrastructure.campaigns.email.domain.ExternalServiceCampaignRepository;
import org.apache.fineract.infrastructure.campaigns.email.service.EmailCampaignReadPlatformService;
import org.apache.fineract.infrastructure.campaigns.email.service.EmailReadPlatformService;
import org.apache.fineract.infrastructure.campaigns.externalservice.data.ExternalServiceCampaignData;
import org.apache.fineract.infrastructure.campaigns.externalservice.domain.ExternalServiceCampaign;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsProductData;
import org.apache.fineract.portfolio.savings.domain.SavingsProduct;
import org.apache.fineract.portfolio.savings.service.SavingsProductReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExternalServiceCampaignReadPlatformServiceImpl implements ExternalServiceCampaignReadPlatformService {

	private LoanProductReadPlatformService loanProductReadPlatformService;
	private SavingsProductReadPlatformService savingsProductReadPlatformService;
	private EmailCampaignReadPlatformService emailCampaignReadPlatformService;
	private ExternalServiceCampaignRepository externalServiceCampaignRepository;

	@Autowired
	public ExternalServiceCampaignReadPlatformServiceImpl(LoanProductReadPlatformService loanProductReadPlatformService,
														  SavingsProductReadPlatformService savingsProductReadPlatformService,
														  EmailCampaignReadPlatformService emailCampaignReadPlatformService,
														  ExternalServiceCampaignRepository externalServiceCampaignRepository) {
		this.loanProductReadPlatformService = loanProductReadPlatformService;
		this.savingsProductReadPlatformService = savingsProductReadPlatformService;
		this.emailCampaignReadPlatformService = emailCampaignReadPlatformService;
		this.externalServiceCampaignRepository = externalServiceCampaignRepository;
	}

	@Override
	public List<ExternalServiceCampaignData> retrieveAll() {
		List<ExternalServiceCampaign> campaigns = this.externalServiceCampaignRepository.findAll();
		return campaigns.stream().map(c -> new ExternalServiceCampaignData(c)).collect(Collectors.toList());
	}

	@Override
	public ExternalServiceCampaignData retrieveOne(Long id) {
		return new ExternalServiceCampaignData(this.externalServiceCampaignRepository.findOne(id));
	}

	@Override
	public ExternalServiceCampaignData retrieveTemplate() {
		ExternalServiceCampaignData campaign = new ExternalServiceCampaignData();
		this.appendTemplate(campaign);
		return campaign;
	}

	private void appendTemplate(ExternalServiceCampaignData externalServiceCampaign) {
		final String searchType = "API";
		Collection<SavingsProductData> savingsProducts = this.savingsProductReadPlatformService.retrieveAll();
		Collection<LoanProductData> loanProducts = this.loanProductReadPlatformService.retrieveAllLoanProducts();
		Collection<EmailBusinessRulesData> businessRules = this.emailCampaignReadPlatformService.retrieveAllBySearchType(searchType);
		externalServiceCampaign.setLoanProducts(loanProducts);
		externalServiceCampaign.setSavingsProducts(savingsProducts);
		externalServiceCampaign.setBusinessRulesOptions(businessRules);
	}

	@Override
	public ExternalServiceCampaignData retrieveWithTemplate(Long id) {
		ExternalServiceCampaignData campaign = this.retrieveOne(id);
		this.appendTemplate(campaign);
		return campaign;
	}
}
