package com.roulette.resto.business.social.controllers;

import com.roulette.resto.business.social.dto.UserInfoDto;
import com.roulette.resto.business.social.services.AccountService;
import com.roulette.resto.business.social.services.UserService;
import com.roulette.resto.common.exception.APIError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
	@Operation(summary = "Get information about an user", description = "Get all the information about a user by his " +
			"id including basic info of his profile and an array of all his social interactions with restaurants")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "User infos",
					content = @Content(mediaType = "application/json",schema = @Schema(implementation =
							UserInfoDto.class))),
			@ApiResponse(responseCode = "500", description = "Server error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation =APIError.class),examples = {
							@ExampleObject(
									name = "failed to retrieve user info",
									value = "{\"message\":\"failed to retrieve user info\",\"description\":\"\"}")})),
			@ApiResponse(responseCode = "404", description = "Account not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation =APIError.class),examples = {
							@ExampleObject(
									name = "Account not found",
									value = "{\"message\":\"Account not found\",\"description\":\"\"}")}))})
	public ResponseEntity<?> usersInfo(@PathVariable String id){
		if(!accountService.existsById(Integer.parseInt(id))){
			return new ResponseEntity<>(new APIError("Account not found"), HttpStatus.NOT_FOUND);
		}
		try {
			UserInfoDto userInfoDto = userService.getUserInfoById(Integer.parseInt(id));
			return new ResponseEntity<>(userInfoDto, HttpStatus.OK);
		}catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>((new APIError("failed to retrieve user info", e.getMessage())),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
