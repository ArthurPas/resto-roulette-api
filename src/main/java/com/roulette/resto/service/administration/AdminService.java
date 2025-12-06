package com.roulette.resto.service.administration;

import com.roulette.resto.configuration.JwtService;
import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.entity.UserRole;
import com.roulette.resto.exception.APIError;
import com.roulette.resto.service.social.AccountService;
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
