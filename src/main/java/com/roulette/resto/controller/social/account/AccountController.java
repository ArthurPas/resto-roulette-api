package com.roulette.resto.controller.social.account;

import com.roulette.resto.configuration.JwtService;
import com.roulette.resto.data.administration.dto.out.AccountsInfos;
import com.roulette.resto.data.common.entity.MediaResource;
import com.roulette.resto.data.resto.entity.Restaurant;
import com.roulette.resto.data.social.dto.in.ChangePasswordDto;
import com.roulette.resto.data.social.dto.in.DeleteAccount;
import com.roulette.resto.data.social.dto.in.UpdateAccountInfo;
import com.roulette.resto.data.social.dto.in.UpdateUserInfo;
import com.roulette.resto.data.social.dto.out.BasicAuthDto;
import com.roulette.resto.data.social.dto.out.UserInfoDto;
import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.entity.MinimalAccountInfo;
import com.roulette.resto.data.social.entity.UserInfo;
import com.roulette.resto.exception.APIError;
import com.roulette.resto.exception.ErrorResponse;
import com.roulette.resto.service.administration.AdminService;
import com.roulette.resto.service.resto.RestoService;
import com.roulette.resto.service.social.AccountService;
import com.roulette.resto.service.social.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

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
	private final RestoService restoService;

	public AccountController(UserService userService, JwtService jwtService, AdminService adminService, AccountService accountService, RestoService restoService) {
		this.userService = userService;
		this.jwtService = jwtService;
		this.adminService = adminService;
		this.accountService = accountService;
		this.restoService = restoService;
	}

	@GetMapping("/me")
	@Tag(name = "Account | User profile")
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
	public ResponseEntity<UserInfoDto> usersInfo(Authentication authentication) {
			int accountId = jwtService.getAccountIdAuthenticated(authentication);
			UserInfoDto userInfoDto = userService.getUserInfoById(accountId);
			return new ResponseEntity<>(userInfoDto, HttpStatus.OK);
	}

	@GetMapping("/owned-restaurants")
	@Tag(name = "Account | User profile")
	@Operation(summary = "Get restaurant(s) that user owns")
	public ResponseEntity<List<Restaurant>> getOwnedRestos(Authentication authentication) {
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		List<Restaurant> restaurants = restoService.getRestosByOwnerId(accountId);
		return new ResponseEntity<>(restaurants, HttpStatus.OK);
	}

	@PutMapping("info")
	@Tag(name = "Account | User profile")
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
	public ResponseEntity<UserInfo> updateUserInfo(@RequestBody UpdateUserInfo userInfo, Authentication authentication) {
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		UserInfo updateUserInfo = userService.updateUserPersonalInfo(accountId, userInfo);
		return new ResponseEntity<>(updateUserInfo, HttpStatus.OK);
	}

	@PostMapping(path = "/add-avatar", consumes = MULTIPART_FORM_DATA_VALUE)
	@Tag(name = "Account | User profile")
	@Operation(summary = "Add user profile picture")
	public ResponseEntity<MediaResource> addUserAvatar(Authentication authentication,
											   @RequestParam() MultipartFile avatar) {
		int accountId = jwtService.getAccountIdAuthenticated(authentication);
		MediaResource mediaResource = userService.addUserAvatar(accountId,avatar);
		return new ResponseEntity<>(mediaResource, HttpStatus.CREATED);
	}

	@PatchMapping("password/new")
	@Tag(name = "Account | User profile")
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
	public ResponseEntity<BasicAuthDto> updatePassword(@RequestBody ChangePasswordDto changePasswordDto,
											Authentication authentication) {
			int accountId = jwtService.getAccountIdAuthenticated(authentication);
			Account account = userService.updatePassword(changePasswordDto, accountId);
			final BasicAuthDto authResponse = jwtService.buildAuthResponse(account);
			return new ResponseEntity<>(authResponse, HttpStatus.OK);
	}

	@GetMapping("/admin/get-all")
	@Tag(name = "Account | Admin view")
	@Operation(summary = "Get all users accounts info")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Accounts info",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = AccountsInfos.class)))})
	public ResponseEntity<List<AccountsInfos>> getTotalUsersRegistrations(Authentication authentication, @RequestParam(required =
			false
			, defaultValue = "0") int page) {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(userService.getAccounts(page), HttpStatus.OK);
	}

	@PatchMapping("/admin/update-role")
	@Tag(name = "Account | Admin view")
	@Operation(summary = "Change account role", description = "Change the role of the account" +
			" The role values available are : ROLE_USER, ROLE_RESTAURANT_OWNER, ROLE_MODERATOR, ROLE_ADMIN")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Total and trend data",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = UserInfoDto.class)))})
	public ResponseEntity<UserInfoDto> modifyAccountRole (Authentication authentication,
												@RequestBody UpdateAccountInfo updateAccountInfo) {
			adminService.rightCheckIsAdmin(authentication);
			int accountId = userService.updateUserRole(updateAccountInfo);
			UserInfoDto userInfoDto = userService.getUserInfoById(accountId);
			return new ResponseEntity<>(userInfoDto, HttpStatus.OK);
	}

	@DeleteMapping("/admin/delete-account")
	@Tag(name = "Account | Admin view")
	@Operation(summary = "Delete account")
	public ResponseEntity<Void> deleteAccount(@RequestBody DeleteAccount deleteAccount, Authentication authentication) {
			adminService.rightCheckIsAdmin(authentication);
			accountService.deleteUser(deleteAccount);
			return new ResponseEntity<>(HttpStatus.OK);
	}
	@PatchMapping("/admin/recover-deleted-account")
	@Tag(name = "Account | Admin view")
	@Operation(summary = "Recover deleted account")
	public ResponseEntity<Void> recoverAccount(@RequestBody DeleteAccount deleteAccount, Authentication authentication) {
			adminService.rightCheckIsAdmin(authentication);
			accountService.recoverUser(deleteAccount);
			return new ResponseEntity<>(HttpStatus.OK);
	}
	@GetMapping("/admin/{user_login}")
	@Tag(name = "Account | Admin view")
	@Operation(summary = "Get specific user info")
	public ResponseEntity<UserInfoDto> recoverAccount(@PathVariable String user_login, Authentication authentication) {
		adminService.rightCheckIsAdmin(authentication);
		UserInfoDto userInfoDto = userService.getUserInfoByLogin(user_login);
		return new ResponseEntity<>(userInfoDto, HttpStatus.OK);
	}
	@GetMapping("/search/{user_login}")
	@Tag(name = "Account | Admin view")
	@Operation(summary = "Search users")
	public ResponseEntity<List<MinimalAccountInfo>> searchUsers(@PathVariable String user_login,
														Authentication authentication) {
		adminService.rightCheckIsAdmin(authentication);
		List<MinimalAccountInfo> users = userService.getUserMatchByLogin(user_login);
		return new ResponseEntity<>(users, HttpStatus.OK);
	}
}
