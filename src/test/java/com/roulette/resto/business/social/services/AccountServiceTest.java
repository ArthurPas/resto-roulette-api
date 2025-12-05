package com.roulette.resto.business.social.services;

import com.roulette.resto.exception.APIError;
import com.roulette.resto.data.social.dto.in.RegisterDto;
import com.roulette.resto.data.social.dto.in.VerifyEmailDto;
import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.entity.UserInfo;
import com.roulette.resto.repository.social.AccountRepository;
import com.roulette.resto.service.common.MailService;
import com.roulette.resto.service.social.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService Tests")
class AccountServiceTest {

	// On mock les dépendances. Mockito va créer des objets factices pour nous.
	@Mock
	private AccountRepository accountRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private MailService mailService;

	// @InjectMocks crée une instance de AccountService et y injecte les mocks déclarés ci-dessus.
	@InjectMocks
	private AccountService accountService;

	private Account sampleAccount;

	@BeforeEach
	void setUp() {
		// Initialisation d'un objet Account de test réutilisable
		UserInfo sampleUserInfo = new UserInfo();
		sampleUserInfo.setEmail("test@example.com");
		sampleUserInfo.setFirstName("John");
		sampleUserInfo.setLastName("Doe");

		sampleAccount = new Account();
		sampleAccount.setAccountId(1);
		sampleAccount.setLogin("testuser");
		sampleAccount.setPassword("encodedPassword");
		sampleAccount.setVerificationToken("12345678");
		sampleAccount.setUserInfo(sampleUserInfo);
	}

	@Nested
	@DisplayName("Tests for registerAccount")
	class RegisterAccountTests {

		@Test
		@DisplayName("should register account successfully and send verification email")
		void registerAccount_shouldSucceed() {
			// Arrange
			RegisterDto registerDto = new RegisterDto("testuser", "password123", "test@example.com", "John", "Doe");

			// Définir le comportement des mocks
			when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
			when(accountRepository.registerAccount(any(Account.class))).thenReturn(1); // Retourne l'ID du nouvel utilisateur
			doNothing().when(mailService).sendVerificationMail(any(Account.class));

			// Act
			Account result = accountService.registerAccount(registerDto);

			// Assert
			assertNotNull(result);
			assertEquals(1, result.getAccountId());
			assertEquals("testuser", result.getLogin());
			assertEquals("encodedPassword", result.getPassword());
			assertEquals("test@example.com", result.getUserInfo().getEmail());
			assertNotNull(result.getVerificationToken());

			// Vérifier que les méthodes des mocks ont été appelées comme prévu
			verify(passwordEncoder, times(1)).encode("password123");
			verify(accountRepository, times(1)).registerAccount(any(Account.class));
			verify(mailService, times(1)).sendVerificationMail(any(Account.class));
		}

		@Test
		@DisplayName("should throw DuplicateKeyException when account already exists")
		void registerAccount_shouldThrowDuplicateKeyException() {
			// Arrange
			RegisterDto registerDto = new RegisterDto("existinguser", "password123", "existing@example.com", "Jane", "Doe");
			when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
			when(accountRepository.registerAccount(any(Account.class))).thenThrow(new DuplicateKeyException("User already exists"));

			// Act & Assert
			assertThrows(DuplicateKeyException.class, () -> accountService.registerAccount(registerDto));

			// Vérifier que le service de mail n'a PAS été appelé en cas d'erreur
			verify(mailService, never()).sendVerificationMail(any(Account.class));
		}
	}

	@Nested
	@DisplayName("Tests for verifyEmail")
	class VerifyEmailTests {

		@Test
		@DisplayName("should return true when verification code is correct")
		void verifyEmail_shouldReturnTrue_whenCodeIsCorrect() throws AccountNotFoundException, SQLException, APIError {
			// Arrange
			VerifyEmailDto verifyDto = new VerifyEmailDto("test@example.com", "12345678");
			when(accountRepository.getAccountByEmail("test@example.com")).thenReturn(sampleAccount);
			doNothing().when(accountRepository).updateMailVerificationStatus(1, true);

			// Act
			accountService.verifyEmail(verifyDto);

			// Assert
			verify(accountRepository, times(1)).updateMailVerificationStatus(1, true);
		}

		@Test
		@DisplayName("should return false when verification code is incorrect")
		void verifyEmail_shouldReturnFalse_whenCodeIsIncorrect() throws AccountNotFoundException, SQLException, APIError {
			// Arrange
			VerifyEmailDto verifyDto = new VerifyEmailDto("test@example.com", "WRONG_CODE");
			when(accountRepository.getAccountByEmail("test@example.com")).thenReturn(sampleAccount);

			// Act
			try {
				accountService.verifyEmail(verifyDto);
			}catch (APIError error) {
				verify(accountRepository, never()).updateMailVerificationStatus(anyInt(), anyBoolean());
			}

		}

		@Test
		@DisplayName("should throw UsernameNotFoundException when user is not found")
		void loadUserByUsername_shouldThrowUsernameNotFoundException() throws AccountNotFoundException {
			// Arrange
			String username = "notfounduser";
			when(accountRepository.getAccountByLogin(username)).thenThrow(new AccountNotFoundException("User not found"));

			// Act & Assert
			assertThrows(UsernameNotFoundException.class, () -> accountService.loadUserByUsername(username));
		}
	}
}