package com.roulette.resto.business.social.controllers;

import com.roulette.resto.business.social.dto.in.ChangePasswordDto;
import com.roulette.resto.business.social.dto.in.ResetPasswordDto;
import com.roulette.resto.business.social.dto.in.UpdateUserInfo;
import com.roulette.resto.business.social.dto.out.BasicAuthDto;
import com.roulette.resto.business.social.dto.out.UserInfoDto;
import com.roulette.resto.business.social.entity.Account;
import com.roulette.resto.business.social.entity.UserInfo;
import com.roulette.resto.business.social.services.AccountService;
import com.roulette.resto.business.social.services.UserService;
import com.roulette.resto.common.configuration.JwtService;
import com.roulette.resto.common.dao.SendEmail;
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

import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;

@Slf4j
@RestController
@RequestMapping("/users")
public class AccountController {
	private final UserService userService;
	private final JwtService jwtService;
	private final AccountService accountService;

	public AccountController(UserService userService, JwtService jwtService, AccountService accountService, SendEmail sendEmail) {
		this.userService = userService;
		this.jwtService = jwtService;
		this.accountService = accountService;
	}
	@GetMapping("/info/{accountId}")
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
	public ResponseEntity<?> usersInfo(@PathVariable String accountId)  {
		try {
			UserInfoDto userInfoDto = userService.getUserInfoById(Integer.parseInt(accountId));
			return new ResponseEntity<>(userInfoDto, HttpStatus.OK);
		}catch (AccountNotFoundException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>((new APIError("failed to retrieve user info", e.getMessage())),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PutMapping("info/{id}")
	@Operation(summary = "Update user infos", description = "Update all users info " +
			"since its a put mapping you must send all userinfo that changed or not")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "updated user infos (basicaly what that was sent)",
					content = @Content(mediaType = "application/json",schema = @Schema(implementation =
							UserInfo.class))),
			@ApiResponse(responseCode = "500", description = "Server error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation =APIError.class),examples = {
							@ExampleObject(
									name = "failed to update userinfos",
									value = "{\"message\":\"Database error : failed to update\",\"description\":\"\"}")})),
			@ApiResponse(responseCode = "404", description = "Account not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation =APIError.class),examples = {
							@ExampleObject(
									name = "Account not found",
									value = "{\"message\":\"Account not found\",\"description\":\"\"}")}))})
	public ResponseEntity<?> updateUserInfo(UpdateUserInfo userInfo, @PathVariable String id){
		try {
			UserInfo updateUserInfo = userService.updateUserInfo(id, userInfo);
			return new ResponseEntity<>(updateUserInfo, HttpStatus.OK);
		}
		catch (AccountNotFoundException e){
			return new ResponseEntity<>(new APIError("Account not found", e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			return new ResponseEntity<>(new APIError("Database error : failed to update", e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PatchMapping("password/new")
	@Operation(summary = "Update password", description = "Password update with verification of the old password")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Reauthentication after update",
					content = @Content(mediaType = "application/json",schema = @Schema(implementation =
							BasicAuthDto.class))),
			@ApiResponse(responseCode = "500", description = "Server error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation =APIError.class),examples = {
							@ExampleObject(
									name = "failed to update",
									value = "{\"message\":\"Database error : failed to update\",\"description\":\"\"}")})),
			@ApiResponse(responseCode = "404", description = "Account not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation =APIError.class),examples = {
							@ExampleObject(
									name = "Account not found",
									value = "{\"message\":\"Account not found\",\"description\":\"\"}")}))})
	public ResponseEntity<?> updatePassword(ChangePasswordDto changePasswordDto){
		try {
			Account account = userService.updatePassword(changePasswordDto);
			final BasicAuthDto authResponse = jwtService.buildAuthResponse(
					accountService.loadUserByUsername(changePasswordDto.getLogin()),
					account.getAccountId());
			return new ResponseEntity<>(authResponse, HttpStatus.OK);
		}
		catch (AccountNotFoundException e){
			return new ResponseEntity<>(new APIError("Account not found", e.getMessage()), HttpStatus.NOT_FOUND);
		}
		catch (Exception e){
			log.error(e.getMessage());
			return new ResponseEntity<>(new APIError("Database error : failed to update", e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}



	@PostMapping("/sendVerificationCode/{accountId}")
	public ResponseEntity<?> sendMail(@PathVariable String accountId) {
		try {
			accountService.sendVerificationCode(accountId);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (AccountNotFoundException e) {
			return new ResponseEntity<>(new APIError("Account not found"), HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/password/reset/{accountId}")
	public ResponseEntity<?> resetPassword(@PathVariable String accountId, ResetPasswordDto resetPasswordDto) {
		try {
			userService.resetPassword(accountId, resetPasswordDto);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (AccountNotFoundException e) {
			return new ResponseEntity<>(new APIError("Account not found"), HttpStatus.NOT_FOUND);
		}
	}

}
