package com.roulette.resto.business.social.controllers;

import com.roulette.resto.social.controllers.AccountController;
import com.roulette.resto.social.dto.in.ChangePasswordDto;
import com.roulette.resto.social.dto.in.UpdateUserInfo;
import com.roulette.resto.social.dto.out.AuthResponse;
import com.roulette.resto.social.dto.out.BasicAuthDto;
import com.roulette.resto.social.dto.out.UserInfoDto;
import com.roulette.resto.social.entity.Account;
import com.roulette.resto.social.entity.UserInfo;
import com.roulette.resto.social.services.AccountService;
import com.roulette.resto.social.services.UserService;
import com.roulette.resto.common.configuration.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountController Tests")
class AccountControllerTest {

	// On mock toutes les dépendances du contrôleur
	@Mock
	private UserService userService;
	@Mock
	private JwtService jwtService;
	@Mock
	private AccountService accountService;
	@Mock // On mock aussi l'objet Authentication qui est passé en paramètre
	private Authentication authentication;

	// On injecte les mocks dans le contrôleur
	@InjectMocks
	private AccountController accountController;

	private final int MOCK_ACCOUNT_ID = 1;

	@BeforeEach
	void setUp() {
		// Pré-configurer le comportement commun à presque tous les tests :
		// extraire l'ID du compte à partir du token d'authentification.
		when(jwtService.getAccountIdAuthenticated(authentication)).thenReturn(MOCK_ACCOUNT_ID);
	}

	@Nested
	@DisplayName("GET /users/info")
	class UsersInfoTests {

		@Test
		@DisplayName("should return 200 OK with UserInfoDto when user is found")
		void usersInfo_shouldReturnOkWithUserInfoDto_whenUserFound() throws AccountNotFoundException {
			// Arrange
			UserInfoDto expectedDto = new UserInfoDto();
			when(userService.getUserInfoById(MOCK_ACCOUNT_ID)).thenReturn(expectedDto);

			// Act
			ResponseEntity<?> response = accountController.usersInfo(authentication);

			// Assert
			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals(expectedDto, response.getBody());
			verify(userService).getUserInfoById(MOCK_ACCOUNT_ID);
		}

		@Test
		@DisplayName("should return 404 Not Found when AccountNotFoundException is thrown")
		void usersInfo_shouldReturnNotFound_whenAccountNotFoundExceptionIsThrown() throws AccountNotFoundException {
			// Arrange
			when(userService.getUserInfoById(MOCK_ACCOUNT_ID)).thenThrow(new AccountNotFoundException());

			// Act
			ResponseEntity<?> response = accountController.usersInfo(authentication);

			// Assert
			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}

		@Test
		@DisplayName("should return 500 Internal Server Error on generic exception")
		void usersInfo_shouldReturnInternalServerError_onGenericException() throws AccountNotFoundException {
			// Arrange
			when(userService.getUserInfoById(MOCK_ACCOUNT_ID)).thenThrow(new RuntimeException("Unexpected error"));

			// Act
			ResponseEntity<?> response = accountController.usersInfo(authentication);

			// Assert
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
			assertTrue(response.getBody().toString().contains("failed to retrieve user info"));
		}
	}

	@Nested
	@DisplayName("PUT /users/info")
	class UpdateUserInfoTests {

		@Test
		@DisplayName("should return 200 OK with updated UserInfo on success")
		void updateUserInfo_shouldReturnOkWithUpdatedUserInfo_whenUpdateSucceeds() throws AccountNotFoundException, SQLException {
			// Arrange
			UpdateUserInfo requestDto = new UpdateUserInfo("Jane", "Doe", "jane.doe@example.com");
			UserInfo expectedUserInfo = new UserInfo(); // L'objet retourné par le service
			expectedUserInfo.setFirstName("Jane");

			when(userService.updateUserInfo(MOCK_ACCOUNT_ID, requestDto)).thenReturn(expectedUserInfo);

			// Act
			ResponseEntity<?> response = accountController.updateUserInfo(requestDto, authentication);

			// Assert
			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals(expectedUserInfo, response.getBody());
			verify(userService).updateUserInfo(MOCK_ACCOUNT_ID, requestDto);
		}

