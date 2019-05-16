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
package org.apache.fineract.portfolio.savings.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.portfolio.savings.domain.NubanAccountPool;
import org.apache.fineract.portfolio.savings.domain.NubanAccountPoolRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NubanAccountServiceImpl implements NubanAccountService {

	private static final int MIN_NUMBER_OF_AVAILABLE_ACCOUNTS = 1000;
	private static final int MAX_NUMBER_OF_ACCOUNTS_TO_GENERATE = 100000;
	private final NubanAccountPoolRepository nubanAccountPoolRepository;
	private final SavingsAccountRepository savingsAccountRepository;

	@Autowired
	public NubanAccountServiceImpl(NubanAccountPoolRepository nubanAccountPoolRepository, SavingsAccountRepository savingsAccountRepository) {
		this.nubanAccountPoolRepository = nubanAccountPoolRepository;
		this.savingsAccountRepository = savingsAccountRepository;
	}

	@Override
	@CronTarget(jobName = JobName.GENERATE_NUBAN_ACCOUNT_NUMBERS)
	public void generateNubanAccountNumbers() {
		List<NubanAccountPool> availableAccountNumbers = this.nubanAccountPoolRepository.findAvailableAccounts(true);
		if (availableAccountNumbers.size() < MIN_NUMBER_OF_AVAILABLE_ACCOUNTS) {
			//Generate NUBAN account numbers when available ones start running out
			String lastSerialNumber = availableAccountNumbers.isEmpty() ? "0" : availableAccountNumbers.get(0).getSavingsAccountNumber();
			String nextSerialNumber = this.generateNextSerialNumber(lastSerialNumber);
			for (int i = 0; i < MAX_NUMBER_OF_ACCOUNTS_TO_GENERATE; i++) {
				String nubanAccountNumber = this.generateNubanAccountNumber(nextSerialNumber);
				SavingsAccount savingsAccount = this.savingsAccountRepository.findAccountByAccountNumber(nextSerialNumber);
				NubanAccountPool nubanAccountPool = new NubanAccountPool();
				nubanAccountPool.setNubanAccountNumber(nubanAccountNumber);
				nubanAccountPool.setSavingsAccountNumber(nextSerialNumber);
				nubanAccountPool.setAvailable(savingsAccount == null);
				this.nubanAccountPoolRepository.save(nubanAccountPool);
				nextSerialNumber = this.generateNextSerialNumber(nextSerialNumber);
				if (savingsAccount != null && StringUtils.isBlank(savingsAccount.getNubanAccountNumber())) {
					savingsAccount.setNubanAccountNumber(nubanAccountNumber);
					this.savingsAccountRepository.save(savingsAccount);
				}
			}
		}
	}

	private String generateNextSerialNumber(String serialNumber) {
		final int maxLength = 9;
		Integer newNumber = Integer.parseInt(serialNumber) + 1;
		String nextSerialNumber = newNumber.toString();
		while (nextSerialNumber.length() < maxLength) {
			nextSerialNumber = "0" + nextSerialNumber;
		}
		return nextSerialNumber;
	}

	/**
	 * The NUBAN code of a typical customer of the SLSBank would be derived as follows:
	 * Convert SLSBank code of 51108 to a 6-digit code of 951108
	 * Assume a NUBAN serial number of 000021457 in SLSBank
	 * The check digit would be computed as follows:
	 * Step 1: 9*3+5*7+1*3+1*3+0*7+8*3+0*3+0*7+0*3+0*3+2*7+1*3+4*3+5*7+7*3 = 211
	 * Step 2: Modulo 10 of 211 is 1 i.e. 1 is the remainder when you divide 211 by 10
	 * Step 3: Subtract 1 from 10 to get 9
	 * Step 4: So the check digit is 9
	 * Therefore, the NUBAN code for this illustration is 000021457-9.
	 *
	 * @param serialNumber Serial number
	 * @return NUBAN account number
	 */
	@Override
	public String generateNubanAccountNumber(String serialNumber) {
		int[] nubanMultipliers = new int[]{3, 7, 3, 3, 7, 3, 3, 7, 3, 3, 7, 3, 3, 7, 3};
		String extendSerialNumber = SLS_UNIQUE_CODE_IDENTIFIER + serialNumber;
		//Step1
		int digit = 0;
		for (int i = 0; i < nubanMultipliers.length; i++) {
			int num = Integer.parseInt(extendSerialNumber.charAt(i) + "");
			digit += num * nubanMultipliers[i];
		}
		//Step2 & 3
		digit = 10 -  (digit % 10);
		return serialNumber + digit;
	}
}
