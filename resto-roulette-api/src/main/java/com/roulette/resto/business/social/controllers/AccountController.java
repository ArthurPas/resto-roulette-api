package com.roulette.resto.business.social.controllers;

import com.roulette.resto.business.social.dto.UserInfoDto;
import com.roulette.resto.business.social.services.AccountService;
import com.roulette.resto.business.social.services.UserService;
import com.roulette.resto.common.exception.APIError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/users")
public class AccountController {
	private final UserService userService;
	private final AccountService accountService;

	public AccountController(UserService userService, AccountService accountService) {
		this.userService = userService;
		this.accountService = accountService;
	}
	@GetMapping("/info/{id}")
	public ResponseEntity<?> usersInfo(@PathVariable String id){
		if(!accountService.existsById(Integer.parseInt(id))){
			return new ResponseEntity<>(new APIError("Account not found"), HttpStatus.BAD_REQUEST);
		}
		try {
			UserInfoDto userInfoDto = userService.getUserInfoById(Integer.parseInt(id));
			return new ResponseEntity<>(userInfoDto, HttpStatus.OK);
		}catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>((new APIError("Error : failed to retrieve user info", e.getMessage())),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
