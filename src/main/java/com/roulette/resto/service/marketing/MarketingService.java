package com.roulette.resto.service.marketing;

import com.roulette.resto.data.marketing.MarketingCampaignInfo;
import com.roulette.resto.data.marketing.NewMarketingCampaign;
import com.roulette.resto.data.resto.entity.Restaurant;
import com.roulette.resto.exception.APIError;
import com.roulette.resto.repository.marketing.MarketingRepository;
import com.roulette.resto.service.resto.RestoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class MarketingService {
	// 1 post out of 10 is sponso
	public final static double APPARITION_RATE = 0.1;

	final MarketingRepository marketingRepository;
	final RestoService restoService;
	public MarketingService(MarketingRepository marketingRepository, RestoService restoService) {
		this.marketingRepository = marketingRepository;
		this.restoService = restoService;
	}

	public int createSponsoCampaign(int accountId, NewMarketingCampaign marketingCampaign) {
		boolean owned = restoService.isRestoOwnerByAccountId(marketingCampaign.getRestoId(), accountId);
		if(!owned) {
			throw new APIError(21, HttpStatus.FORBIDDEN);
		}
		try {
			return marketingRepository.createSponsoCampaign(marketingCampaign);
		}catch (DuplicateKeyException e) {
			throw new APIError(24, HttpStatus.BAD_REQUEST);
		}
	}

	public MarketingCampaignInfo getCampaign(int accountId, String campaignId) {
		try {

			MarketingCampaignInfo marketingCampaignInfo = marketingRepository.getSponsoCampaign(campaignId);
			boolean owned = restoService.isRestoOwnerByAccountId(marketingCampaignInfo.getRestoId(), accountId);
			if(!owned) {
				throw new APIError(21, HttpStatus.FORBIDDEN);
			}
			return marketingCampaignInfo;
		}catch (NoSuchElementException e) {
			throw new APIError(24, HttpStatus.NOT_FOUND);
		}
	}

	public void incrementViews(int campaignId, int count) {
		marketingRepository.incrementViews(campaignId, count);
	}
}
