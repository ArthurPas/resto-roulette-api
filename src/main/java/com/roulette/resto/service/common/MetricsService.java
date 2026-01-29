package com.roulette.resto.service.common;

import com.roulette.resto.configuration.CampaignCacheWarmer;
import com.roulette.resto.service.marketing.MarketingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.roulette.resto.service.marketing.MarketingService.APPARITION_RATE;

@Service
@Slf4j
public class MetricsService {

	private final StringRedisTemplate redisTemplate;
	private final MarketingService marketingService;

	private static final String KEY_PREFIX = "sponso_campaign:";

	public MetricsService(StringRedisTemplate redisTemplate, MarketingService marketingService) {
		this.redisTemplate = redisTemplate;
		this.marketingService = marketingService;
	}


	@Async
	public void calculateAndIncrementViews() {
		Set<String> activeCampaignIds = redisTemplate.opsForSet().members(CampaignCacheWarmer.ACTIVE_CAMPAIGNS_KEY);

		if (activeCampaignIds == null || activeCampaignIds.isEmpty()) return;

		List<String> selectedIds = new ArrayList<>();
		for (String id : activeCampaignIds) {
			if (shouldIncrement()) {
				selectedIds.add(id);
			}
		}

		redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
			for (String campaignId : selectedIds) {
				connection.stringCommands().incr((KEY_PREFIX + campaignId).getBytes());
			}
			return null;
		});
	}
	@Scheduled(fixedDelay = 60000)
	public void syncCountsToDb() {
		ScanOptions options = ScanOptions.scanOptions().match(KEY_PREFIX + "*").count(100).build();

		try (Cursor<String> cursor = redisTemplate.scan(options)) {
			while (cursor.hasNext()) {
				String key = cursor.next();
				String[] parts = key.split(":");
				if (parts.length < 2) continue;

				String campaignId = parts[1];
				String countStr = redisTemplate.opsForValue().getAndDelete(key);

				if (countStr != null) {
					try {
						int count = Integer.parseInt(countStr);
						if (count > 0) {
							marketingService.incrementViews(Integer.parseInt(campaignId), count);
						}
					} catch (NumberFormatException e) {
						log.error("Erreur de format pour la clé " + key);
					}
				}
			}
		}
	}

	private boolean shouldIncrement() {
		return Math.random() < APPARITION_RATE;
	}
}
