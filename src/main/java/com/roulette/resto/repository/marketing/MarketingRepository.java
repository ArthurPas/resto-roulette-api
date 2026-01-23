package com.roulette.resto.repository.marketing;

import com.roulette.resto.dao.marketing.MarketingDao;
import com.roulette.resto.data.marketing.NewMarketingCampaign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class MarketingRepository {
	final MarketingDao marketingDao;

	public MarketingRepository(MarketingDao marketingDao) {
		this.marketingDao = marketingDao;
	}

	public int createSponsoCampaign(NewMarketingCampaign campaign) {
		return marketingDao.createSponsoCampaign(campaign);
	}
}
