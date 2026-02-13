package com.roulette.resto.service.social;

import com.roulette.resto.dao.social.AccountDao;
import com.roulette.resto.data.social.dto.in.DeleteAccount;
import com.roulette.resto.data.social.dto.in.RegisterDto;
import com.roulette.resto.data.social.dto.in.VerifyEmailDto;
import com.roulette.resto.data.social.dto.in.VerifyTokenDto;
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
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;


@Slf4j
@Service
public class AccountService implements UserDetailsService {
	final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;
	private final AccountDao accountDao;


	public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder, MailService mailService, AccountDao accountDao) {
		this.accountRepository = accountRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailService = mailService;
		this.accountDao = accountDao;
	}

	public Account getAccountByLogin(String login) throws AccountNotFoundException {
		return accountRepository.getAccountByLogin(login);
	}

	public Account getAccountById(int id) {
		try {

			return accountRepository.getAccountById(id);
		}catch (AccountNotFoundException e) {
			throw new APIError(64, HttpStatus.NOT_FOUND);
		}
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
		try {
			accountRepository.getAccountById(id);
			return true;
		} catch (AccountNotFoundException e) {
			return false;
		}
	}

	public Account registerAccount(RegisterDto registerDto) throws DuplicateKeyException, APIError {

		Account account = new Account();
		account.setLogin(registerDto.getLogin());
		if(registerDto.getPassword() != null)
			account.setPassword(passwordEncoder.encode(registerDto.getPassword()));
		account.setVerificationToken(generateVerificationToken(6));
		UserInfo userInfo = new UserInfo();
		userInfo.setEmail(registerDto.getEmail());
		userInfo.setFirstName(registerDto.getFirstName());
		userInfo.setLastName(registerDto.getLastName());
		userInfo.setRole(UserRole.ROLE_USER);
		userInfo.setLastLoginAt(Timestamp.from(Instant.now()));
		account.setUserInfo(userInfo);
		try {
			int accountId = accountRepository.registerAccount(account);
			account.setAccountId(accountId);
			mailService.sendVerificationMail(account);
			return account;
		} catch (DuplicateKeyException e) {
			throw new APIError(6000,HttpStatus.BAD_REQUEST);
		}
	}

	public static String generateVerificationToken(int length) {
		Random rand = new Random();
		StringBuilder res = new StringBuilder();
		String chars = "123456789";
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
				accountRepository.updateVerificationToken(generateVerificationToken(6), account.getAccountId());
			}
		} catch (SQLException e) {
			throw new APIError(500, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (AccountNotFoundException e) {
			throw new APIError(64, HttpStatus.NOT_FOUND);
		}
		return true;
	}

	public String verifyToken(VerifyTokenDto verifyEmailDto) throws APIError {
		try {
			Account account = accountRepository.getAccountByEmail(verifyEmailDto.getEmail());
			if(!(Objects.equals(account.getVerificationToken(), verifyEmailDto.getVerificationCode()))) {
				throw new APIError(700, HttpStatus.BAD_REQUEST);
			} else {
				return verifyEmailDto.getVerificationCode();
			}
		} catch (AccountNotFoundException e) {
			throw new APIError(64, HttpStatus.NOT_FOUND);
		}
	}

	public void sendVerificationCode(String email) throws APIError {
		try {
			Account account = accountRepository.getAccountByEmail(email);
			String newToken = generateVerificationToken(6);
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

	public void deleteUser(DeleteAccount deleteAccount){
		try {
			accountRepository.deleteAccount(deleteAccount);
		} catch (AccountNotFoundException e) {
			log.error(e.getMessage());
			throw new APIError(64, HttpStatus.NOT_FOUND);
		}
	}
	public void recoverUser(DeleteAccount deleteAccount){

		try {
			accountRepository.recoverAccount(deleteAccount);
		} catch (AccountNotFoundException e) {
			throw new APIError(64, HttpStatus.NOT_FOUND);
		}
	}
	public List<Account> getAccountsByIds(Set<Integer> ids) throws AccountNotFoundException {
		int[] arrayIds = ids.stream()
				.mapToInt(Integer::intValue)
				.toArray();
		return accountDao.getAccountByIds(arrayIds);
	}
}
