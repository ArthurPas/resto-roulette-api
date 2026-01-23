package com.roulette.resto.data.marketing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketingCampaignInfo {
	String campaignId;
	int restoId;
	String mediaId;
	Date startDate;
	Date expirationDate;
	String description;
}
