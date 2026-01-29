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

	private static final String KEY_SPONSO_VIEW = "sponso_campaign:";
	private static final String KEY_RESTO_CLICK = "resto_click:";

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

		if (selectedIds.isEmpty()) return;

		redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
			for (String campaignId : selectedIds) {
				connection.stringCommands().incr((KEY_SPONSO_VIEW + campaignId).getBytes());
			}
			return null;
		});
	}

	@Async
	public void calculateAndIncrementRestoClick(String restoId) {
		redisTemplate.opsForValue().increment(KEY_RESTO_CLICK + restoId);
		log.debug("Incremented click for resto: {}", restoId);
	}

	@Scheduled(fixedDelay = 60000)
	public void syncCountsToDb() {
		syncType(KEY_SPONSO_VIEW, true);
		syncType(KEY_RESTO_CLICK, false);
	}

	private void syncType(String prefix, boolean isView) {
		ScanOptions options = ScanOptions.scanOptions().match(prefix + "*").count(100).build();

		try (Cursor<String> cursor = redisTemplate.scan(options)) {
			while (cursor.hasNext()) {
				String key = cursor.next();
				String idStr = key.substring(prefix.length());
				String countStr = redisTemplate.opsForValue().getAndDelete(key);

				if (countStr != null) {
					try {
						int count = Integer.parseInt(countStr);
						int id = Integer.parseInt(idStr);

						if (count > 0) {
							if (isView) {
								marketingService.incrementViews(id, count);
							} else {
								marketingService.incrementRestoClicks(id, count);
							}
						}
					} catch (NumberFormatException e) {
						log.error("Erreur de parsing pour l'ID ou le compteur sur la clé : {}", key);
					}
				}
			}
		}
	}

	private boolean shouldIncrement() {
		return Math.random() < APPARITION_RATE;
	}
}