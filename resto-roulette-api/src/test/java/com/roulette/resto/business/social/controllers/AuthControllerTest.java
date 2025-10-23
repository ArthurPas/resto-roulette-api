package com.roulette.resto.business.social.controllers;

import com.roulette.resto.common.exception.APIError;
import com.roulette.resto.social.controllers.AuthController;
import com.roulette.resto.social.dto.in.LoginDto;
import com.roulette.resto.social.dto.in.RegisterDto;
import com.roulette.resto.social.dto.in.VerifyEmailDto;
import com.roulette.resto.social.dto.out.AuthResponse;
import com.roulette.resto.social.dto.out.UserInfoDto;
import com.roulette.resto.social.entity.Account;
import com.roulette.resto.social.entity.UserInfo;
import com.roulette.resto.social.repository.AccountRepository;
import com.roulette.resto.social.services.AccountService;
import com.roulette.resto.social.services.AuthService;
import com.roulette.resto.social.services.UserService;
import com.roulette.resto.common.configuration.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// Pour tester un contrôleur, il est souvent plus simple d'utiliser l'approche @InjectMocks
// comme pour les services, afin de ne pas monter tout le contexte web.
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

	// On mock toutes les dépendances du contrôleur
	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private JwtService jwtService;
	@Mock
	private AccountService accountService;
	@Mock
	private UserService userService;
	@Mock
	private AccountRepository accountRepository;
	@Mock // On mock aussi l'objet Authentication qui est retourné par le manager
	private Authentication authentication;
	@Mock
	private AuthService authService;

	// On injecte les mocks dans le contrôleur
	@InjectMocks
	private AuthController authController;

	private Account sampleAccount;
	private UserInfo sampleUserInfo;
	private AuthResponse sampleAuthResponse;

	@BeforeEach
	void setUp() {
		// Préparation des objets de test réutilisables
		sampleUserInfo = new UserInfo();
		sampleUserInfo.setEmail("test@example.com");
		sampleUserInfo.setFirstName("John");
		sampleUserInfo.setLastName("Doe");

		sampleAccount = new Account();
		sampleAccount.setAccountId(1);
		sampleAccount.setLogin("testuser");
		sampleAccount.setUserInfo(sampleUserInfo);

		sampleAuthResponse = new AuthResponse();
		sampleAuthResponse.setUserInfo(sampleUserInfo);


		// Simuler le contexte de la requête HTTP pour les tests qui en ont besoin (comme /login)
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}

	@Nested
	@DisplayName("POST /login")
	class LoginTests {
		@Test
		@DisplayName("should return 200 OK with AuthResponse when credentials are valid")
		void login_shouldReturnOkAndAuthResponse_whenCredentialsAreValid() throws AccountNotFoundException, APIError {
			// Arrange
			LoginDto loginDto = new LoginDto("testuser", "password");
			UserInfoDto userInfoDto = new UserInfoDto();
			userInfoDto.setUserInfo(sampleUserInfo);

			when(jwtService.buildAuthResponse(any())).thenReturn(sampleAuthResponse);

			// Act
			ResponseEntity<?> response = authController.login(loginDto, new MockHttpServletRequest());

			// Assert
			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertNotNull(response.getBody());
			assertEquals(sampleAuthResponse, response.getBody());
		}

		@Test
		@DisplayName("should return 401 Unauthorized when credentials are bad")
		void login_shouldReturnUnauthorized_whenCredentialsAreInvalid() throws APIError {
			// Arrange
			LoginDto loginDto = new LoginDto("testuser", "wrongpassword");
			when(authService.authenticate(any(), any(), any())).thenThrow(new APIError("Bad credentials",
					HttpStatus.UNAUTHORIZED));

			// Act
			ResponseEntity<?> response = authController.login(loginDto, new MockHttpServletRequest());

			// Assert
			assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
			assertTrue(response.getBody().toString().contains("Bad credentials"));
		}

		@Test
		@DisplayName("should return 404 Not Found when account is not found after auth")
		void login_shouldReturnNotFound_whenAccountIsMissing() throws AccountNotFoundException, APIError {
			// Arrange
			LoginDto loginDto = new LoginDto("testuser", "password");
			when(authService.getAccountFromLoginRequest(loginDto)).thenThrow(new APIError("Account not found", HttpStatus.NOT_FOUND));
			// Act
			ResponseEntity<?> response = authController.login(loginDto, new MockHttpServletRequest());

			// Assert
			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
			assertTrue(response.getBody().toString().contains("Account not found"));
		}
	}

	@Nested
	@DisplayName("POST /signup")
	class SignUpTests {
		@Test
		@DisplayName("should return 201 Created with AuthResponse for successful registration")
		void registerUser_shouldReturnCreatedAndAuthResponse_whenDataIsValid() throws AccountNotFoundException, APIError {
			// Arrange
			RegisterDto registerDto = new RegisterDto("newuser", "password", "new@example.com", "Jane", "Doe");
			when(accountService.registerAccount(registerDto)).thenReturn(sampleAccount);
			when(jwtService.buildAuthResponse(any())).thenReturn(sampleAuthResponse);

			// Act
			ResponseEntity<?> response = authController.registerUser(registerDto);

			// Assert
			assertEquals(HttpStatus.CREATED, response.getStatusCode());
			assertEquals(sampleAuthResponse, response.getBody());
		}

		@Test
		@DisplayName("should return 400 Bad Request when login already exists")
		void registerUser_shouldReturnBadRequest_whenLoginExists() throws APIError {
			// Arrange
			RegisterDto registerDto = new RegisterDto("existinguser", "password", "new@example.com", "Jane", "Doe");
			when(authService.checkIfExists(registerDto)).thenThrow(new APIError("Login already exist",
					HttpStatus.BAD_REQUEST));

			// Act
			ResponseEntity<?> response = authController.registerUser(registerDto);

			// Assert
			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
			assertTrue(response.getBody().toString().contains("Login already exist"));
			verify(accountService, never()).registerAccount(any()); // On vérifie que la création n'a pas été tentée
		}

		@Test
		@DisplayName("should return 400 Bad Request when email already exists")
		void registerUser_shouldReturnBadRequest_whenEmailExists() throws APIError {
			// Arrange
			RegisterDto registerDto = new RegisterDto("newuser", "password", "existing@example.com", "Jane", "Doe");
			when(authService.checkIfExists(any())).thenThrow(new APIError("Email already exist",
					HttpStatus.BAD_REQUEST));

			// Act
			ResponseEntity<?> response = authController.registerUser(registerDto);

			// Assert
			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
			assertTrue(response.getBody().toString().contains("Email already exist"));
		}

		@Test
		@DisplayName("should return 400 Bad Request on DuplicateKeyException")
		void registerUser_shouldReturnBadRequest_onDuplicateKeyException() throws APIError {
			// Arrange
			RegisterDto registerDto = new RegisterDto("newuser", "password", "new@example.com", "Jane", "Doe");
			when(accountService.registerAccount(registerDto)).thenThrow(new DuplicateKeyException("Duplicate value"));
			// Act
			ResponseEntity<?> response = authController.registerUser(registerDto);

			// Assert
			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
			assertTrue(response.getBody().toString().contains("Duplicate value"));
		}
	}

	@Nested
	@DisplayName("POST /verifyEmailAddress")
	class VerifyEmailTests {

		@Test
		@DisplayName("should return 204 No Content when verification is successful")
		void verifyMail_shouldReturnNoContent_whenVerificationSucceeds() throws AccountNotFoundException, SQLException, APIError {
			// Arrange
			VerifyEmailDto verifyDto = new VerifyEmailDto("test@example.com", "code");
			// Act
			ResponseEntity<?> response = authController.verifyMail(verifyDto);

			// Assert
			assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		}

		@Test
		@DisplayName("should return 400 Bad Request when token does not match")
		void verifyMail_shouldReturnBadRequest_whenTokenIsIncorrect() throws AccountNotFoundException, SQLException {
			// Arrange
			VerifyEmailDto verifyDto = new VerifyEmailDto("test@example.com", "wrong-code");
			when(authController.verifyMail(verifyDto)).thenThrow(new APIError("Tokens didnt match",HttpStatus.BAD_REQUEST));

			// Act
			ResponseEntity<?> response = authController.verifyMail(verifyDto);

			// Assert
			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
			assertTrue(response.getBody().toString().contains("Tokens didnt match"));
		}

		@Test
		@DisplayName("should return 404 Not Found when account is not found")
		void verifyMail_shouldReturnNotFound_whenAccountNotFound() throws AccountNotFoundException, SQLException, APIError {
			// Arrange
			VerifyEmailDto verifyDto = new VerifyEmailDto("notfound@example.com", "code");
			when(accountService.verifyEmail(verifyDto)).thenThrow(new APIError("Account not found", HttpStatus.NOT_FOUND));

			// Act
			ResponseEntity<?> response = authController.verifyMail(verifyDto);

			// Assert
			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
			assertTrue(response.getBody().toString().contains("Account not found"));
		}
	}
}