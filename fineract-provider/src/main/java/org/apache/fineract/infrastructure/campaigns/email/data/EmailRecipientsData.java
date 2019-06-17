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
package org.apache.fineract.infrastructure.campaigns.email.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EmailRecipientsData {
  private final Collection<EnumOptionData> recipients;

  public EmailRecipientsData(Collection<EnumOptionData> recipients) {
    this.recipients = recipients;
  }


  public static EmailRecipientsData instance(Collection<EnumOptionData> recipients) {
    List<EnumOptionData> recipientOptions = new ArrayList<>();
    if(recipients != null && !recipients.isEmpty()){
      recipientOptions.addAll(recipients);
      recipientOptions.add(new EnumOptionData(-1L, "ALL", "ALL"));
    }

    return new EmailRecipientsData(recipientOptions);
  }
}
