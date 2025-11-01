package com.roulette.resto.social.controllers;

import com.roulette.resto.common.configuration.JwtService;
import com.roulette.resto.common.exception.APIError;
import com.roulette.resto.common.exception.ErrorResponse;
import com.roulette.resto.social.dto.in.*;
import com.roulette.resto.social.dto.out.AuthResponse;
import com.roulette.resto.social.entity.Account;
import com.roulette.resto.social.services.AccountService;
import com.roulette.resto.social.services.AuthService;
import com.roulette.resto.social.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {


	private final JwtService jwtService;
	private final AccountService accountService;
	private final UserService userService;
	private final AuthService authService;


	public AuthController(JwtService jwtService, AccountService accountService, UserService userService, AuthService authService) {
		this.jwtService = jwtService;
		this.accountService = accountService;
		this.userService = userService;
		this.authService = authService;
	}

	@PostMapping("/login")
	@Operation(summary = "Log user", description = "Authenticate a user using his login and password combination, " +
			"then return a jwt token and his profile information")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Auth info",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation =
							AuthResponse.class))),
			@ApiResponse(responseCode = "401", description = "Login failed",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = APIError.class), examples = {
							@ExampleObject(
									name = "login failed",
									value = "{\"message\":\"Bad credentials\",\"description\":\"\"}"
							)
					})),
			@ApiResponse(responseCode = "400", description = "Bad request",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = APIError.class), examples = {
							@ExampleObject(
									name = "Bad request",
									value = "{\"message\":\"Login request must have at least login or email\",\"description\":\"\"}"
							)
					})),
			@ApiResponse(responseCode = "404", description = "Account not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = APIError.class), examples = {
							@ExampleObject(
									name = "Account not found",
									value = "{\"message\":\"Account not found\",\"description\":\"\"}")}))
	})
	public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
		try {
			Account account = authService.getAccountFromLoginRequest(loginDto);
			authService.authenticate(loginDto, request, account);
			return new ResponseEntity<>(jwtService.buildAuthResponse(account), HttpStatus.OK);
		} catch (APIError e) {
			return new ResponseEntity<>(e, e.getStatus());
		}
	}


	@PostMapping("/signup")
	@Operation(summary = "Register user", description = "Register a new user with auth and personal information" +
			"then return a jwt token and his profile information")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201",
					description = "Auth info",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation =
							AuthResponse.class))),
			@ApiResponse(responseCode = "400", description = "Login already exist",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = APIError.class), examples = {
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
							schema = @Schema(implementation = APIError.class), examples = {
							@ExampleObject(
									name = "server error",
									value = "{\"message\":\"Server error while creating account\",\"description\":\"\"}")}))
	})
	public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto) {

		try {
			authService.checkIfExists(registerDto);
			Account newAccount = accountService.registerAccount(registerDto);
			return new ResponseEntity<>(jwtService.buildAuthResponse(newAccount), HttpStatus.CREATED);
		} catch (DuplicateKeyException e) {
			return new ResponseEntity<>((new APIError("Duplicate value that should be unique")),
					HttpStatus.BAD_REQUEST);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, e.getStatus());
		} catch (Exception e) {
			ErrorResponse errorResponse = new ErrorResponse(new APIError("Server error while creating account",
					HttpStatus.INTERNAL_SERVER_ERROR));
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@PostMapping("/verifyEmailAddress")
	@Operation(summary = "Update the verified mail status of the account", description = "After a registration, the " +
			"app send a code by email. Call this endpoint with the code received by mail to update the account status" +
			" about the email verification. In case of a email address change, just call again the endpoint with the " +
			"code received in the other email address")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "User verified status updated", content = @Content(schema =
			@Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "404", description = "Account not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = APIError.class), examples = {
							@ExampleObject(
									name = "Account not found",
									value = "{\"message\":\"Account not found\",\"description\":\"\"}")})),
			@ApiResponse(responseCode = "400", description = "Wrong token",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = APIError.class), examples = {
							@ExampleObject(
									name = "Tokens didnt match",
									value = "{\"message\":\"Tokens didnt match\",\"description\":\"\"}")})),
			@ApiResponse(responseCode = "500", description = "Server error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = APIError.class), examples = {
							@ExampleObject(
									name = "server error",
									value = "{\"message\":\"Unexpected error\",\"description\":\"\"}")}))
	})
	public ResponseEntity<?> verifyMail(@RequestBody VerifyEmailDto verifyEmailDto) {
		try {
			accountService.verifyEmail(verifyEmailDto);
			return new ResponseEntity<>("{\"Message\": \"email verification succeed\"}", HttpStatus.NO_CONTENT);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, e.getStatus());
		}
	}

	@PostMapping("/password/reset")
	@Operation(summary = "Reset password", description = "This route need to be called after calling the verification" +
			" by email. You must provide the email associated with the account, the new password AND the last code " +
			"received by mail to" +
			" be " +
			"able to reset the password ")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Reset password succeed", content = @Content(schema =
			@Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "404", description = "Account not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = APIError.class), examples = {
							@ExampleObject(
									name = "Account not found",
									value = "{\"message\":\"Account not found\",\"description\":\"\"}")}))})
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
		try {
			userService.resetPassword(resetPasswordDto);
			accountService.verifyEmail(new VerifyEmailDto(resetPasswordDto.getEmail(), resetPasswordDto.getVerificationToken()));
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, e.getStatus());
		}
	}

	@PostMapping("/sendVerificationCode")
	@Operation(summary = "Send verification code", description = "Send a code by email to the address associated to " +
			"the account. This code can be used to perfom action that require a verification such as reset the " +
			"account password ")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Email sent", content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "404", description = "Account not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = APIError.class), examples = {
							@ExampleObject(
									name = "Account not found",
									value = "{\"message\":\"Account not found\",\"description\":\"\"}")}))})
	public ResponseEntity<?> sendMail(@RequestBody SendEmailDto emailDto) {
		try {
			accountService.sendVerificationCode(emailDto.getEmail());
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (APIError e) {
			ErrorResponse errorResponse = new ErrorResponse(e);
			return new ResponseEntity<>(errorResponse, e.getStatus());
		}
	}
}
