package com.roulette.resto.common.configuration;

import com.roulette.resto.business.social.services.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.authorization.SingleResultAuthorizationManager.permitAll;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	final AccountService accountService;
	static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	final JwtAuthenticationFilter jwtAuthenticationFilter;
	public SecurityConfig(AccountService accountService, JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.accountService = accountService;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, AccountService userDetailsService) throws Exception {
		http.cors(Customizer.withDefaults());
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		http.authorizeHttpRequests(auth -> auth
				// Public endpoints
				.requestMatchers("/auth/**").permitAll()
				.requestMatchers(
						"/v3/api-docs/**",
						"/swagger-ui/**",
						"/swagger-ui.html",
						"/swagger-resources/**",
						"/webjars/**"
				).permitAll()
				.requestMatchers("/users/**").authenticated()
				.requestMatchers("/kpi/**").authenticated()
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
		).oauth2Login(new Customizer<OAuth2LoginConfigurer<HttpSecurity>>() {
			@Override
			public void customize(OAuth2LoginConfigurer<HttpSecurity> httpSecurityOAuth2LoginConfigurer) {

			}
		});

		http.httpBasic(Customizer.withDefaults());
		http.userDetailsService(userDetailsService);
		http.csrf(AbstractHttpConfigurer::disable);
		return http.build();
	}


	@Bean
	public AuthenticationManager authenticationManager( PasswordEncoder passwordEncoder) {
		System.out.println("in authenticationManager");
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(accountService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);

		return new ProviderManager(authenticationProvider);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("*")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("Authorization", "Content-Type", "Accept")
						.exposedHeaders("Authorization")
						.allowCredentials(false);
			}
		};
	}
	@Bean
	public static PasswordEncoder passwordEncoder(){
		return passwordEncoder;
	}
}