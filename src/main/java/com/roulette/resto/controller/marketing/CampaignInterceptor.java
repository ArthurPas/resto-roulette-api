package com.roulette.resto.controller.marketing;

import com.roulette.resto.service.common.MetricsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import java.util.Map;

@Component
@Slf4j
public class CampaignInterceptor implements HandlerInterceptor {

	private final MetricsService metricsService;

	public CampaignInterceptor(MetricsService metricsService) {
		this.metricsService = metricsService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		metricsService.calculateAndIncrementViews();
		log.info("CampaignInterceptor preHandle");
		return true;
	}
}