package com.roulette.resto.business.social.services;

import com.roulette.resto.exception.APIError;
import com.roulette.resto.data.social.dto.in.ChangePasswordDto;
import com.roulette.resto.data.social.dto.in.ResetPasswordDto;
import com.roulette.resto.data.social.dto.in.UpdateUserInfo;
import com.roulette.resto.data.social.dto.out.UserInfoDto;
import com.roulette.resto.data.social.dto.out.SocialInteraction;
import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.entity.UserInfo;
import com.roulette.resto.repository.social.AccountRepository;
import com.roulette.resto.repository.social.InteractionRepository;
import com.roulette.resto.service.social.AccountService;
import com.roulette.resto.service.social.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

	// On mock toutes les dépendances de UserService
	@Mock
	private AccountRepository accountRepository;
	@Mock
	private InteractionRepository interactionRepository;
	@Mock
	private AccountService accountService;
	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private PasswordEncoder passwordEncoder;

	// On injecte les mocks ci-dessus dans notre instance de UserService
	@InjectMocks
	private UserService userService;

	private Account sampleAccount;
	private UserInfo sampleUserInfo;

	@BeforeEach
	void setUp() {
		sampleUserInfo = new UserInfo();
		sampleUserInfo.setEmail("test@example.com");
		sampleUserInfo.setFirstName("John");
		sampleUserInfo.setLastName("Doe");

		sampleAccount = new Account();
		sampleAccount.setAccountId(1);
		sampleAccount.setLogin("testuser");
		sampleAccount.setUserInfo(sampleUserInfo);
		sampleAccount.setVerificationToken("VALID_TOKEN");
	}

	@Nested
	@DisplayName("Tests for getUserInfoById")
	class GetUserInfoByIdTests {
		@Test
		@DisplayName("should return user info dto when user exists")
		void getUserInfoById_shouldReturnDto_whenUserExists() throws AccountNotFoundException {
			// Arrange
			int userId = 1;
			List<SocialInteraction> interactions = Collections.singletonList(new SocialInteraction());
			when(accountService.existsById(userId)).thenReturn(true);
			when(accountRepository.getAccountById(userId)).thenReturn(sampleAccount);
			when(interactionRepository.getInteractionsByAccountId(userId)).thenReturn(interactions);

			// Act
			UserInfoDto result = userService.getUserInfoById(userId);

			// Assert
			assertNotNull(result);
			assertEquals("testuser", result.getLogin());
			assertEquals("John", result.getUserInfo().getFirstName());
			assertEquals(1, result.getSocialInteractions().size());
			verify(accountService).existsById(userId);
			verify(accountRepository).getAccountById(userId);
			verify(interactionRepository).getInteractionsByAccountId(userId);
		}

		@Test
		@DisplayName("should throw AccountNotFoundException when user does not exist")
		void getUserInfoById_shouldThrowException_whenUserDoesNotExist() {
			// Arrange
			int userId = 99;
			when(accountService.existsById(userId)).thenReturn(false);

			// Act & Assert
			assertThrows(AccountNotFoundException.class, () -> userService.getUserInfoById(userId));

			// On vérifie que les repositories n'ont pas été appelés inutilement
			verify(accountRepository, never()).getAccountById(anyInt());
			verify(interactionRepository, never()).getInteractionsByAccountId(anyInt());
		}
	}

	@Nested
	@DisplayName("Tests for updateUserInfo")
	class UpdateUserInfoTests {

		@Test
		@DisplayName("should update user info and return it")
		void updateUserInfo_shouldSucceed() throws SQLException, AccountNotFoundException {
			// Arrange
			int userId = 1;
			UpdateUserInfo newUserInfo = new UpdateUserInfo("newfirst", "newlast", "test@example.com");
			UserInfo updatedUserInfo = new UserInfo();
			updatedUserInfo.setFirstName("newfirst");
			updatedUserInfo.setEmail("test@example.com");

			when(accountRepository.getUserInfoById(userId)).thenReturn(sampleUserInfo).thenReturn(updatedUserInfo);
			when(accountRepository.updateUserInfo(String.valueOf(userId), newUserInfo)).thenReturn(1);
			// Act
			UserInfo result = userService.updateUserPersonalInfo(userId, newUserInfo);

			// Assert
			assertNotNull(result);
			assertEquals("newfirst", result.getFirstName());
			verify(accountRepository, times(1)).updateUserInfo(anyString(), any(UpdateUserInfo.class));
		}

		@Test
		@DisplayName("should reset verification status when email changes")
		void updateUserInfo_shouldResetVerification_whenEmailChanges() throws SQLException, AccountNotFoundException {
			// Arrange
			int userId = 1;
			UpdateUserInfo newUserInfo = new UpdateUserInfo("John", "Doe", "new.email@example.com");
			when(accountRepository.getUserInfoById(userId)).thenReturn(sampleUserInfo);
			when(accountRepository.updateUserInfo(String.valueOf(userId), newUserInfo)).thenReturn(1);

			// Act
			userService.updateUserPersonalInfo(userId, newUserInfo);

			// Assert
			verify(accountRepository, times(1)).updateMailVerificationStatus(userId, false);
		}

		@Test
		@DisplayName("should throw SQLException when no rows are updated")
		void updateUserInfo_shouldThrowSqlException_whenNoRowsUpdated() throws AccountNotFoundException, SQLException {
			// Arrange
			int userId = 1;
			UpdateUserInfo newUserInfo = new UpdateUserInfo("newfirst", "newlast", "test@example.com");
			when(accountRepository.getUserInfoById(userId)).thenReturn(sampleUserInfo);
			when(accountRepository.updateUserInfo(String.valueOf(userId), newUserInfo)).thenReturn(0); // 0 ligne mise à jour

			// Act & Assert
			assertThrows(SQLException.class, () -> userService.updateUserPersonalInfo(userId, newUserInfo));
		}
	}

	@Nested
	@DisplayName("Tests for updatePassword")
	class UpdatePasswordTests {

		@Test
		@DisplayName("should update password when old password is correct")
		void updatePassword_shouldSucceed_whenCredentialsAreValid() throws AccountNotFoundException {
			// Arrange
			int accountId = 1;
			ChangePasswordDto passwordDto = new ChangePasswordDto("oldPassword", "newPassword");

			when(accountRepository.getAccountById(accountId)).thenReturn(sampleAccount);
			// Pas d'exception levée par authenticate() signifie que l'authentification a réussi
			doReturn(null).when(authenticationManager).authenticate(any());
			when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

			// Act
			Account result = userService.updatePassword(passwordDto, accountId);

			// Assert
			assertNotNull(result);
			assertEquals(accountId, result.getAccountId());
			verify(authenticationManager).authenticate(any());
			verify(passwordEncoder).encode("newPassword");
			verify(accountRepository).changePassword(accountId, "encodedNewPassword");
		}

		@Test
		@DisplayName("should throw AuthenticationException when old password is wrong")
		void updatePassword_shouldThrowAuthenticationException_whenCredentialsAreInvalid() throws AccountNotFoundException {
			// Arrange
			int accountId = 1;
			ChangePasswordDto passwordDto = new ChangePasswordDto("wrongOldPassword", "newPassword");
			when(accountRepository.getAccountById(accountId)).thenReturn(sampleAccount);
			// On simule une erreur d'authentification
			doThrow(new BadCredentialsException("Bad credentials")).when(authenticationManager).authenticate(any());

			// Act & Assert
			assertThrows(AuthenticationException.class, () -> userService.updatePassword(passwordDto, accountId));

			// On vérifie que le mot de passe n'a pas été changé
			verify(passwordEncoder, never()).encode(anyString());
			verify(accountRepository, never()).changePassword(anyInt(), anyString());
		}
	}

	@Nested
	@DisplayName("Tests for resetPassword")
	class ResetPasswordTests {
		@Test
		@DisplayName("should reset password when verification token is correct")
		void resetPassword_shouldSucceed_whenTokenIsValid() throws AccountNotFoundException, APIError {
			// Arrange
			ResetPasswordDto resetDto = new ResetPasswordDto("test@example.com", "VALID_TOKEN", "newPassword123");
			when(accountRepository.getAccountByEmail("test@example.com")).thenReturn(sampleAccount);
			when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");

			// Act
			userService.resetPassword(resetDto);

			// Assert
			verify(accountRepository).getAccountByEmail("test@example.com");
			verify(passwordEncoder).encode("newPassword123");
			verify(accountRepository).changePassword(sampleAccount.getAccountId(), "encodedNewPassword");
		}

		@Test
		@DisplayName("should throw SecurityException when verification token is incorrect")
		void resetPassword_shouldThrowSecurityException_whenTokenIsInvalid() throws AccountNotFoundException {
			// Arrange
			ResetPasswordDto resetDto = new ResetPasswordDto("test@example.com", "INVALID_TOKEN", "newPassword123");
			when(accountRepository.getAccountByEmail("test@example.com")).thenReturn(sampleAccount);

			// Act & Assert
			assertThrows(SecurityException.class, () -> userService.resetPassword(resetDto));

			// On vérifie que le mot de passe n'a pas été changé
			verify(passwordEncoder, never()).encode(anyString());
			verify(accountRepository, never()).changePassword(anyInt(), anyString());
		}

		@Test
		@DisplayName("should throw AccountNotFoundException when email does not exist")
		void resetPassword_shouldThrowAccountNotFoundException_whenEmailNotFound() throws AccountNotFoundException {
			// Arrange
			ResetPasswordDto resetDto = new ResetPasswordDto("notfound@example.com", "TOKEN", "newPassword123");
			// On simule une erreur d'accès à la base de données quand on cherche l'email
			when(accountRepository.getAccountByEmail(anyString())).thenThrow(new DataAccessException("..."){});

			// Act & Assert
			assertThrows(APIError.class, () -> userService.resetPassword(resetDto));
		}
	}
}