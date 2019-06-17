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
package org.apache.fineract.infrastructure.campaigns.email.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.fineract.infrastructure.campaigns.email.constants.EmailCampaignSendTo;
import org.apache.fineract.infrastructure.campaigns.email.data.EmailMessageWithAttachmentData;
import org.apache.fineract.infrastructure.campaigns.email.domain.EmailCampaign;
import org.apache.fineract.infrastructure.campaigns.email.domain.EmailCampaignRepository;
import org.apache.fineract.infrastructure.campaigns.email.domain.EmailCampaignType;
import org.apache.fineract.infrastructure.campaigns.email.domain.EmailMessage;
import org.apache.fineract.infrastructure.campaigns.email.domain.EmailMessageRepository;
import org.apache.fineract.infrastructure.campaigns.email.domain.EmailMessageStatusType;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.staff.domain.StaffRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

@Service
public class EmailCampaignDomainServiceImpl {
	private static final Logger logger = LoggerFactory.getLogger(EmailCampaignDomainServiceImpl.class);
	private final BusinessEventNotifierService businessEventNotifierService;
	private final EmailCampaignRepository emailCampaignRepository;
	private final ClientRepositoryWrapper clientRepositoryWrapper;
	private final StaffRepositoryWrapper staffRepositoryWrapper;
	private final EmailMessageRepository emailMessageRepository;
	private final EmailCampaignWritePlatformService emailCampaignWritePlatformService;
	private final EmailMessageJobEmailService emailMessageJobEmailService;

	@Autowired
	public EmailCampaignDomainServiceImpl(BusinessEventNotifierService businessEventNotifierService,
										  EmailCampaignRepository emailCampaignRepository,
										  ClientRepositoryWrapper clientRepositoryWrapper,
										  StaffRepositoryWrapper staffRepositoryWrapper,
										  EmailMessageRepository emailMessageRepository,
										  EmailCampaignWritePlatformService emailCampaignWritePlatformService,
										  EmailMessageJobEmailService emailMessageJobEmailService) {
		this.businessEventNotifierService = businessEventNotifierService;
		this.emailCampaignRepository = emailCampaignRepository;
		this.clientRepositoryWrapper = clientRepositoryWrapper;
		this.staffRepositoryWrapper = staffRepositoryWrapper;
		this.emailMessageRepository = emailMessageRepository;
		this.emailCampaignWritePlatformService = emailCampaignWritePlatformService;
		this.emailMessageJobEmailService = emailMessageJobEmailService;
	}


	@PostConstruct
	public void addListners() {
		this.businessEventNotifierService.addBusinessEventPostListners(BusinessEventNotificationConstants.BUSINESS_EVENTS.LOAN_APPROVED, new SendEmailOnLoanApproved());
		this.businessEventNotifierService.addBusinessEventPostListners(BusinessEventNotificationConstants.BUSINESS_EVENTS.LOAN_REJECTED, new SendEmailOnLoanRejected());
		this.businessEventNotifierService.addBusinessEventPostListners(BusinessEventNotificationConstants.BUSINESS_EVENTS.LOAN_DISBURSAL, new SendEmailOnLoanDisbursed());
		this.businessEventNotifierService.addBusinessEventPostListners(BusinessEventNotificationConstants.BUSINESS_EVENTS.LOAN_CREATE, new SendEmailOnLoanCreated());
	}

