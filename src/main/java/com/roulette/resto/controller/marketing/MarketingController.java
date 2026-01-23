package com.roulette.resto.controller.marketing;

import com.roulette.resto.configuration.JwtService;
import com.roulette.resto.data.marketing.MarketingCampaignInfo;
import com.roulette.resto.data.marketing.NewMarketingCampaign;
import com.roulette.resto.service.marketing.MarketingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/marketing")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
public class MarketingController {

	final JwtService jwtService;
	final MarketingService marketingService;
	public MarketingController(JwtService jwtService, MarketingService marketingService) {
		this.jwtService = jwtService;
		this.marketingService = marketingService;
	}

	@Tag(name = "Marketing | Sponsored post")
	@PostMapping("/sponso-campaign/new")
	@Operation(summary = "Create a campaign for your resto")
	public ResponseEntity<?> createSponsoCampaign(Authentication authentication,
												  @RequestBody NewMarketingCampaign marketingCampaign) {
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		int sponsoId = marketingService.createSponsoCampaign(accountId, marketingCampaign);
		record CampaignResponse(int campaignId){}
		return new ResponseEntity<>( new CampaignResponse(sponsoId), HttpStatus.CREATED);
	}
	@Tag(name = "Marketing | Sponsored post")
	@GetMapping("/sponso-campaign/{campaignId}")
	@Operation(summary = "Get campaign info")
	public ResponseEntity<?> getCampaign(Authentication authentication,
												  @PathVariable String campaignId) {
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		MarketingCampaignInfo marketingCampaignInfo = marketingService.getCampaign(accountId, campaignId);
		return new ResponseEntity<>(marketingCampaignInfo, HttpStatus.CREATED);
	}

}
