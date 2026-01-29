package com.roulette.resto.data.marketing;


import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.entity.UserInfo;
import com.roulette.resto.data.social.mapper.UserInfoRowMapper;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MarketingMapper implements RowMapper<MarketingCampaignInfo> {

	@Override
	public MarketingCampaignInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		MarketingCampaignInfo marketingCampaignInfo = new MarketingCampaignInfo();
		marketingCampaignInfo.setDescription(rs.getString("description"));
		marketingCampaignInfo.setStartDate(rs.getDate("start_date"));
		marketingCampaignInfo.setExpirationDate(rs.getDate("expiration_date"));
		marketingCampaignInfo.setRestoId(rs.getInt("resto_id"));
		marketingCampaignInfo.setCampaignId(rs.getString("campaign_id"));
		marketingCampaignInfo.setMediaId(rs.getString("media_id"));
		marketingCampaignInfo.setPostLocation(rs.getString("post_location"));
		marketingCampaignInfo.setClicks(rs.getInt("click"));
		marketingCampaignInfo.setViews(rs.getInt("viewed"));
		return marketingCampaignInfo;
	}
}