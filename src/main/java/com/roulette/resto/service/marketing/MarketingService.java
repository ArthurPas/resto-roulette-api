package com.roulette.resto.service.marketing;

import com.roulette.resto.data.marketing.MarketingCampaignInfo;
import com.roulette.resto.data.marketing.NewMarketingCampaign;
import com.roulette.resto.data.resto.entity.Restaurant;
import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.exception.APIError;
import com.roulette.resto.repository.marketing.MarketingRepository;
import com.roulette.resto.service.resto.RestoService;
import com.roulette.resto.service.social.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class MarketingService {
	// 1 post out of 10 is sponso
	public final static double APPARITION_RATE = 0.1;

	final MarketingRepository marketingRepository;
	final RestoService restoService;
	private final AccountService accountService;

	public MarketingService(MarketingRepository marketingRepository, RestoService restoService, AccountService accountService) {
		this.marketingRepository = marketingRepository;
		this.restoService = restoService;
		this.accountService = accountService;
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

	public void incrementRestoClicks(int restoId, int count) {
		marketingRepository.incrementClick(restoId, count);
	}

	public List<MarketingCampaignInfo> getCampaignByAccountId(int accountId)  {
		List<Restaurant> restosOwned = restoService.getRestosByOwnerId(accountId);
		List<Integer> restoIds = new ArrayList<>();
 		restosOwned.forEach(restaurant ->restoIds.add(restaurant.getId()));
		return marketingRepository.getCampaignByRestoIds(restoIds);
	}
}
