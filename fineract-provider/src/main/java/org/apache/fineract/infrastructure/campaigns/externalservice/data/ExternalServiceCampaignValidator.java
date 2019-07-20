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

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.campaigns.externalservice.domain.ExternalServiceCampaign;
import org.apache.fineract.infrastructure.campaigns.externalservice.domain.ExternalServiceCampaignApiKey;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ExternalServiceCampaignValidator {


	public static final String RESOURCE_NAME = "externalServiceCampaign";
	public static final String campaignName = "campaignName";
	public static final String serviceUrl = "serviceUrl";
	public static final String payload = "payload";
	public static final String businessRuleId = "businessRuleId";
	public static final String specificExecutionDate = "specificExecutionDate";
	public static final String loanProductId = "loanProductId";
	public static final String savingsProductId = "savingsProductId";
	public static final String localeParamName = "locale";
	public static final String dateFormatParamName = "dateFormat";
	public static final String apiKeyIdParamName = "apiKeyId";
	public static final String apiKeyNameParamName = "apiKeyName";
	public static final String apiKeyParamName = "apiKey";


	private final FromJsonHelper fromApiJsonHelper;


	public static final Set<String> supportedParams = new HashSet<String>(Arrays.asList(campaignName, localeParamName, dateFormatParamName,
			businessRuleId, serviceUrl, payload, specificExecutionDate, loanProductId, savingsProductId, apiKeyIdParamName));

	@Autowired
	public ExternalServiceCampaignValidator(FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}


	public void validateCampaign(String json, List<ExternalServiceCampaign> existingCampaigns, Long campaignId) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ExternalServiceCampaignValidator.supportedParams);

		final JsonElement element = this.fromApiJsonHelper.parse(json);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
				.resource(ExternalServiceCampaignValidator.RESOURCE_NAME);

		final String campaignName = this.fromApiJsonHelper.extractStringNamed(ExternalServiceCampaignValidator.campaignName, element);
		baseDataValidator.reset().parameter(ExternalServiceCampaignValidator.campaignName).value(campaignName).notBlank().notExceedingLengthOf(100);

		final Long businessRuleId = this.fromApiJsonHelper.extractLongNamed(ExternalServiceCampaignValidator.businessRuleId, element);
		baseDataValidator.reset().parameter(ExternalServiceCampaignValidator.businessRuleId).value(businessRuleId).notNull().integerGreaterThanZero();

		final String serviceUrl = this.fromApiJsonHelper.extractStringNamed(ExternalServiceCampaignValidator.serviceUrl, element);
		baseDataValidator.reset().parameter(ExternalServiceCampaignValidator.serviceUrl).value(serviceUrl).notBlank();

		final String payload = this.fromApiJsonHelper.extractStringNamed(ExternalServiceCampaignValidator.payload, element);
		baseDataValidator.reset().parameter(ExternalServiceCampaignValidator.payload).value(payload).notBlank();

		existingCampaigns.forEach(campaign -> {
			if (campaign.getCampaignName().equals(campaignName) && !campaign.getId().equals(campaignId)) {
				baseDataValidator.reset().parameter(ExternalServiceCampaignValidator.campaignName).value(campaignName).failWithCode("campaign.name.should.be.unique", "Campaign name should be unique");
			}
		});

		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(dataValidationErrors);
		}
	}

	public void validateApiKey(String json, List<ExternalServiceCampaignApiKey> existingCampaignApiKeys, Long apiKeyId) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, new HashSet<String>(Arrays.asList(apiKeyNameParamName, apiKeyParamName)));

		final JsonElement element = this.fromApiJsonHelper.parse(json);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
				.resource(ExternalServiceCampaignValidator.RESOURCE_NAME);

		final String apiKeyName = this.fromApiJsonHelper.extractStringNamed(ExternalServiceCampaignValidator.apiKeyNameParamName, element);
		baseDataValidator.reset().parameter(ExternalServiceCampaignValidator.apiKeyNameParamName).value(apiKeyName).notBlank().notExceedingLengthOf(250);

		final String apiKey = this.fromApiJsonHelper.extractStringNamed(ExternalServiceCampaignValidator.apiKeyParamName, element);
		baseDataValidator.reset().parameter(ExternalServiceCampaignValidator.apiKeyParamName).value(apiKey).notBlank().notExceedingLengthOf(250);

		existingCampaignApiKeys.forEach(key -> {
			if (key.getName().equals(apiKeyName) && !key.getId().equals(apiKeyId)) {
				baseDataValidator.reset().parameter(ExternalServiceCampaignValidator.apiKeyNameParamName).value(apiKeyName).failWithCode("api.key.name.should.be.unique", "API key name should be unique");
			}
		});

		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(dataValidationErrors);
		}

	}
}
