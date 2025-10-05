package com.roulette.resto.business.social.controllers;

import com.roulette.resto.business.social.dto.in.VerifyEmailDto;
import com.roulette.resto.business.social.dto.out.AuthResponse;
import com.roulette.resto.business.social.dto.in.LoginDto;
import com.roulette.resto.business.social.dto.in.RegisterDto;
import com.roulette.resto.business.social.dto.out.UserInfoDto;
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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;

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
					userInfo.getPersonnelInformation());
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
			@ApiResponse(responseCode = "201",
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
									value = "{\"message\":\"Server error while creating account\",\"description\":\"\"}")}))
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

			return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
		}catch (DuplicateKeyException e) {
			return new ResponseEntity<>((new APIError("Duplicate value that should be unique", e.getMessage())),
					HttpStatus.BAD_REQUEST);
		}
		catch (AccountNotFoundException e) {
			return new ResponseEntity<>(new APIError("Account not found", e.getMessage()), HttpStatus.NOT_FOUND);
		}
		catch (Exception e){
			return new ResponseEntity<>(new APIError("Server error while creating account", e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/verify")
	@Operation(summary = "Update the verified mail status of the account", description = "After a registration, the " +
			"app send a code by email. Call this endpoint with the code received by mail to update the account status" +
			" about the email verification. In case of a email address change, just call again the endpoint with the " +
			"code received in the other email address")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204",
					description = "Success (no detail needed)"),
			@ApiResponse(responseCode = "404", description = "Account not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation =APIError.class),examples = {
							@ExampleObject(
									name = "Account not found",
									value = "{\"message\":\"Account not found\",\"description\":\"\"}")})),
			@ApiResponse(responseCode = "400", description = "Wrong token",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation =APIError.class),examples = {
							@ExampleObject(
									name = "Tokens didnt match",
									value = "{\"message\":\"Tokens didnt match\",\"description\":\"\"}")})),
			@ApiResponse(responseCode = "500", description = "Server error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation =APIError.class),examples = {
							@ExampleObject(
									name = "server error",
									value = "{\"message\":\"Unexpected error\",\"description\":\"\"}")}))
	})
	public ResponseEntity<?> verifyMail(@RequestBody VerifyEmailDto verifyEmailDto){
		try {
			boolean success = accountService.verifyEmail(verifyEmailDto);
			if(!success){
				return new ResponseEntity<>((new APIError("Tokens didnt match")), HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>("Message: email verification succeed", HttpStatus.NO_CONTENT);
		} catch (AccountNotFoundException e) {
			return new ResponseEntity<>(new APIError("Account not found", e.getMessage()), HttpStatus.NOT_FOUND);
		}catch (SQLException e){
			return new ResponseEntity<>(new APIError("Unexpected error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
