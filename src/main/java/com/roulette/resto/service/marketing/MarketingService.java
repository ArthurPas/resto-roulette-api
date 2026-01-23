package com.roulette.resto.service.marketing;

import com.roulette.resto.data.marketing.NewMarketingCampaign;
import com.roulette.resto.data.resto.entity.Restaurant;
import com.roulette.resto.exception.APIError;
import com.roulette.resto.repository.marketing.MarketingRepository;
import com.roulette.resto.service.resto.RestoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MarketingService {

	final MarketingRepository marketingRepository;
	final RestoService restoService;
	public MarketingService(MarketingRepository marketingRepository, RestoService restoService) {
		this.marketingRepository = marketingRepository;
		this.restoService = restoService;
	}

	public int createSponsoCampaign(int accountId, NewMarketingCampaign marketingCampaign) {
		List<Restaurant> ownedResto = restoService.getRestosByOwnerId(accountId);
		boolean owned = false;
		for (Restaurant restaurant : ownedResto) {
			if(restaurant.getId() == marketingCampaign.getRestoId()){
				owned = true;
				break;
			}
		}
		if(!owned) {
			throw new APIError(21, HttpStatus.FORBIDDEN);
		}
		return marketingRepository.createSponsoCampaign(marketingCampaign);

	}
}
