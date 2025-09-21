package com.roulette.resto.business.social.controllers;

import com.roulette.resto.business.social.dto.AuthResponse;
import com.roulette.resto.business.social.dto.UserInfoDto;
import com.roulette.resto.business.social.repository.AccountRepository;
import com.roulette.resto.business.social.services.AccountService;
import com.roulette.resto.business.social.services.UserService;
import com.roulette.resto.common.configuration.JwtService;
import com.roulette.resto.common.exception.APIError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
@Slf4j
@RestController
@RequestMapping("/users")
public class AccountController {
	private final JwtService jwtService;
	private final UserService userService;
	private final AccountService accountService;

	public AccountController(JwtService jwtService, UserService userService, AccountService accountService) {
		this.jwtService = jwtService;
		this.userService = userService;
		this.accountService = accountService;
	}
	@GetMapping("/info/{login}")
	public ResponseEntity<?> registerUser(@PathVariable String login) throws AccountNotFoundException {
		if(!accountService.existsByLogin(login)){
			return new ResponseEntity<>(new AccountNotFoundException("Account not found"), HttpStatus.BAD_REQUEST);
		}
		try {
			UserInfoDto userInfoDto = userService.getUserInfoByLogin(login);
			return new ResponseEntity<>(userInfoDto, HttpStatus.OK);
		}catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>((new APIError("DB error while creating account", e.getMessage())),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