	private void executeCampaignOnLoanApproved(Loan loan, String reportName) throws JSONException {
		Client client = null;
		Staff staff = null;
		String emailAddress = null;
		EmailMessage emailMessage = null;
		List<EmailCampaign> emailCampaigns = retrieveEmailCampaigns(reportName);
		if(emailCampaigns.size()>0){
			for (EmailCampaign campaign : emailCampaigns){
				String paramValue = campaign.getParamValue();
				JSONObject jsonObject = new JSONObject(paramValue);
				HashMap<String, String> reportParams = this.populateReportParams(loan, campaign.getStretchyReportParamMap());
				String emailMessageTemplate = this.emailCampaignWritePlatformService.handleMessageTemplate(campaign.getEmailMessage(), campaign, reportParams);
				Long recipient = null;
				EmailCampaignSendTo emailCampaignSendTo = null;
				if(jsonObject.has("id") && jsonObject.has("recipient")){
					recipient = jsonObject.getLong("recipient");
					emailCampaignSendTo = EmailCampaignSendTo.fromInt(jsonObject.getInt("id"));
					if(emailCampaignSendTo.isClient()){
						if(recipient != -1 && recipient.equals(loan.getClientId())){
							client = this.clientRepositoryWrapper.findOneWithNotFoundDetection(recipient);
							emailAddress = client.emailAddress();
						}
					}
					if(emailCampaignSendTo.isStaff()){
						if(recipient != -1){
							staff = this.staffRepositoryWrapper.findOneWithNotFoundDetection(recipient);
							emailAddress = staff.emailAddress();
						}
					}
				}

				if(recipient != null && recipient.equals(-1L)){
					if(emailCampaignSendTo != null){
						if(emailCampaignSendTo.isClient()){
							//send email to the client whose loan was affected
							emailAddress = loan.getClient().emailAddress();
							client = loan.getClient();
						}
						if(emailCampaignSendTo.isStaff() && loan.getLoanOfficer() != null){
							//send email to all officers
							emailAddress = loan.getLoanOfficer().emailAddress();
							staff = loan.getLoanOfficer();
						}
					}
				}

				if (emailAddress != null && EmailCampaignWritePlatformCommandHandlerImpl.isValidEmail(emailAddress)) {
					emailMessage = EmailMessage.pendingEmail(null, client, staff, campaign, campaign.getEmailSubject(), emailMessageTemplate,
							emailAddress, campaign.getCampaignName());
					emailMessage = this.emailMessageRepository.save(emailMessage);
					this.sendEmailOnTriggeredEvent(emailMessage);
				}


			}
		}
	}

	private void sendEmailOnTriggeredEvent(EmailMessage emailMessage){
		final EmailMessageWithAttachmentData emailMessageWithAttachmentData = EmailMessageWithAttachmentData.createNew(
				emailMessage.getEmailAddress(), emailMessage.getMessage(), emailMessage.getEmailSubject(), null);
		this.emailMessageJobEmailService.sendEmailWithAttachment(emailMessageWithAttachmentData);

		emailMessage.setStatusType(EmailMessageStatusType.SENT.getValue());

		this.emailMessageRepository.save(emailMessage);
	}

	private HashMap<String, String> populateReportParams(Loan loan, String reportParams) {
		try {
			HashMap<String, String> campaignParams = new ObjectMapper().readValue(reportParams,
					new TypeReference<HashMap<String, String>>() {});
			if(campaignParams.containsKey("loanId")){
				campaignParams.put("loanId", loan.getId() + "");
			}
			return campaignParams;
		} catch (IOException e) {
			return null;
		}
	}

	private List<EmailCampaign> retrieveEmailCampaigns(String paramValue) {
		List<EmailCampaign> emailCampaigns = emailCampaignRepository.findActiveEmailCampaigns("%"+paramValue+"%", EmailCampaignType.TRIGGERED.getValue());
		return emailCampaigns;
	}

	private abstract class EmailBusinessEventAdapter implements BusinessEventListner {

		@Override
		public void businessEventToBeExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
			//Nothing to do
		}
	}

	private class SendEmailOnLoanApproved extends EmailBusinessEventAdapter{

		@Override
		public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
			logger.info("Event OnLoanApproved fired!");
			Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.LOAN);
			if (entity instanceof Loan) {
				Loan loan = (Loan) entity;
				try {
					executeCampaignOnLoanApproved(loan, "Loan Approved - Email");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class SendEmailOnLoanCreated extends EmailBusinessEventAdapter{

		@Override
		public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
			Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.LOAN);
			if (entity instanceof Loan) {
				Loan loan = (Loan) entity;
				try {
					executeCampaignOnLoanApproved(loan, "Loan Created");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class SendEmailOnLoanRejected extends EmailBusinessEventAdapter{

		@Override
		public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
			Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.LOAN);
			if (entity instanceof Loan) {
				Loan loan = (Loan) entity;
				try {
					executeCampaignOnLoanApproved(loan, "Loan Rejected - Email");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class SendEmailOnLoanDisbursed extends EmailBusinessEventAdapter{

		@Override
		public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
			Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.LOAN);
			if (entity instanceof Loan) {
				Loan loan = (Loan) entity;
				try {
					executeCampaignOnLoanApproved(loan, "Loan Disbursed");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
