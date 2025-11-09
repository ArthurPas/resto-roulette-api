package com.roulette.resto.social.controllers;

import com.roulette.resto.administration.dto.out.AccountsInfos;
import com.roulette.resto.administration.services.AdminService;
import com.roulette.resto.common.configuration.JwtService;
import com.roulette.resto.common.exception.APIError;
import com.roulette.resto.common.exception.ErrorResponse;
import com.roulette.resto.social.dto.in.ChangePasswordDto;
import com.roulette.resto.social.dto.in.DeleteAccount;
import com.roulette.resto.social.dto.in.UpdateAccountInfo;
import com.roulette.resto.social.dto.in.UpdateUserInfo;
import com.roulette.resto.social.dto.out.BasicAuthDto;
import com.roulette.resto.social.dto.out.UserInfoDto;
import com.roulette.resto.social.entity.Account;
import com.roulette.resto.social.entity.UserInfo;
import com.roulette.resto.social.services.AccountService;
import com.roulette.resto.social.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;

@Slf4j
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
public class AccountController {
	private final UserService userService;
	private final JwtService jwtService;
	private final AdminService adminService;
	private final AccountService accountService;

	public AccountController(UserService userService, JwtService jwtService, AdminService adminService, AccountService accountService) {
		this.userService = userService;
		this.jwtService = jwtService;
		this.adminService = adminService;
		this.accountService = accountService;
	}

	@GetMapping("/me")
	@Operation(summary = "Get information about an user", description = "Get all the information about a user by his " +
			"id including basic info of his profile and an array of all his social interactions with restaurants")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "User infos",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation =
							UserInfoDto.class))),
			@ApiResponse(responseCode = "500", description = "Server error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = APIError.class), examples = {
							@ExampleObject(
									name = "failed to retrieve user info",
									value = "{\"message\":\"failed to retrieve user info\",\"description\":\"\"}")})),
			@ApiResponse(responseCode = "404", description = "Account not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = APIError.class), examples = {
							@ExampleObject(
									name = "Account not found",
									value = "{\"message\":\"Account not found\",\"description\":\"\"}")}))})
	public ResponseEntity<?> usersInfo(Authentication authentication) {
		try {
			int accountId = jwtService.getAccountIdAuthenticated(authentication);
			UserInfoDto userInfoDto = userService.getUserInfoById(accountId);
			return new ResponseEntity<>(userInfoDto, HttpStatus.OK);
		}catch (AccountNotFoundException e) {
			ErrorResponse errorResponse = new ErrorResponse(new APIError(64, HttpStatus.NOT_FOUND));
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}

	@PatchMapping("info")
	@Operation(summary = "Update user infos", description = "Update all users info " +
			"send in the body all userinfo that changed or not")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "updated user infos (basicaly what that was sent)",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation =
							UserInfo.class))),
			@ApiResponse(responseCode = "500", description = "Server error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = APIError.class), examples = {
							@ExampleObject(
									name = "failed to update userinfos",
									value = "{\"message\":\"Database error : failed to update\",\"description\":\"\"}")})),
			@ApiResponse(responseCode = "404", description = "Account not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = APIError.class), examples = {
							@ExampleObject(
									name = "Account not found",
									value = "{\"message\":\"Account not found\",\"description\":\"\"}")}))})
	public ResponseEntity<?> updateUserInfo(@RequestBody UpdateUserInfo userInfo, Authentication authentication) {
		try {
			int accountId = jwtService.getAccountIdAuthenticated(authentication);
			UserInfo updateUserInfo = userService.updateUserPersonalInfo(accountId, userInfo);
			return new ResponseEntity<>(updateUserInfo, HttpStatus.OK);
		} catch (AccountNotFoundException e) {
			ErrorResponse errorResponse = new ErrorResponse(new APIError(64, HttpStatus.NOT_FOUND));
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		} catch (SQLException e) {
			ErrorResponse errorResponse = new ErrorResponse(new APIError(500,
					HttpStatus.INTERNAL_SERVER_ERROR));
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}

	@PatchMapping("password/new")
	@Operation(summary = "Update password", description = "Password update with verification of the old password")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Reauthentication after update",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation =
							BasicAuthDto.class))),
			@ApiResponse(responseCode = "500", description = "Server error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = APIError.class), examples = {
							@ExampleObject(
									name = "failed to update",
									value = "{\"message\":\"Database error : failed to update\",\"description\":\"\"}")})),
			@ApiResponse(responseCode = "404", description = "Account not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = APIError.class), examples = {
							@ExampleObject(
									name = "Account not found",
									value = "{\"message\":\"Account not found\",\"description\":\"\"}")}))})
	public ResponseEntity<?> updatePassword(@RequestBody ChangePasswordDto changePasswordDto,
											Authentication authentication) {
		try {

			int accountId = jwtService.getAccountIdAuthenticated(authentication);
			Account account = userService.updatePassword(changePasswordDto, accountId);
			final BasicAuthDto authResponse = jwtService.buildAuthResponse(account);
			return new ResponseEntity<>(authResponse, HttpStatus.OK);
		} catch (AccountNotFoundException e) {
			ErrorResponse errorResponse = new ErrorResponse(new APIError(64, HttpStatus.NOT_FOUND));
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}

	@GetMapping("/admin/get-all")
	@Operation(summary = "Get all users accounts info")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Accounts info",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = AccountsInfos.class)))})
	public ResponseEntity<?> getTotalUsersRegistrations(Authentication authentication, @RequestParam(required = false
			,defaultValue = "0")int page) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(userService.getAccounts(page), HttpStatus.OK);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}

	@PatchMapping("/admin/update-role")
	@Operation(summary = "Change account role", description = "Change the role of the account" +
			" The role values available are : ROLE_USER, ROLE_RESTAURANT_OWNER, ROLE_MODERATOR, ROLE_ADMIN")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Total and trend data",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = UserInfoDto.class)))})
	public ResponseEntity<?> modifyAccountRole (Authentication authentication,
												@RequestBody UpdateAccountInfo updateAccountInfo) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			int accountId = userService.updateUserRole(updateAccountInfo);
			UserInfoDto userInfoDto = userService.getUserInfoById(accountId);
			return new ResponseEntity<>(userInfoDto, HttpStatus.OK);
		} catch (AccountNotFoundException e) {
			ErrorResponse errorResponse = new ErrorResponse(new APIError(64, HttpStatus.NOT_FOUND));
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}

	@DeleteMapping("/admin/delete-account")
	@Operation(summary = "Delete account")
	public ResponseEntity<?> deleteAccount(@RequestBody DeleteAccount deleteAccount, Authentication authentication) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			accountService.deteleUser(deleteAccount);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (AccountNotFoundException e) {
			ErrorResponse errorResponse = new ErrorResponse(new APIError(64, HttpStatus.NOT_FOUND));
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
		}
	}
}
