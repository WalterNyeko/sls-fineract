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

import org.apache.fineract.infrastructure.campaigns.email.domain.ExternalServiceCampaignRepository;
import org.apache.fineract.infrastructure.campaigns.externalservice.constants.ExternalServiceCampaignConstants;
import org.apache.fineract.infrastructure.campaigns.externalservice.data.ClientReportData;
import org.apache.fineract.infrastructure.campaigns.externalservice.data.LoanReportData;
import org.apache.fineract.infrastructure.campaigns.externalservice.data.LoanTransactionReportData;
import org.apache.fineract.infrastructure.campaigns.externalservice.data.SavingsAccountReportData;
import org.apache.fineract.infrastructure.campaigns.externalservice.data.SavingsAccountTransactionReportData;
import org.apache.fineract.infrastructure.campaigns.externalservice.domain.ExternalServiceCampaign;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepository;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jettison.json.JSONException;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class ExternalServiceCampaignDomainService {

	private static final Logger logger = LoggerFactory.getLogger(ExternalServiceCampaignDomainService.class);
	private final JdbcTemplate jdbcTemplate;
	private final LoanReportMapper loanReportMapper;
	private final ClientRepository clientRepository;
	private final ClientReportMapper clientReportMapper;
	private final SavingsAccountReportMapper savingsAccountReportMapper;
	private final LoanTransactionReportMapper loanTransactionReportMapper;
	private final BusinessEventNotifierService businessEventNotifierService;
	private final ExternalServiceCampaignRepository externalServiceCampaignRepository;
	private final SavingsAccountTransactionReportMapper savingsAccountTransactionReportMapper;

	@Autowired
	public ExternalServiceCampaignDomainService(RoutingDataSource dataSource,
												ClientRepository clientRepository,
												BusinessEventNotifierService businessEventNotifierService,
												ExternalServiceCampaignRepository externalServiceCampaignRepository) {
		this.clientRepository = clientRepository;
		this.loanReportMapper = new LoanReportMapper();
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.clientReportMapper = new ClientReportMapper();
		this.businessEventNotifierService = businessEventNotifierService;
		this.savingsAccountReportMapper = new SavingsAccountReportMapper();
		this.loanTransactionReportMapper = new LoanTransactionReportMapper();
		this.externalServiceCampaignRepository = externalServiceCampaignRepository;
		this.savingsAccountTransactionReportMapper = new SavingsAccountTransactionReportMapper();
	}

	@PostConstruct
	public void addListeners() {
		this.businessEventNotifierService.addBusinessEventPostListners(BusinessEventNotificationConstants.BUSINESS_EVENTS.LOAN_CREATE, new CallExternalServiceOnLoanCreated());
		this.businessEventNotifierService.addBusinessEventPostListners(BusinessEventNotificationConstants.BUSINESS_EVENTS.LOAN_APPROVED, new CallExternalServiceOnLoanApproved());
		this.businessEventNotifierService.addBusinessEventPostListners(BusinessEventNotificationConstants.BUSINESS_EVENTS.LOAN_DISBURSAL, new CallExternalServiceOnLoanDisbursed());
		this.businessEventNotifierService.addBusinessEventPostListners(BusinessEventNotificationConstants.BUSINESS_EVENTS.SAVINGS_DEPOSIT, new CallExternalServiceOnAccountCredit());
		this.businessEventNotifierService.addBusinessEventPostListners(BusinessEventNotificationConstants.BUSINESS_EVENTS.SAVINGS_WITHDRAWAL, new CallExternalServiceOnAccountDebit());
		this.businessEventNotifierService.addBusinessEventPostListners(BusinessEventNotificationConstants.BUSINESS_EVENTS.SAVINGS_CREATE, new CallExternalServiceOnSavingsAccountCreated());
	}

	@CronTarget(jobName = JobName.EXECUTE_SCHEDULED_EXTERNAL_SERVICE_CAMPAIGNS)
	public void executeScheduledExternalServiceCampaigns() {
		this.executeBirthdayEventCampaigns();
		this.executeSpecialEventCampaigns();
	}

	private void executeBirthdayEventCampaigns() {
		String reportName = "Birthday Event - API";
		List<ExternalServiceCampaign> campaigns = retrieveExternalServiceCampaigns(reportName);
		if (!campaigns.isEmpty()) {
			List<Client> clients = this.clientRepository.findAll();
			clients.stream().filter(client -> client.isActive()).forEach(client -> {
				if (this.isClientsBirthday(client)) {
					campaigns.forEach(campaign -> {
						try {
							this.executeCampaign(campaign, client);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					});
				}
			});
		}
	}

	private boolean isClientsBirthday(Client client) {
		LocalDate today = LocalDate.now();
		LocalDate clientDob = client.dateOfBirthLocalDate();
		if (clientDob != null) {
			return clientDob.getMonthOfYear() == today.getMonthOfYear() && clientDob.getDayOfMonth() == today.getDayOfMonth();
		}
		return false;
	}

	private void executeSpecialEventCampaigns() {
		LocalDate today = LocalDate.now();
		String reportName = "Special Event - API";
		List<ExternalServiceCampaign> campaigns = retrieveExternalServiceCampaigns(reportName);
		List<ExternalServiceCampaign> filteredCampaigns = new ArrayList<>();
		campaigns.stream().forEach(campaign -> {
			if (campaign.getSpecificExecutionDate() != null) {
				LocalDate specialDate = LocalDate.fromDateFields(campaign.getSpecificExecutionDate());
				if (specialDate.getMonthOfYear() == today.getMonthOfYear() && specialDate.getDayOfMonth() == today.getDayOfMonth()) {
					filteredCampaigns.add(campaign);
				}
			}
			});
		if (!filteredCampaigns.isEmpty()) {
			filteredCampaigns.forEach(campaign -> {
				List<Client> clients = this.clientRepository.findAll();
				clients.stream().filter(client -> client.isActive()).forEach(client -> {
					try {
						this.executeCampaign(campaign, client);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				});
			});
		}
	}

	private void executeCampaign(ExternalServiceCampaign campaign, Client client) throws JSONException {
		String sql = campaign.getBusinessRule().getReportSql();
		sql = sql.replace("${clientId}", client.getId().toString());
		ClientReportData clientReport = this.jdbcTemplate.queryForObject(sql, this.clientReportMapper);
		String payload = this.replacePlaceholdersInPayload(campaign.getPayload(), clientReport);
		this.callExternalSystem(campaign.getUrl(), payload);

	}

	private void executeCampaign(Loan loan, String reportName) throws JSONException {
		List<ExternalServiceCampaign> campaigns = retrieveExternalServiceCampaigns(reportName);
		if (campaigns.size() > 0) {
			for (ExternalServiceCampaign campaign : campaigns) {
				if (campaign.getLoanProduct() != null) {
					if (!campaign.getLoanProduct().getId().equals(loan.getLoanProduct().getId())) {
						continue;
					}
				}
				String sql = campaign.getBusinessRule().getReportSql();
				sql = sql.replace("${loanId}", loan.getId().toString());
				LoanReportData loanReport = this.jdbcTemplate.queryForObject(sql, loanReportMapper);
				String payload = this.replacePlaceholdersInPayload(campaign.getPayload(), loanReport);
				this.callExternalSystem(campaign.getUrl(), payload);
			}
		}
	}

	private void executeCampaign(LoanTransaction loanTransaction, String reportName) throws JSONException {
		List<ExternalServiceCampaign> campaigns = retrieveExternalServiceCampaigns(reportName);
		if (campaigns.size() > 0) {
			for (ExternalServiceCampaign campaign : campaigns) {
				if (campaign.getLoanProduct() != null) {
					if (!campaign.getLoanProduct().getId().equals(loanTransaction.getLoan().getLoanProduct().getId())) {
						continue;
					}
				}
				String sql = campaign.getBusinessRule().getReportSql();
				sql = sql.replace("${transactionId}", loanTransaction.getId().toString());
				LoanTransactionReportData loanTransactionReport = this.jdbcTemplate.queryForObject(sql, loanTransactionReportMapper);
				String payload = this.replacePlaceholdersInPayload(campaign.getPayload(), loanTransactionReport);
				this.callExternalSystem(campaign.getUrl(), payload);
			}
		}
	}

	private void executeCampaign(SavingsAccount savingsAccount, String reportName) throws JSONException {
		List<ExternalServiceCampaign> campaigns = retrieveExternalServiceCampaigns(reportName);
		if (campaigns.size() > 0) {
			for (ExternalServiceCampaign campaign : campaigns) {
				if (campaign.getSavingsProduct() != null) {
					if (!campaign.getSavingsProduct().getId().equals(savingsAccount.getProduct().getId())) {
						continue;
					}
				}
				String sql = campaign.getBusinessRule().getReportSql();
				sql = sql.replace("${savingsId}", savingsAccount.getId().toString());
				SavingsAccountReportData savingsAccountReport = this.jdbcTemplate.queryForObject(sql, savingsAccountReportMapper);
				String payload = this.replacePlaceholdersInPayload(campaign.getPayload(), savingsAccountReport);
				this.callExternalSystem(campaign.getUrl(), payload);
			}
		}
	}

	private void executeCampaign(SavingsAccountTransaction savingsAccountTransaction, String reportName) throws JSONException {
		List<ExternalServiceCampaign> campaigns = retrieveExternalServiceCampaigns(reportName);
		if (campaigns.size() > 0) {
			for (ExternalServiceCampaign campaign : campaigns) {
				if (campaign.getSavingsProduct() != null) {
					if (!campaign.getSavingsProduct().getId().equals(savingsAccountTransaction.getSavingsAccount().getProduct().getId())) {
						continue;
					}
				}
				String sql = campaign.getBusinessRule().getReportSql();
				sql = sql.replace("${savingsTransactionId}", savingsAccountTransaction.getId().toString());
				SavingsAccountTransactionReportData savingsAccountTransactionReport = this.jdbcTemplate.queryForObject(sql, savingsAccountTransactionReportMapper);
				String payload = this.replacePlaceholdersInPayload(campaign.getPayload(), savingsAccountTransactionReport);
				this.callExternalSystem(campaign.getUrl(), payload);
			}
		}
	}

	private String replacePlaceholdersInPayload(String payload, LoanReportData loanReport) {
		payload = payload.replace(ExternalServiceCampaignConstants.LOAN_ID, loanReport.getLoanId().toString());
		payload = payload.replace(ExternalServiceCampaignConstants.LOAN_AMOUNT, loanReport.getLoanAmount().toString());
		payload = payload.replace(ExternalServiceCampaignConstants.ANNUAL_INTEREST_RATE, loanReport.getAnnualInterestRate().toString());
		payload = payload.replace(ExternalServiceCampaignConstants.SUBMIT_DATE, "\"" + loanReport.getSubmittedOnDate().toString("YYYY-MM-DD") + "\"");
		payload = payload.replace(ExternalServiceCampaignConstants.CLIENT_FIRST_NAME, "\"" + loanReport.getClientFirstName() + "\"");
		payload = payload.replace(ExternalServiceCampaignConstants.CLIENT_DISPLAY_NAME, "\"" + loanReport.getClientDisplayName() + "\"");
		if (loanReport.getApprovedOnDate() != null) {
			payload = payload.replace(ExternalServiceCampaignConstants.APPROVAL_DATE, "\"" + loanReport.getApprovedOnDate().toString("YYYY-MM-DD") + "\"");
		}
		if (loanReport.getDisbursedOnDate() != null) {
			payload = payload.replace(ExternalServiceCampaignConstants.DISBURSEMENT_DATE, "\"" + loanReport.getDisbursedOnDate().toString("YYYY-MM-DD") + "\"");
		}
		if (loanReport.getOfficerId() != null) {
			payload = payload.replace(ExternalServiceCampaignConstants.OFFICER_ID, loanReport.getOfficerId().toString());
		}
		if (loanReport.getOfficerFirstName() != null) {
			payload = payload.replace(ExternalServiceCampaignConstants.OFFICER_FIRST_NAME, "\"" + loanReport.getOfficerFirstName() + "\"");
		}
		if (loanReport.getOfficerDisplayName() != null) {
			payload = payload.replace(ExternalServiceCampaignConstants.OFFICER_DISPLAY_NAME, "\"" + loanReport.getOfficerDisplayName() + "\"");
		}
		return payload;
	}

	private String replacePlaceholdersInPayload(String payload, LoanTransactionReportData loanTransactionReport) {
		payload = payload.replace(ExternalServiceCampaignConstants.LOAN_ID, loanTransactionReport.getLoanId().toString());
		payload = payload.replace(ExternalServiceCampaignConstants.TRANSACTION_AMOUNT, loanTransactionReport.getTransactionAmount().toString());
		payload = payload.replace(ExternalServiceCampaignConstants.OUTSTANDING_BALANCE, loanTransactionReport.getOutstandingBalance().toString());
		payload = payload.replace(ExternalServiceCampaignConstants.TRANSACTION_DATE, "\"" + loanTransactionReport.getTransactionDate().toString("YYYY-MM-DD") + "\"");
		payload = payload.replace(ExternalServiceCampaignConstants.CLIENT_FIRST_NAME, "\"" + loanTransactionReport.getClientFirstName() + "\"");
		payload = payload.replace(ExternalServiceCampaignConstants.CLIENT_DISPLAY_NAME, "\"" + loanTransactionReport.getClientDisplayName() + "\"");
		return payload;
	}

	private String replacePlaceholdersInPayload(String payload, SavingsAccountReportData savingsAccountReport) {
		payload = payload.replace(ExternalServiceCampaignConstants.SAVINGS_ACCOUNT_ID, savingsAccountReport.getSavingsAccountId().toString());
		payload = payload.replace(ExternalServiceCampaignConstants.ACCOUNT_NUMBER, "\"" + savingsAccountReport.getAccountNumber().toString() + "\"");
		payload = payload.replace(ExternalServiceCampaignConstants.NUBAN_ACCOUNT_NUMBER, "\"" + savingsAccountReport.getNubanAccountNumber().toString() + "\"");
		payload = payload.replace(ExternalServiceCampaignConstants.SUBMIT_DATE, "\"" + savingsAccountReport.getSubmittedOnDate().toString("YYYY-MM-DD") + "\"");
		payload = payload.replace(ExternalServiceCampaignConstants.CLIENT_FIRST_NAME, "\"" + savingsAccountReport.getClientFirstName() + "\"");
		payload = payload.replace(ExternalServiceCampaignConstants.CLIENT_DISPLAY_NAME, "\"" + savingsAccountReport.getClientDisplayName() + "\"");
		return payload;
	}

	private String replacePlaceholdersInPayload(String payload, SavingsAccountTransactionReportData savingsAccountTransactionReport) {
		payload = payload.replace(ExternalServiceCampaignConstants.TRANSACTION_ID, savingsAccountTransactionReport.getTransactionId().toString());
		payload = payload.replace(ExternalServiceCampaignConstants.ACCOUNT_NUMBER, "\"" + savingsAccountTransactionReport.getAccountNumber().toString() + "\"");
		payload = payload.replace(ExternalServiceCampaignConstants.NUBAN_ACCOUNT_NUMBER, "\"" + savingsAccountTransactionReport.getNubanAccountNumber().toString() + "\"");
		payload = payload.replace(ExternalServiceCampaignConstants.DEPOSIT_AMOUNT, savingsAccountTransactionReport.getTransactionAmount().toString());
		payload = payload.replace(ExternalServiceCampaignConstants.WITHDRAWAL_AMOUNT, savingsAccountTransactionReport.getTransactionAmount().toString());
		payload = payload.replace(ExternalServiceCampaignConstants.ACCOUNT_BALANCE, savingsAccountTransactionReport.getAccountBalance().toString());
		payload = payload.replace(ExternalServiceCampaignConstants.TRANSACTION_DATE, "\"" + savingsAccountTransactionReport.getTransactionDate().toString("YYYY-MM-DD") + "\"");
		payload = payload.replace(ExternalServiceCampaignConstants.CLIENT_FIRST_NAME, "\"" + savingsAccountTransactionReport.getClientFirstName() + "\"");
		payload = payload.replace(ExternalServiceCampaignConstants.CLIENT_DISPLAY_NAME, "\"" + savingsAccountTransactionReport.getClientDisplayName() + "\"");
		return payload;
	}

	private String replacePlaceholdersInPayload(String payload, ClientReportData clientReport) {
		payload = payload.replace(ExternalServiceCampaignConstants.DATE_OF_BIRTH, "\"" + clientReport.getDateOfBirth().toString("YYYY-MM-DD") + "\"");
		payload = payload.replace(ExternalServiceCampaignConstants.CLIENT_FIRST_NAME, "\"" + clientReport.getClientFirstName() + "\"");
		payload = payload.replace(ExternalServiceCampaignConstants.CLIENT_DISPLAY_NAME, "\"" + clientReport.getClientDisplayName() + "\"");
		return payload;
	}

	private void callExternalSystem(String url, String payload) {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(url);
			StringEntity input = new StringEntity(payload, ContentType.APPLICATION_JSON);
			post.addHeader("accept", ContentType.APPLICATION_JSON.getMimeType());
			post.addHeader("content-type", ContentType.APPLICATION_JSON.getMimeType());
			post.setEntity(input);

			HttpResponse response = client.execute(post);

			int status = response.getStatusLine().getStatusCode();
			if (!(status >= 200 && status < 300)) {
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String line = rd.readLine();
				System.out.println("========================================================================");
				System.out.println(response.getStatusLine().getReasonPhrase());
				System.out.println("REQUEST STRING: " + payload);
				System.out.println("========================================================================");
				System.out.println("RESPONSE STRING: " + line);
				System.out.println("========================================================================");
			} else {

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private List<ExternalServiceCampaign> retrieveExternalServiceCampaigns(String reportName) {
		List<ExternalServiceCampaign> externalServiceCampaigns = this.externalServiceCampaignRepository.findActiveExternalServiceCampaigns(reportName);
		return externalServiceCampaigns;
	}

	private abstract class ExternalServiceBusinessEventAdapter implements BusinessEventListner {

		@Override
		public void businessEventToBeExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
			//Nothing to do
		}
	}

	private class CallExternalServiceOnLoanApproved extends ExternalServiceBusinessEventAdapter {

		@Override
		public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
			logger.info("Event OnLoanApproved fired!");
			Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.LOAN);
			if (entity instanceof Loan) {
				Loan loan = (Loan) entity;
				try {
					logger.info("Loan account approved!");
					executeCampaign(loan, "Loan Approved - API");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class CallExternalServiceOnLoanCreated extends ExternalServiceBusinessEventAdapter {

		@Override
		public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
			Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.LOAN);
			if (entity instanceof Loan) {
				Loan loan = (Loan) entity;
				try {
					logger.info("Loan account created!");
					executeCampaign(loan, "Loan Created - API");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class CallExternalServiceOnLoanDisbursed extends ExternalServiceBusinessEventAdapter {

		@Override
		public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
			Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.LOAN);
			if (entity instanceof Loan) {
				Loan loan = (Loan) entity;
				try {
					logger.info("Loan account disbursed!");
					executeCampaign(loan, "Loan Disbursed - API");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class CallExternalServiceOnLoanRepayment extends ExternalServiceBusinessEventAdapter {

		@Override
		public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
			Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.LOAN_TRANSACTION);
			if (entity instanceof LoanTransaction) {
				LoanTransaction loanTransaction = (LoanTransaction) entity;
				if (loanTransaction.isRepayment()) {
					try {
						logger.info("Loan repayment made!");
						executeCampaign(loanTransaction, "Loan Repayment - API");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private class CallExternalServiceOnSavingsAccountCreated extends ExternalServiceBusinessEventAdapter {

		@Override
		public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
			Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.SAVING);
			if (entity instanceof SavingsAccount) {
				SavingsAccount savingsAccount = (SavingsAccount) entity;
				try {
					logger.info("Savings account created!");
					executeCampaign(savingsAccount, "Savings Account Created - API");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class CallExternalServiceOnAccountCredit extends ExternalServiceBusinessEventAdapter {

		@Override
		public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
			Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.SAVINGS_TRANSACTION);
			if (entity instanceof SavingsAccountTransaction) {
				SavingsAccountTransaction savingsAccountTransaction = (SavingsAccountTransaction) entity;
				try {
					logger.info("Savings account credited!");
					executeCampaign(savingsAccountTransaction, "Savings Account Deposit - API");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class CallExternalServiceOnAccountDebit extends ExternalServiceBusinessEventAdapter {

		@Override
		public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
			Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.SAVINGS_TRANSACTION);
			if (entity instanceof SavingsAccountTransaction) {
				SavingsAccountTransaction savingsAccountTransaction = (SavingsAccountTransaction) entity;
				try {
					logger.info("Savings account debited!");
					executeCampaign(savingsAccountTransaction, "Savings Account Withdrawal - API");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static final class LoanReportMapper implements RowMapper<LoanReportData> {

		@Override
		public LoanReportData mapRow(ResultSet rs, int rowNum) throws SQLException {
			LoanReportData loanReport = new LoanReportData();
			loanReport.setLoanId(rs.getLong("loanId"));
			loanReport.setOfficerId(rs.getLong("officerId"));
			loanReport.setLoanAmount(rs.getBigDecimal("loanAmount"));
			loanReport.setSubmittedOnDate(JdbcSupport.getLocalDate(rs, "submittedon_date"));
			loanReport.setApprovedOnDate(JdbcSupport.getLocalDate(rs, "approvedon_date"));
			loanReport.setDisbursedOnDate(JdbcSupport.getLocalDate(rs, "disbursedon_date"));
			loanReport.setClientDisplayName(rs.getString("clientDisplayName"));
			loanReport.setClientFirstName(rs.getString("clientFirstName"));
			loanReport.setAnnualInterestRate(rs.getFloat("annualInterestRate"));
			loanReport.setOfficerFirstName(rs.getString("officerFirstname"));
			loanReport.setOfficerDisplayName(rs.getString("officerDisplayName"));
			return loanReport;
		}
	}

	private static final class LoanTransactionReportMapper implements RowMapper<LoanTransactionReportData> {

		@Override
		public LoanTransactionReportData mapRow(ResultSet rs, int rowNum) throws SQLException {
			LoanTransactionReportData loanTransactionReport = new LoanTransactionReportData();
			loanTransactionReport.setLoanId(rs.getLong("loanId"));
			loanTransactionReport.setTransactionAmount(rs.getBigDecimal("transactionAmount"));
			loanTransactionReport.setTransactionDate(JdbcSupport.getLocalDate(rs, "transactionDate"));
			loanTransactionReport.setOutstandingBalance(rs.getBigDecimal("outstandingBalance"));
			loanTransactionReport.setClientDisplayName(rs.getString("clientDisplayName"));
			loanTransactionReport.setClientFirstName(rs.getString("clientFirstName"));
			return loanTransactionReport;
		}
	}

	private static final class SavingsAccountReportMapper implements RowMapper<SavingsAccountReportData> {

		@Override
		public SavingsAccountReportData mapRow(ResultSet rs, int rowNum) throws SQLException {
			SavingsAccountReportData savingsAccountReport = new SavingsAccountReportData();
			savingsAccountReport.setSavingsAccountId(rs.getLong("accountId"));
			savingsAccountReport.setAccountNumber(rs.getString("accountNumber"));
			savingsAccountReport.setNubanAccountNumber(rs.getString("nubanAccountNumber"));
			savingsAccountReport.setSubmittedOnDate(JdbcSupport.getLocalDate(rs, "submittedon_date"));
			savingsAccountReport.setClientDisplayName(rs.getString("clientDisplayName"));
			savingsAccountReport.setClientFirstName(rs.getString("clientFirstName"));
			return savingsAccountReport;
		}
	}

	private static final class SavingsAccountTransactionReportMapper implements RowMapper<SavingsAccountTransactionReportData> {

		@Override
		public SavingsAccountTransactionReportData mapRow(ResultSet rs, int rowNum) throws SQLException {
			SavingsAccountTransactionReportData savingsAccountTransactionReport = new SavingsAccountTransactionReportData();
			savingsAccountTransactionReport.setTransactionId(rs.getLong("transactionId"));
			savingsAccountTransactionReport.setAccountNumber(rs.getString("accountNumber"));
			savingsAccountTransactionReport.setNubanAccountNumber(rs.getString("nubanAccountNumber"));
			if (rs.getBigDecimal("depositAmount") != null) {
				savingsAccountTransactionReport.setTransactionAmount(rs.getBigDecimal("depositAmount"));
			} else if (rs.getBigDecimal("withdrawalAmount") != null) {
				savingsAccountTransactionReport.setTransactionAmount(rs.getBigDecimal("withdrawalAmount"));
			}
			savingsAccountTransactionReport.setTransactionDate(JdbcSupport.getLocalDate(rs, "transactionDate"));
			savingsAccountTransactionReport.setClientDisplayName(rs.getString("clientDisplayName"));
			savingsAccountTransactionReport.setClientFirstName(rs.getString("clientFirstName"));
			return savingsAccountTransactionReport;
		}
	}

	private static final class ClientReportMapper implements RowMapper<ClientReportData> {

		@Override
		public ClientReportData mapRow(ResultSet rs, int rowNum) throws SQLException {
			ClientReportData clientReport = new ClientReportData();
			clientReport.setDateOfBirth(JdbcSupport.getLocalDate(rs, "dateOfBirth"));
			clientReport.setClientDisplayName(rs.getString("clientDisplayName"));
			clientReport.setClientFirstName(rs.getString("clientFirstName"));
			return clientReport;
		}
	}
}
