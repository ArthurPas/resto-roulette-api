package com.roulette.resto.data.marketing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewMarketingCampaign {
	int restoId;
	String mediaId;
	Date startDate;
	Date expirationDate;
	String description;
	PostLocation location;
}
