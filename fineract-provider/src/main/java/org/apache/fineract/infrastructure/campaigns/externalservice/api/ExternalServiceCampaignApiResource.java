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
package org.apache.fineract.infrastructure.campaigns.externalservice.api;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.campaigns.externalservice.data.ExternalServiceCampaignApiKeyData;
import org.apache.fineract.infrastructure.campaigns.externalservice.data.ExternalServiceCampaignData;
import org.apache.fineract.infrastructure.campaigns.externalservice.data.ExternalServiceCampaignLogData;
import org.apache.fineract.infrastructure.campaigns.externalservice.service.ExternalServiceCampaignReadPlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

@Path("/externalservice/campaign")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Component
@Scope("singleton")
public class ExternalServiceCampaignApiResource {

	private final String resourceNameForPermissions = "EXTERNAL_SERVICE_CAMPAIGN";

	private final PlatformSecurityContext context;

	private final DefaultToApiJsonSerializer<ExternalServiceCampaignData> toApiJsonSerializer;

	private final ApiRequestParameterHelper apiRequestParameterHelper;

	private final ExternalServiceCampaignReadPlatformService externalServiceCampaignReadPlatformService;

	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final DefaultToApiJsonSerializer<ExternalServiceCampaignLogData> externalServiceCampaignLogDataToApiJsonSerializer;
	private final DefaultToApiJsonSerializer<ExternalServiceCampaignApiKeyData> externalServiceCampaignApiKeyDataToApiJsonSerializer;


	@Autowired
	public ExternalServiceCampaignApiResource(final PlatformSecurityContext context,
											  final DefaultToApiJsonSerializer<ExternalServiceCampaignData> toApiJsonSerializer,
											  final ApiRequestParameterHelper apiRequestParameterHelper,
											  final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
											  final DefaultToApiJsonSerializer<ExternalServiceCampaignApiKeyData> externalServiceCampaignApiKeyDataToApiJsonSerializer,
											  ExternalServiceCampaignReadPlatformService externalServiceCampaignReadPlatformService,
											  DefaultToApiJsonSerializer<ExternalServiceCampaignLogData> externalServiceCampaignLogDataToApiJsonSerializer) {
		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.externalServiceCampaignApiKeyDataToApiJsonSerializer = externalServiceCampaignApiKeyDataToApiJsonSerializer;
		this.externalServiceCampaignReadPlatformService = externalServiceCampaignReadPlatformService;
		this.externalServiceCampaignLogDataToApiJsonSerializer = externalServiceCampaignLogDataToApiJsonSerializer;
	}


	@GET
	@Path("{campaignId}")
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveOneCampaign(@PathParam("campaignId") final Long campaignId, @Context final UriInfo uriInfo) {
		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, this.externalServiceCampaignReadPlatformService.retrieveOne(campaignId));
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveAllCampaigns(@Context final UriInfo uriInfo) {
		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, this.externalServiceCampaignReadPlatformService.retrieveAll());
	}

	@GET
	@Path("logs")
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveAllCampaignLogs(@QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit,
										  @Context final UriInfo uriInfo) {
		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
		SearchParameters searchParameters = SearchParameters.forPagination(offset, limit);
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.externalServiceCampaignLogDataToApiJsonSerializer.serialize(settings, this.externalServiceCampaignReadPlatformService.retrieveLogs(searchParameters));
	}

	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String createCampaign(final String apiRequestBodyAsJson) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createExternalServiceCampaign().withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	@PUT
	@Path("{campaignId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String updateCampaign(@PathParam("campaignId") final Long campaignId, final String apiRequestBodyAsJson) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateExternalServiceCampaign(campaignId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	@GET()
	@Path("template")
	public String template(@Context final UriInfo uriInfo) {
		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, this.externalServiceCampaignReadPlatformService.retrieveTemplate());
	}

	@DELETE
	@Path("{campaignId}")
	public String delete(@PathParam("campaignId") final Long campaignId) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteExternalServiceCampaign(campaignId).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	@GET
	@Path("apikey/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveOneApiKey(@PathParam("id") final Long apiKeyId, @Context final UriInfo uriInfo) {
		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.externalServiceCampaignApiKeyDataToApiJsonSerializer.serialize(settings, this.externalServiceCampaignReadPlatformService.retrieveApiKeyById(apiKeyId));
	}

	@GET
	@Path("apikey")
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveAllApiKeys(@Context final UriInfo uriInfo) {
		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.externalServiceCampaignApiKeyDataToApiJsonSerializer.serialize(settings, this.externalServiceCampaignReadPlatformService.retrieveApiKeys());
	}

	@POST
	@Path("apikey")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String createCampaignApiKey(final String apiRequestBodyAsJson) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createExternalServiceCampaignApiKey().withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.externalServiceCampaignApiKeyDataToApiJsonSerializer.serialize(result);
	}

	@PUT
	@Path("apikey/{id}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String updateCampaignApiKey(@PathParam("id") final Long id, final String apiRequestBodyAsJson) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateExternalServiceCampaignApiKey(id).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.externalServiceCampaignApiKeyDataToApiJsonSerializer.serialize(result);
	}

	@DELETE
	@Path("apikey/{id}")
	public String deleteApiKey(@PathParam("id") final Long id) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteExternalServiceCampaignApiKey(id).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.externalServiceCampaignApiKeyDataToApiJsonSerializer.serialize(result);
	}
}
