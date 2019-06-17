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
import org.apache.fineract.infrastructure.campaigns.email.data.EmailBusinessRulesData;
import org.apache.fineract.infrastructure.campaigns.email.data.EmailRecipientsData;
import org.apache.fineract.infrastructure.campaigns.email.data.PreviewCampaignMessage;
import org.apache.fineract.infrastructure.campaigns.email.service.EmailCampaignReadPlatformService;
import org.apache.fineract.infrastructure.campaigns.email.service.EmailCampaignWritePlatformService;
import org.apache.fineract.infrastructure.campaigns.externalservice.data.ExternalServiceCampaignData;
import org.apache.fineract.infrastructure.campaigns.externalservice.service.ExternalServiceCampaignReadPlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/externalservice/campaign")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Component
@Scope("singleton")
public class ExternalServiceCampaignApiResource {


	//change name to email campaign
	private final String resourceNameForPermissions = "EXTERNAL_SERVICE_CAMPAIGN";

	private final PlatformSecurityContext context;

	private final DefaultToApiJsonSerializer<EmailBusinessRulesData> toApiJsonSerializer;
	private final DefaultToApiJsonSerializer<EmailRecipientsData> emailRecipientsDataDefaultToApiJsonSerializer;

	private final ApiRequestParameterHelper apiRequestParameterHelper;

	private final EmailCampaignReadPlatformService emailCampaignReadPlatformService;
	private final ExternalServiceCampaignReadPlatformService externalServiceCampaignReadPlatformService;
	private final FromJsonHelper fromJsonHelper;


	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final DefaultToApiJsonSerializer<ExternalServiceCampaignData> externalServiceCampaignDataDefaultToApiJsonSerializer;
	private final EmailCampaignWritePlatformService emailCampaignWritePlatformService;

	private final DefaultToApiJsonSerializer<PreviewCampaignMessage> previewCampaignMessageDefaultToApiJsonSerializer;


	@Autowired
	public ExternalServiceCampaignApiResource(final PlatformSecurityContext context, final DefaultToApiJsonSerializer<EmailBusinessRulesData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
											  final EmailCampaignReadPlatformService emailCampaignReadPlatformService, final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
											  final DefaultToApiJsonSerializer<ExternalServiceCampaignData> externalServiceCampaignDataDefaultToApiJsonSerializer,
											  final FromJsonHelper fromJsonHelper, final EmailCampaignWritePlatformService emailCampaignWritePlatformService,
											  final DefaultToApiJsonSerializer<PreviewCampaignMessage> previewCampaignMessageDefaultToApiJsonSerializer,
											  final DefaultToApiJsonSerializer<EmailRecipientsData> emailRecipientsDataDefaultToApiJsonSerializer, ExternalServiceCampaignReadPlatformService externalServiceCampaignReadPlatformService) {
		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.emailCampaignReadPlatformService = emailCampaignReadPlatformService;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.externalServiceCampaignDataDefaultToApiJsonSerializer = externalServiceCampaignDataDefaultToApiJsonSerializer;
		this.fromJsonHelper = fromJsonHelper;
		this.emailCampaignWritePlatformService = emailCampaignWritePlatformService;
		this.previewCampaignMessageDefaultToApiJsonSerializer = previewCampaignMessageDefaultToApiJsonSerializer;
		this.emailRecipientsDataDefaultToApiJsonSerializer = emailRecipientsDataDefaultToApiJsonSerializer;
		this.externalServiceCampaignReadPlatformService = externalServiceCampaignReadPlatformService;
	}


	@GET
	@Path("{campaignId}")
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveOneCampaign(@PathParam("campaignId") final Long campaignId, @Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
		ExternalServiceCampaignData externalServiceCampaignData = this.externalServiceCampaignReadPlatformService.retrieveOne(campaignId);
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

		return this.externalServiceCampaignDataDefaultToApiJsonSerializer.serialize(settings, externalServiceCampaignData);
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveAllCampaign(@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
		List<ExternalServiceCampaignData> externalServiceCampaigns = this.externalServiceCampaignReadPlatformService.retrieveAll();
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

		return this.externalServiceCampaignDataDefaultToApiJsonSerializer.serialize(settings, externalServiceCampaigns);
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

		final ExternalServiceCampaignData externalServiceCampaign = this.externalServiceCampaignReadPlatformService.retrieveTemplate();

		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.externalServiceCampaignDataDefaultToApiJsonSerializer.serialize(settings, externalServiceCampaign);
	}

	@GET
	@Path("{campaignId}/template")
	public String retrieveOneTemplate(@PathParam("campaignId") final Long campaignId, @Context final UriInfo uriInfo) {
		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

		final EmailBusinessRulesData emailBusinessRulesData = this.emailCampaignReadPlatformService.retrieveOneTemplate(campaignId, "API");
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, emailBusinessRulesData);

	}

	@DELETE
	@Path("{campaignId}")
	public String delete(@PathParam("campaignId") final Long campaignId) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteExtenalServiceCampaign(campaignId).build();

		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

		return this.toApiJsonSerializer.serialize(result);
	}

}
