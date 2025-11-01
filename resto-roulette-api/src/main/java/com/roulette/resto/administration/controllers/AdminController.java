package com.roulette.resto.administration.controllers;

import com.roulette.resto.administration.services.AdminService;
import com.roulette.resto.common.exception.APIError;
import com.roulette.resto.common.exception.ErrorResponse;
import com.roulette.resto.social.dto.in.UpdateAccountInfo;
import com.roulette.resto.social.dto.out.UserInfoDto;
import com.roulette.resto.social.services.AccountService;
import com.roulette.resto.social.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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

@Slf4j
@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
@SecurityRequirement(name="Bearer Authentication")
public class AdminController {
	private final AccountService accountService;
	private final UserService userService;
	private final AdminService adminService;
	public AdminController(AccountService accountService, UserService userService, AdminService adminService) {
		this.accountService = accountService;
		this.userService = userService;
		this.adminService = adminService;
	}

	@GetMapping("/users")
	public ResponseEntity<?> getTotalUsersRegistrations(Authentication authentication, @RequestParam(required = false
			,defaultValue = "0")int page) {
		try {
			adminService.rightCheckIsAdmin(authentication);
			return new ResponseEntity<>(userService.getAccounts(page), HttpStatus.OK);
		} catch (APIError e) {
			return new ResponseEntity<>(new ErrorResponse(e.getMessage()), e.getStatus());
		}
	}

	@PatchMapping("/update-role")
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
			int accountId = userService.updateAccountInfo(updateAccountInfo);
			UserInfoDto userInfoDto = userService.getUserInfoById(accountId);
			return new ResponseEntity<>(userInfoDto, HttpStatus.OK);
		} catch (APIError e) {
			return new ResponseEntity<>(new ErrorResponse(e.getMessage()), e.getStatus());
		} catch (AccountNotFoundException e) {
			return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}


}
