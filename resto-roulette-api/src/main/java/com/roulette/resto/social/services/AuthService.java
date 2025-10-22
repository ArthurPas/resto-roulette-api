package com.roulette.resto.social.services;

import com.roulette.resto.common.exception.APIError;
import com.roulette.resto.social.dto.in.LoginDto;
import com.roulette.resto.social.dto.in.RegisterDto;
import com.roulette.resto.social.entity.Account;
import com.roulette.resto.social.repository.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;

@Slf4j
@Service
public class AuthService {

	private final AccountService accountService;
	private final AuthenticationManager authenticationManager;
	public AuthService(AccountService accountService, AuthenticationManager authenticationManager) {
		this.accountService = accountService;
		this.authenticationManager = authenticationManager;
	}

	public Account getAccountFromLoginRequest(LoginDto loginDto) throws APIError {
		try {

			if(loginDto.getLogin() != null) {
				return accountService.getAccountByLogin(loginDto.getLogin());
			} else if(loginDto.getEmail() != null) {
				return accountService.getAccountByEmail(loginDto.getEmail());
			} else {
				throw new APIError("Login request must have at least login or email", HttpStatus.BAD_REQUEST);
			}
		} catch (AccountNotFoundException e) {
			throw new APIError("Account not found", HttpStatus.NOT_FOUND);
		}
	}
	public void authenticate(LoginDto loginDto, HttpServletRequest request, Account account) throws APIError {
		try {
			UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(account.getLogin(),
					loginDto.getPassword());
			Authentication auth = authenticationManager.authenticate(authReq);
			SecurityContext sc = SecurityContextHolder.getContext();
			sc.setAuthentication(auth);
			HttpSession session = request.getSession();
			session.setAttribute("SPRING_SECURITY_CONTEXT", sc);
		}catch (AuthenticationException e) {
			throw new APIError(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}

	}

	public boolean checkIfExists(RegisterDto registerDto) throws APIError {
		if(accountService.existsByLogin(registerDto.getLogin())) {
			throw new APIError("Login already exist", HttpStatus.BAD_REQUEST);
		}
		if(accountService.existsByEmail(registerDto.getEmail())) {
			throw new APIError("Email already exist", HttpStatus.BAD_REQUEST);
		}
		return true;
	}
}
