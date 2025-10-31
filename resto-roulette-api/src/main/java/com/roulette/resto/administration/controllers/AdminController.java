package com.roulette.resto.administration.controllers;

import com.roulette.resto.administration.services.AdminService;
import com.roulette.resto.social.services.AccountService;
import com.roulette.resto.social.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
		final ResponseEntity<?> UNAUTHORIZED = adminService.rightCheckIsAdmin(authentication);
		if(UNAUTHORIZED != null) return UNAUTHORIZED;
		try {
			return new ResponseEntity<>(userService.getAccounts(page), HttpStatus.OK);
		}catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
