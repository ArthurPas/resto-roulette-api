package com.roulette.resto.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@SecurityScheme(
		name = "Bearer Authentication",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		scheme = "bearer")
public class SwaggerConfig implements WebMvcConfigurer {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Resto-roulette API")
						.version("1.0")
						.description(
								"eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImZhbjJyZXN0byIsImlhdCI6MTc2NTAyNjk1Myw" +
										"iZXhwIjoyMDgwMzg2OTUzfQ.Uf9awermXQ1GGGC6gyFMA-IhmobIMOAk8ZjZMgYAtrEDsq7qiqsxfgSah_T2" +
										"LDW8"));
	}

	@Bean
	public GroupedOpenApi adminApi() {
		return GroupedOpenApi.builder()
				.group("Admin")
				.packagesToScan("com.roulette.resto.controller.administration")
				.build();
	}

	@Bean
	public GroupedOpenApi restoApi() {
		return GroupedOpenApi.builder()
				.group("Resto")
				.packagesToScan("com.roulette.resto.controller.resto")
				.build();
	}

	@Bean
	public GroupedOpenApi commonApi() {
		return GroupedOpenApi.builder()
				.group("Common")
				.packagesToScan("com.roulette.resto.controller.common")
				.build();
	}

	@Bean
	public GroupedOpenApi rouletteApi() {
		return GroupedOpenApi.builder()
				.group("Roulette")
				.packagesToScan("com.roulette.resto.controller.roulette")
				.build();
	}

	@Bean
	public GroupedOpenApi socialApi() {
		return GroupedOpenApi.builder()
				.group("Social")
				.packagesToScan("com.roulette.resto.controller.social.interaction")
				.build();
	}
	@Bean
	public GroupedOpenApi accountApi() {
		return GroupedOpenApi.builder()
				.group("Account")
				.packagesToScan("com.roulette.resto.controller.social.account")
				.build();
	}
	@Bean
	public GroupedOpenApi authApi() {
		return GroupedOpenApi.builder()
				.group("Auth")
				.packagesToScan("com.roulette.resto.controller.social.auth")
				.build();
	}
}