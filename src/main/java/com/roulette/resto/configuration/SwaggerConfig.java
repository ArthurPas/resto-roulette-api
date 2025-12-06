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
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
				.group("resto-roulette")
				.pathsToMatch("/**")
				.build();
	}

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
}