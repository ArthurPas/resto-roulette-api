package com.roulette.resto.controller.marketing;

import com.roulette.resto.configuration.TrackCampaign;
import com.roulette.resto.service.common.MetricsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Component
@Slf4j
public class CampaignInterceptor implements HandlerInterceptor {

	private final MetricsService metricsService;

	public CampaignInterceptor(MetricsService metricsService) {
		this.metricsService = metricsService;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView){
		log.info("CampaignInterceptor postHandle");
		if (handler instanceof HandlerMethod handlerMethod) {
			TrackCampaign annotation = handlerMethod.getMethodAnnotation(TrackCampaign.class);
			switch (annotation.type()) {
				case "Resto":
					Map<String, String> pathVariables =
							(Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
					if (pathVariables != null && pathVariables.containsKey("id")) {
						String restoId = pathVariables.get("id");
						metricsService.calculateAndIncrementRestoClick(restoId);
					}
					break;
				case "Feed":
					metricsService.calculateAndIncrementViews();
					break;
			}
		}
	}
}