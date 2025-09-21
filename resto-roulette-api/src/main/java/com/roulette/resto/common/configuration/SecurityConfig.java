package com.roulette.resto.common.configuration;

import com.roulette.resto.business.social.services.AccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	final AccountService accountService;
	static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public SecurityConfig(AccountService accountService) {
		this.accountService = accountService;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						.anyRequest().permitAll()
				)
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable);

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
	public static PasswordEncoder passwordEncoder(){
		return passwordEncoder;
	}
}