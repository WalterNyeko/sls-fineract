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

import com.google.gson.JsonElement;
import org.apache.fineract.infrastructure.campaigns.email.domain.ExternalServiceCampaignRepository;
import org.apache.fineract.infrastructure.campaigns.externalservice.data.ExternalServiceCampaignValidator;
import org.apache.fineract.infrastructure.campaigns.externalservice.domain.ExternalServiceCampaign;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.dataqueries.domain.ReportRepository;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsProductRepository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExternalServiceCampaignWritePlatformServiceJpaRepositoryImpl implements ExternalServiceCampaignWritePlatformService {

	private final ExternalServiceCampaignRepository repository;
	private final ExternalServiceCampaignValidator validator;
	private final FromJsonHelper fromApiJsonHelper;
	private final ReportRepository reportRepository;
	private final LoanProductRepository loanProductRepository;
	private final SavingsProductRepository savingsProductRepository;

	@Autowired
	public ExternalServiceCampaignWritePlatformServiceJpaRepositoryImpl(ExternalServiceCampaignRepository repository,
																		ExternalServiceCampaignValidator validator,
																		FromJsonHelper fromApiJsonHelper,
																		ReportRepository reportRepository,
																		LoanProductRepository loanProductRepository,
																		SavingsProductRepository savingsProductRepository) {
		this.repository = repository;
		this.validator = validator;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.reportRepository = reportRepository;
		this.loanProductRepository = loanProductRepository;
		this.savingsProductRepository = savingsProductRepository;
	}

	@Transactional
	@Override
	public CommandProcessingResult create(final JsonCommand command) {

		try {
			List<ExternalServiceCampaign> existingCampaigns = this.repository.findAll();
			this.validator.validateCampaign(command.json(), existingCampaigns, null);
			ExternalServiceCampaign campaign = new ExternalServiceCampaign();
			JsonElement element = this.fromApiJsonHelper.parse(command.json());

			String campaignName = this.fromApiJsonHelper.extractStringNamed(ExternalServiceCampaignValidator.campaignName, element);
			Long businessRuleId = this.fromApiJsonHelper.extractLongNamed(ExternalServiceCampaignValidator.businessRuleId, element);
			String serviceUrl = this.fromApiJsonHelper.extractStringNamed(ExternalServiceCampaignValidator.serviceUrl, element);
			String payload = this.fromApiJsonHelper.extractStringNamed(ExternalServiceCampaignValidator.payload, element);
			Long loanProductId = this.fromApiJsonHelper.extractLongNamed(ExternalServiceCampaignValidator.loanProductId, element);
			Long savingsProductId = this.fromApiJsonHelper.extractLongNamed(ExternalServiceCampaignValidator.savingsProductId, element);
			LocalDate specificExecutionDate = this.fromApiJsonHelper.extractLocalDateNamed(ExternalServiceCampaignValidator.specificExecutionDate, element);

			campaign.setCampaignName(campaignName);
			campaign.setBusinessRule(this.reportRepository.findOne(businessRuleId));
			campaign.setUrl(serviceUrl);
			campaign.setPayload(payload);
			if (specificExecutionDate != null) {
				campaign.setSpecificExecutionDate(specificExecutionDate.toDate());
			}
			if (loanProductId != null) {
				campaign.setLoanProduct(this.loanProductRepository.findOne(loanProductId));
			}
			if (savingsProductId != null) {
				campaign.setSavingsProduct(this.savingsProductRepository.findOne(savingsProductId));
			}

			this.repository.save(campaign);

			return new CommandProcessingResultBuilder()
					.withCommandId(command.commandId())
					.withEntityId(campaign.getId())
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return CommandProcessingResult.empty();
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult update(final Long resourceId, final JsonCommand command) {

		try {
			List<ExternalServiceCampaign> existingCampaigns = this.repository.findAll();
			this.validator.validateCampaign(command.json(), existingCampaigns, resourceId);

			ExternalServiceCampaign campaign = this.repository.findOne(resourceId);
			JsonElement element = this.fromApiJsonHelper.parse(command.json());

			String campaignName = this.fromApiJsonHelper.extractStringNamed(ExternalServiceCampaignValidator.campaignName, element);
			Long businessRuleId = this.fromApiJsonHelper.extractLongNamed(ExternalServiceCampaignValidator.businessRuleId, element);
			String serviceUrl = this.fromApiJsonHelper.extractStringNamed(ExternalServiceCampaignValidator.serviceUrl, element);
			String payload = this.fromApiJsonHelper.extractStringNamed(ExternalServiceCampaignValidator.payload, element);
			Long loanProductId = this.fromApiJsonHelper.extractLongNamed(ExternalServiceCampaignValidator.loanProductId, element);
			Long savingsProductId = this.fromApiJsonHelper.extractLongNamed(ExternalServiceCampaignValidator.savingsProductId, element);
			LocalDate specificExecutionDate = this.fromApiJsonHelper.extractLocalDateNamed(ExternalServiceCampaignValidator.specificExecutionDate, element);

			campaign.setCampaignName(campaignName);
			campaign.setBusinessRule(this.reportRepository.findOne(businessRuleId));
			campaign.setUrl(serviceUrl);
			campaign.setPayload(payload);
			if (specificExecutionDate != null) {
				campaign.setSpecificExecutionDate(specificExecutionDate.toDate());
			} else {
				if (campaign.getSpecificExecutionDate() != null) {
					campaign.setSpecificExecutionDate(null);
				}
			}
			if (loanProductId != null) {
				campaign.setLoanProduct(this.loanProductRepository.findOne(loanProductId));
			} else {
				if (campaign.getLoanProduct() != null) {
					campaign.setLoanProduct(null);
				}
			}
			if (savingsProductId != null) {
				campaign.setSavingsProduct(this.savingsProductRepository.findOne(savingsProductId));
			} else {
				if (campaign.getSavingsProduct() != null) {
					campaign.setSavingsProduct(null);
				}
			}

			this.repository.save(campaign);
			return new CommandProcessingResultBuilder()
					.withCommandId(command.commandId())
					.withEntityId(resourceId)
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return CommandProcessingResult.empty();
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult delete(final Long campaignId) {
		this.repository.delete(campaignId);
		this.repository.flush();
		return new CommandProcessingResultBuilder().withEntityId(campaignId).build();
	}
}