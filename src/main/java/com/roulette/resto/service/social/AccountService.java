package com.roulette.resto.service.social;

import com.roulette.resto.data.social.dto.in.DeleteAccount;
import com.roulette.resto.data.social.dto.in.RegisterDto;
import com.roulette.resto.data.social.dto.in.VerifyEmailDto;
import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.entity.UserInfo;
import com.roulette.resto.data.social.entity.UserRole;
import com.roulette.resto.exception.APIError;
import com.roulette.resto.repository.social.AccountRepository;
import com.roulette.resto.service.common.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;


@Slf4j
@Service
public class AccountService implements UserDetailsService {
	final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;


	public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder,MailService mailService) {
		this.accountRepository = accountRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailService = mailService;
	}

	public Account getAccountByLogin(String login) throws AccountNotFoundException {
		return accountRepository.getAccountByLogin(login);
	}

	public Account getAccountById(int id) throws AccountNotFoundException {
		return accountRepository.getAccountById(id);
	}


	public boolean existsByLogin(String login) {
		try {
			accountRepository.getAccountByLogin(login);
			return true;
		} catch (AccountNotFoundException e) {
			log.error(e.getMessage());
			return false;
		}
	}

	public boolean existsByEmail(String email) {
		try {
			accountRepository.getAccountByEmail(email);
			return true;
		} catch (AccountNotFoundException e) {
			log.error(e.getMessage());
			return false;
		}
	}

	public boolean existsById(int id) {
		Account account = accountRepository.getAccountById(id);
		return account != null;
	}

	public Account registerAccount(RegisterDto registerDto) throws DuplicateKeyException {

		Account account = new Account();
		account.setLogin(registerDto.getLogin());
		if(registerDto.getPassword() != null)
			account.setPassword(passwordEncoder.encode(registerDto.getPassword()));
		account.setVerificationToken(generateVerificationToken(8));
		UserInfo userInfo = new UserInfo();
		userInfo.setEmail(registerDto.getEmail());
		userInfo.setFirstName(registerDto.getFirstName());
		userInfo.setLastName(registerDto.getLastName());
		userInfo.setRole(UserRole.ROLE_USER);
		account.setUserInfo(userInfo);
		try {
			int accountId = accountRepository.registerAccount(account);
			account.setAccountId(accountId);
			mailService.sendVerificationMail(account);
			return account;
		} catch (DuplicateKeyException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public static String generateVerificationToken(int length) {
		Random rand = new Random();
		StringBuilder res = new StringBuilder();
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		for (int i = 0; i < length; i++) {
			int randIndex = rand.nextInt(chars.length());
			res.append(chars.charAt(randIndex));
		}
		return res.toString();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			return accountRepository.getAccountByLogin(username);
		} catch (AccountNotFoundException e) {
			log.error(e.getMessage());
			throw new UsernameNotFoundException(e.getMessage(), e.getCause());
		}
	}

	public boolean verifyEmail(VerifyEmailDto verifyEmailDto) throws APIError {
		try {
			Account account = accountRepository.getAccountByEmail(verifyEmailDto.getEmail());
			if(!(Objects.equals(account.getVerificationToken(), verifyEmailDto.getVerificationCode()))) {
				throw new APIError(700, HttpStatus.BAD_REQUEST);
			} else {
				accountRepository.updateMailVerificationStatus(account.getAccountId(), true);
				accountRepository.updateVerificationToken(generateVerificationToken(8), account.getAccountId());
			}
		} catch (SQLException e) {
			throw new APIError(500, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (AccountNotFoundException e) {
			throw new APIError(64, HttpStatus.NOT_FOUND);
		}
		return true;
	}

	public void sendVerificationCode(String email) throws APIError {
		try {
			Account account = accountRepository.getAccountByEmail(email);
			String newToken = generateVerificationToken(8);
			accountRepository.updateVerificationToken(newToken, account.getAccountId());
			account.setVerificationToken(newToken);
			mailService.sendSecurityCode(account);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new APIError(500, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (AccountNotFoundException e) {
			log.error(e.getMessage());
			throw new APIError(64, HttpStatus.NOT_FOUND);
		}
	}

	public Account getAccountByEmail(String email) throws AccountNotFoundException {
		return accountRepository.getAccountByEmail(email);
	}

	public void deleteUser(DeleteAccount deleteAccount) throws APIError, AccountNotFoundException {
		accountRepository.deleteAccount(deleteAccount);
	}
	public void recoverUser(DeleteAccount deleteAccount) throws APIError, AccountNotFoundException {
		accountRepository.recoverAccount(deleteAccount);
	}
}