		@Test
		@DisplayName("should return 404 Not Found when account does not exist")
		void updateUserInfo_shouldReturnNotFound_whenAccountNotFoundExceptionIsThrown() throws AccountNotFoundException, SQLException {
			// Arrange
			UpdateUserInfo requestDto = new UpdateUserInfo();
			when(userService.updateUserInfo(anyInt(), any(UpdateUserInfo.class))).thenThrow(new AccountNotFoundException());

			// Act
			ResponseEntity<?> response = accountController.updateUserInfo(requestDto, authentication);

			// Assert
			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}

		@Test
		@DisplayName("should return 500 Internal Server Error on SQLException")
		void updateUserInfo_shouldReturnInternalServerError_onSqlException() throws AccountNotFoundException, SQLException {
			// Arrange
			UpdateUserInfo requestDto = new UpdateUserInfo();
			when(userService.updateUserInfo(anyInt(), any(UpdateUserInfo.class))).thenThrow(new SQLException("DB error"));

			// Act
			ResponseEntity<?> response = accountController.updateUserInfo(requestDto, authentication);

			// Assert
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
			assertTrue(response.getBody().toString().contains("Database error : failed to update"));
		}
	}

	@Nested
	@DisplayName("PATCH /users/password/new")
	class UpdatePasswordTests {

		@Test
		@DisplayName("should return 200 OK with new BasicAuthDto on success")
		void updatePassword_shouldReturnOkWithNewAuth_whenUpdateSucceeds() throws Exception {
			// Arrange
			ChangePasswordDto passwordDto = new ChangePasswordDto("oldPass", "newPass");
			Account updatedAccount = new Account();
			updatedAccount.setAccountId(MOCK_ACCOUNT_ID);
			updatedAccount.setLogin("testuser");

			AuthResponse expectedAuthDto = new AuthResponse(new UserInfo());

			when(userService.updatePassword(passwordDto, MOCK_ACCOUNT_ID)).thenReturn(updatedAccount);
			when(jwtService.buildAuthResponse(updatedAccount)).thenReturn(expectedAuthDto);
			when(jwtService.getAccountIdAuthenticated(authentication)).thenReturn(MOCK_ACCOUNT_ID);
			when(userService.updatePassword(passwordDto, updatedAccount.getAccountId())).thenReturn(updatedAccount);
			// Act
			ResponseEntity<?> response = accountController.updatePassword(passwordDto, authentication);

			// Assert
			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals(expectedAuthDto, response.getBody());
			verify(userService).updatePassword(passwordDto, MOCK_ACCOUNT_ID);
			verify(jwtService).buildAuthResponse(updatedAccount);
		}

		@Test
		@DisplayName("should return 404 Not Found when account does not exist")
		void updatePassword_shouldReturnNotFound_whenAccountNotFoundExceptionIsThrown() throws Exception {
			// Arrange
			ChangePasswordDto passwordDto = new ChangePasswordDto();
			when(userService.updatePassword(any(ChangePasswordDto.class), anyInt())).thenThrow(new AccountNotFoundException());

			// Act
			ResponseEntity<?> response = accountController.updatePassword(passwordDto, authentication);

			// Assert
			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}

		@Test
		@DisplayName("should return 500 Internal Server Error on generic exception")
		void updatePassword_shouldReturnInternalServerError_onGenericException() throws Exception {
			// Arrange
			ChangePasswordDto passwordDto = new ChangePasswordDto();
			when(userService.updatePassword(any(ChangePasswordDto.class), anyInt())).thenThrow(new RuntimeException("Unexpected error"));

			// Act
			ResponseEntity<?> response = accountController.updatePassword(passwordDto, authentication);

			// Assert
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
			assertTrue(response.getBody().toString().contains("Database error : failed to update"));
		}
	}
}