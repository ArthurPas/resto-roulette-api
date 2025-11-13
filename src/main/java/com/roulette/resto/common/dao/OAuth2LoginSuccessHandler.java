package com.roulette.resto.common.dao;

// Importez vos classes (Account, AccountService, JwtService, AuthResponse, etc.)

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roulette.resto.common.configuration.JwtService;
import com.roulette.resto.social.dto.in.RegisterDto;
import com.roulette.resto.social.dto.out.AuthResponse;
import com.roulette.resto.social.entity.Account;
import com.roulette.resto.social.services.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AccountNotFoundException;
import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final AccountService accountService;
	private final JwtService jwtService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public OAuth2LoginSuccessHandler(AccountService accountService, JwtService jwtService) {
		this.accountService = accountService;
		this.jwtService = jwtService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
										HttpServletResponse response,
										Authentication authentication) throws IOException {

		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		String email = oAuth2User.getAttribute("email");

		AuthResponse authResponse;
		HttpStatus status;

		try {
			Account account = accountService.getAccountByEmail(email);
			authResponse = jwtService.buildAuthResponse(account);
			status = HttpStatus.OK;

		} catch (AccountNotFoundException e) {
			RegisterDto registerDto = new RegisterDto();
			registerDto.setEmail(email);
			registerDto.setFirstName(oAuth2User.getAttribute("given_name"));
			registerDto.setLastName(oAuth2User.getAttribute("family_name"));
			registerDto.setLogin(email);
			Account newAccount = accountService.registerAccount(registerDto);
			authResponse = jwtService.buildAuthResponse(newAccount);
			status = HttpStatus.CREATED;
		}
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(response.getWriter(), authResponse);

		SecurityContextHolder.clearContext();
	}
}