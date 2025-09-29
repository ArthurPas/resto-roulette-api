package com.roulette.resto.business.social.controllers;

import com.roulette.resto.business.social.dto.AuthResponse;
import com.roulette.resto.business.social.dto.LoginDto;
import com.roulette.resto.business.social.dto.RegisterDto;
import com.roulette.resto.business.social.dto.UserInfoDto;
import com.roulette.resto.business.social.entity.Account;
import com.roulette.resto.business.social.services.AccountService;
import com.roulette.resto.business.social.services.UserService;
import com.roulette.resto.common.configuration.JwtService;
import com.roulette.resto.common.exception.APIError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountNotFoundException;
@Slf4j
@RestController
public class AuthController {
	private final AuthenticationManager authenticationManager;

	private final JwtService jwtService;
	private final AccountService accountService;
	private final UserService userService;


	public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, AccountService accountService, UserService userService) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.accountService = accountService;
		this.userService = userService;
	}
	@PostMapping("/login")
	@Operation(summary = "Log user", description = "Authenticate a user using his login and password combination, " +
			"then return a jwt token and his profile information")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Auth info",
					content = @Content(mediaType = "application/json",schema = @Schema(implementation =
							AuthResponse.class))),
			@ApiResponse(responseCode = "401", description = "Login failed",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation =APIError.class),examples = {
							@ExampleObject(
									name = "login failed",
									value = "{\"message\":\"Bad credentials\",\"description\":\"\"}"
							)
					})),
			@ApiResponse(responseCode = "404", description = "Account not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation =APIError.class),examples = {
							@ExampleObject(
									name = "Account not found",
									value = "{\"message\":\"Account not found\",\"description\":\"\"}")}))
	})
	public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletRequest request){
		try {
			UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(loginDto.getLogin(), loginDto.getPassword());
			Authentication auth = authenticationManager.authenticate(authReq);
			SecurityContext sc = SecurityContextHolder.getContext();
			sc.setAuthentication(auth);
			HttpSession session = request.getSession();
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
			Account account = accountService.getAccountByLogin(loginDto.getLogin());
			UserInfoDto userInfo = userService.getUserInfoById(account.getAccountId());
			log.warn(userInfo.toString());
			final AuthResponse authResponse = jwtService.buildAuthResponse(
					accountService.loadUserByUsername(loginDto.getLogin()),
					account.getAccountId(),
					userInfo.getBasicUserInfo());
			return new ResponseEntity<>(authResponse, HttpStatus.OK);

		}catch (AuthenticationException e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(new APIError("Bad credentials", e.getMessage()), HttpStatus.UNAUTHORIZED);
		} catch (AccountNotFoundException e) {
			return new ResponseEntity<>(new APIError("Account not found", e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}


	@PostMapping("/signup")
	@Operation(summary = "Register user", description = "Register a new user with auth and personal information"+
			"then return a jwt token and his profile information")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Auth info",
					content = @Content(mediaType = "application/json",schema = @Schema(implementation =
							AuthResponse.class))),
			@ApiResponse(responseCode = "400", description = "Login already exist",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation =APIError.class),examples = {
							@ExampleObject(
									name = "Login already exist",
									value = "{\"message\":\"Login already exist\",\"description\":\"\"}"
							),
							@ExampleObject(
									name = "Email already exist",
									value = "{\"message\":\"Email already exist\",\"description\":\"\"}"
							)
					})),
			@ApiResponse(responseCode = "500", description = "Server error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation =APIError.class),examples = {
							@ExampleObject(
									name = "server error",
									value = "{\"message\":\"database error while creating account\",\"description\":\"\"}")}))
	})
	public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto){
		if(accountService.existsByLogin(registerDto.getLogin())){
			return new ResponseEntity<>(new APIError("Login already exist"), HttpStatus.BAD_REQUEST);
		}
		if(accountService.existsByEmail(registerDto.getEmail())){
			return new ResponseEntity<>(new APIError("Email already exist"), HttpStatus.BAD_REQUEST);
		}
		try {
			Account newAccount = accountService.registerAccount(registerDto);
			final AuthResponse authResponse = jwtService.buildAuthResponse(
					accountService.loadUserByUsername(newAccount.getLogin()),
					newAccount.getAccountId(),
					newAccount.getUserInfo());
			return new ResponseEntity<>(authResponse, HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>((new APIError("database error while creating account", e.getMessage())),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}


	}
}
