package com.roulette.resto.social.services;

import com.roulette.resto.common.exception.APIError;
import com.roulette.resto.social.dto.in.LoginDto;
import com.roulette.resto.social.dto.in.RegisterDto;
import com.roulette.resto.social.entity.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class AuthService {

	private final AccountService accountService;
	private final AuthenticationManager authenticationManager;

	public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
			Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	private final UserService userService;

	public static boolean validate(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.matches();
	}
	public AuthService(AccountService accountService, AuthenticationManager authenticationManager, UserService userService) {
		this.accountService = accountService;
		this.authenticationManager = authenticationManager;
		this.userService = userService;
	}

	public Account getAccountFromLoginRequest(LoginDto loginDto) throws APIError {
		try {
			if(loginDto.getLogin().isEmpty()){
				throw new APIError(20, HttpStatus.BAD_REQUEST);
			}
			boolean isEmailUsedAsLogin = validate(loginDto.getLogin());
			if(isEmailUsedAsLogin) {
				return accountService.getAccountByEmail(loginDto.getLogin());
			}
			return accountService.getAccountByLogin(loginDto.getLogin());
		} catch (AccountNotFoundException e) {
			throw new APIError(14, HttpStatus.NOT_FOUND);
		}
	}

	public boolean authenticate(LoginDto loginDto, HttpServletRequest request, Account account) throws APIError {
		if(account.isDeleted()){
			throw new APIError(14, HttpStatus.NOT_FOUND);
		}
		try {
			UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(account.getLogin(),
					loginDto.getPassword());
			Authentication auth = authenticationManager.authenticate(authReq);
			SecurityContext sc = SecurityContextHolder.getContext();
			sc.setAuthentication(auth);
			HttpSession session = request.getSession();
			session.setAttribute("SPRING_SECURITY_CONTEXT", sc);
			userService.updateLoginDate(account);
		} catch (AuthenticationException e) {
			throw new APIError(70, HttpStatus.UNAUTHORIZED);
		}
		return true;
	}

	public boolean checkIfExists(RegisterDto registerDto) throws APIError {
		if(accountService.existsByLogin(registerDto.getLogin())) {
			throw new APIError(601, HttpStatus.BAD_REQUEST);
		}
		if(accountService.existsByEmail(registerDto.getEmail())) {
			throw new APIError(600, HttpStatus.BAD_REQUEST);
		}
		return true;
	}
}
