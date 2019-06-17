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
package org.apache.fineract.infrastructure.campaigns.email.constants;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum EmailCampaignSendTo {
  INVALID(-1, "email.campaign.sendto.invalid"),
  CLIENTS(1, "email.campaign.sendto.clients"),
  OFFICERS(2, "email.campaign.sendto.officers");

  private Integer value;
  private String code;

  private EmailCampaignSendTo(Integer value, String code) {
    this.value = value;
    this.code = code;
  }

  public Integer getValue() {
    return value;
  }

  public String getCode() {
    return code;
  }

  public static EmailCampaignSendTo fromInt(final Integer typeValue) {
    EmailCampaignSendTo type = null;
    switch (typeValue) {
      case 1:
        type = CLIENTS;
        break;
      case 2:
        type = OFFICERS;
        break;
    }
    return type;
  }

  public static EnumOptionData toOption(final EmailCampaignSendTo emailCampaignSendTo) {
    EnumOptionData optionData = new EnumOptionData(EmailCampaignSendTo.INVALID.getValue().longValue(),
                                                   EmailCampaignSendTo.INVALID.getCode(), "Invalid");
    switch (emailCampaignSendTo) {
      case INVALID:
        optionData = new EnumOptionData(EmailCampaignSendTo.INVALID.getValue().longValue(),
                                        EmailCampaignSendTo.INVALID.getCode(), "Invalid");
        break;
      case CLIENTS:
        optionData = new EnumOptionData(EmailCampaignSendTo.CLIENTS.getValue().longValue(),
                                        EmailCampaignSendTo.CLIENTS.getCode(), "Clients");
        break;
      case OFFICERS:
        optionData = new EnumOptionData(EmailCampaignSendTo.OFFICERS.getValue().longValue(),
                                        EmailCampaignSendTo.OFFICERS.getCode(), "Officers");
        break;
    }
    return optionData;
  }

  public boolean isClient(){
    return this.value.equals(EmailCampaignSendTo.CLIENTS.getValue());
  }

  public boolean isStaff(){
    return this.value.equals(EmailCampaignSendTo.OFFICERS.getValue());
  }
}
