package com.roulette.resto.administration.services;

import com.roulette.resto.common.configuration.JwtService;
import com.roulette.resto.common.exception.APIError;
import com.roulette.resto.social.entity.Account;
import com.roulette.resto.social.entity.UserRole;
import com.roulette.resto.social.services.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
@Service
@Slf4j
public class AdminService {

	private final JwtService jwtService;
	private final AccountService accountService;
	public AdminService(JwtService jwtService, AccountService accountService) {
		this.jwtService = jwtService;
		this.accountService = accountService;
	}

	public void rightCheckIsAdmin(Authentication authentication) throws APIError {
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		try {
			Account account = accountService.getAccountById(accountId);
			log.warn("Account role : {}", account.getUserInfo().getRole().toString());
			if(account.getUserInfo().getRole()!= UserRole.ROLE_ADMIN){
				throw new APIError(71,
						HttpStatus.UNAUTHORIZED);
			}
		}catch (AccountNotFoundException e) {
			 throw new APIError(64,
					HttpStatus.NOT_FOUND);
		}
	}
}
