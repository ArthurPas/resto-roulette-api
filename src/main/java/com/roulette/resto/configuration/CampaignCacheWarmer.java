package com.roulette.resto.configuration;

import com.roulette.resto.repository.marketing.MarketingRepository;
import com.roulette.resto.service.marketing.MarketingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CampaignCacheWarmer implements CommandLineRunner {

	private final MarketingRepository marketingRepository;
	private final StringRedisTemplate redisTemplate;

	public static final String ACTIVE_CAMPAIGNS_KEY = "ref:active_campaigns";

	public CampaignCacheWarmer(MarketingRepository marketingRepository, StringRedisTemplate redisTemplate) {
		this.marketingRepository = marketingRepository;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void run(String... args) {
		refreshCache();
	}
	public void refreshCache() {
		try {
			var ids = marketingRepository.findAllActiveIds();

			List<String> activeIds = ids.stream().map(String::valueOf).toList();

			if (!activeIds.isEmpty()) {
				redisTemplate.delete(ACTIVE_CAMPAIGNS_KEY);
				redisTemplate.opsForSet().add(ACTIVE_CAMPAIGNS_KEY, activeIds.toArray(new String[0]));
			} else {
				log.warn("no active campaigns found, redis empty");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Scheduled(fixedRate = 300000)
	public void refreshCampaignList() {
		this.refreshCache();
	}
}
