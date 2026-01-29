package com.roulette.resto.repository.marketing;

import com.roulette.resto.dao.marketing.MarketingDao;
import com.roulette.resto.data.marketing.MarketingCampaignInfo;
import com.roulette.resto.data.marketing.NewMarketingCampaign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@Slf4j
public class MarketingRepository {
	final MarketingDao marketingDao;

	public MarketingRepository(MarketingDao marketingDao) {
		this.marketingDao = marketingDao;
	}

	public int createSponsoCampaign(NewMarketingCampaign campaign) {
		try {

			return marketingDao.createSponsoCampaign(campaign);
		}catch (DuplicateKeyException e) {
			throw new DuplicateKeyException(e.getMessage());
		}
	}

	public MarketingCampaignInfo getSponsoCampaign(String campaignId) {
		return marketingDao.getCampaignById(Integer.valueOf(campaignId));
	}

	public void incrementViews(int campaignId, int count) {
		marketingDao.incrementViews(campaignId, count);
	}

	public List<Integer> findAllActiveIds() {
		return marketingDao.findAllActiveIds();
	}

	public void incrementClick(int restoId, int count) {
		marketingDao.incrementClick(restoId, count);
	}


	public List<MarketingCampaignInfo> getCampaignByRestoIds(List<Integer> restoIds) {
		return marketingDao.getCampaignByRestos(restoIds);
	}
}
