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

import org.apache.fineract.infrastructure.campaigns.email.domain.ExternalServiceCampaignLogRepository;
import org.apache.fineract.infrastructure.campaigns.externalservice.domain.ExternalServiceCampaign;
import org.apache.fineract.infrastructure.campaigns.externalservice.domain.ExternalServiceCampaignLog;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

public class ExternalServiceExecutor implements Runnable {

	private Loan loan;
	private int tries;
	private long retryLag;
	private String payload;
	private int maximumRetries;
	private FineractPlatformTenant tenant;
	private SavingsAccount savingsAccount;
	private LoanTransaction loanTransaction;
	private ExternalServiceCampaign externalServiceCampaign;
	private SavingsAccountTransaction savingsAccountTransaction;
	private ExternalServiceCampaignLogRepository externalServiceCampaignLogRepository;

	public ExternalServiceExecutor(long retryLag,
								   String payload,
								   int maximumRetries,
								   FineractPlatformTenant tenant,
								   ExternalServiceCampaign externalServiceCampaign,
								   ExternalServiceCampaignLogRepository externalServiceCampaignLogRepository) {
		tries = 1;
		this.tenant = tenant;
		this.payload = payload;
		this.retryLag = retryLag;
		this.maximumRetries = maximumRetries;
		this.externalServiceCampaign = externalServiceCampaign;
		this.externalServiceCampaignLogRepository = externalServiceCampaignLogRepository;
	}

	public ExternalServiceExecutor(Loan loan,
								   long retryLag,
								   String payload,
								   int maximumRetries,
								   FineractPlatformTenant tenant,
								   ExternalServiceCampaign externalServiceCampaign,
								   ExternalServiceCampaignLogRepository externalServiceCampaignLogRepository) {
		this(retryLag, payload, maximumRetries, tenant, externalServiceCampaign, externalServiceCampaignLogRepository);
		this.loan = loan;
	}

	public ExternalServiceExecutor(LoanTransaction loanTransaction,
								   long retryLag,
								   String payload,
								   int maximumRetries,
								   FineractPlatformTenant tenant,
								   ExternalServiceCampaign externalServiceCampaign,
								   ExternalServiceCampaignLogRepository externalServiceCampaignLogRepository) {
		this(retryLag, payload, maximumRetries, tenant, externalServiceCampaign, externalServiceCampaignLogRepository);
		this.loanTransaction = loanTransaction;
	}

	public ExternalServiceExecutor(SavingsAccount savingsAccount,
								   long retryLag,
								   String payload,
								   int maximumRetries,
								   FineractPlatformTenant tenant,
								   ExternalServiceCampaign externalServiceCampaign,
								   ExternalServiceCampaignLogRepository externalServiceCampaignLogRepository) {
		this(retryLag, payload, maximumRetries, tenant, externalServiceCampaign, externalServiceCampaignLogRepository);
		this.savingsAccount = savingsAccount;
	}

	public ExternalServiceExecutor(SavingsAccountTransaction savingsAccountTransaction,
								   long retryLag,
								   String payload,
								   int maximumRetries,
								   FineractPlatformTenant tenant,
								   ExternalServiceCampaign externalServiceCampaign,
								   ExternalServiceCampaignLogRepository externalServiceCampaignLogRepository) {
		this(retryLag, payload, maximumRetries, tenant, externalServiceCampaign, externalServiceCampaignLogRepository);
		this.savingsAccountTransaction = savingsAccountTransaction;
	}

	@Override
	public void run() {
		ThreadLocalContextUtil.setTenant(this.tenant);
		this.callExternalSystem();
	}

	public void start() {
		System.out.println("External Service Campaign Executor thread started!");
		Thread executorThread = new Thread(this);
		executorThread.start();
	}

	private void callExternalSystem() {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(this.externalServiceCampaign.getUrl());
			StringEntity input = new StringEntity(payload, ContentType.APPLICATION_JSON);
			post.addHeader("accept", ContentType.APPLICATION_JSON.getMimeType());
			post.addHeader("content-type", ContentType.APPLICATION_JSON.getMimeType());
			if (this.externalServiceCampaign.getApiKey() != null) {
				post.addHeader("Authorization", "Basic " + this.externalServiceCampaign.getApiKey().getKey());
			}
			post.setEntity(input);

			HttpResponse response = client.execute(post);

			int status = response.getStatusLine().getStatusCode();
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = rd.readLine();
			System.out.println("========================================================================");
			System.out.println(response.getStatusLine().getReasonPhrase());
			System.out.println("REQUEST STRING: " + this.payload);
			System.out.println("========================================================================");
			System.out.println("RESPONSE STRING: " + line);
			System.out.println("========================================================================");
			if (status >= 200 && status < 300) {
				this.logExternalServiceCampaignExecution(status, line, null);
			} else {
				String error = response.getStatusLine().getReasonPhrase() + ": " + line;
				this.logExternalServiceCampaignExecution(status, null, error);
			}
		} catch (HttpHostConnectException ex) {
			if (tries <= this.maximumRetries) {
				tries += 1;
				try {
					Thread.sleep(this.retryLag);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("External Service Campaign Executor retry after timeout " + tries);
				this.callExternalSystem();
			} else {
				this.logExternalServiceCampaignExecution(500, null, ex.getMessage());
			}
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void logExternalServiceCampaignExecution(int status, String apiResponse, String apiResponseError) {
		ExternalServiceCampaignLog externalServiceCampaignLog = new ExternalServiceCampaignLog();
		externalServiceCampaignLog.setLoan(this.loan);
		externalServiceCampaignLog.setApiResponse(apiResponse);
		externalServiceCampaignLog.setApiResponseStatus(status);
		externalServiceCampaignLog.setExecutionTime(new Date());
		externalServiceCampaignLog.setApiResponseError(apiResponseError);
		externalServiceCampaignLog.setSavingsAccount(this.savingsAccount);
		externalServiceCampaignLog.setLoanTransaction(this.loanTransaction);
		externalServiceCampaignLog.setSavingsAccountTransaction(savingsAccountTransaction);
		externalServiceCampaignLog.setExternalServiceCampaign(this.externalServiceCampaign);
		this.externalServiceCampaignLogRepository.save(externalServiceCampaignLog);
	}
}
