package com.roulette.resto.configuration;

import com.roulette.resto.controller.marketing.CampaignInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebInterceptorConfig implements WebMvcConfigurer {

	private final CampaignInterceptor campaignInterceptor;

	public WebInterceptorConfig(CampaignInterceptor campaignInterceptor) {
		this.campaignInterceptor = campaignInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(campaignInterceptor)
				.addPathPatterns("/activities/followers/feed")
				.addPathPatterns("/restos/**");
	}
}
