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
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;

@Slf4j
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class AccountController {
	private final UserService userService;
	private final JwtService jwtService;
	private final AccountService accountService;

	public AccountController(UserService userService, JwtService jwtService, AccountService accountService) {
		this.userService = userService;
		this.jwtService = jwtService;
		this.accountService = accountService;
	}
	@GetMapping("/info")
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
	public ResponseEntity<?> usersInfo(Authentication authentication)   {
		try {
			int accountId = jwtService.getAccountIdAuthenticated(authentication);
			UserInfoDto userInfoDto = userService.getUserInfoById(accountId);
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
	@PutMapping("info")
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
	public ResponseEntity<?> updateUserInfo(@RequestBody UpdateUserInfo userInfo, Authentication authentication){
		try {
			int accountId = jwtService.getAccountIdAuthenticated(authentication);
			log.warn("AccountId {}", accountId);
			log.warn(userInfo.toString());
			UserInfo updateUserInfo = userService.updateUserInfo(accountId, userInfo);
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
	public ResponseEntity<?> updatePassword(@RequestBody ChangePasswordDto changePasswordDto,
											Authentication authentication){
		try {

			int accountId = jwtService.getAccountIdAuthenticated(authentication);
			Account account = userService.updatePassword(changePasswordDto, accountId);
			final BasicAuthDto authResponse = jwtService.buildAuthResponse(
					accountService.loadUserByUsername(account.getUsername()),
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




}
