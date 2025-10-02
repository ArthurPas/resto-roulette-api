package com.roulette.resto.business.social.services;

import com.roulette.resto.business.social.dto.in.RegisterDto;
import com.roulette.resto.business.social.dto.in.VerifyEmailDto;
import com.roulette.resto.business.social.entity.Account;
import com.roulette.resto.business.social.entity.UserInfo;
import com.roulette.resto.business.social.entity.UserRole;
import com.roulette.resto.business.social.repository.AccountRepository;
import com.roulette.resto.common.dao.SendEmail;
import com.roulette.resto.common.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
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


	public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder, SendEmail sendEmail, MailService mailService) {
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


	public boolean existsByLogin(String login)  {
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
	public boolean existsById(int id)  {
		Account account = accountRepository.getAccountById(id);
		return account != null;
	}

	public Account registerAccount(RegisterDto registerDto) throws DuplicateKeyException{

		Account account = new Account();
		account.setLogin(registerDto.getLogin());
		account.setPassword(registerDto.getPassword());
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
		}catch (DuplicateKeyException e) {
			log.error(e.getMessage());
			throw e;
		}
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

	public static String generateVerificationToken(int length) {
		Random rand = new Random();
		StringBuilder res = new StringBuilder();
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		for (int i = 0; i < length; i++) {
			int randIndex=rand.nextInt(chars.length());
			res.append(chars.charAt(randIndex));
		}
		return res.toString();
	}

	public boolean verifyEmail(VerifyEmailDto verifyEmailDto) throws AccountNotFoundException, SQLException {
		try {
			Account account = accountRepository.getAccountByLogin(verifyEmailDto.getLogin());
			if(!(Objects.equals(account.getVerificationToken(), verifyEmailDto.getVerificationCode()))){
				return false;
			}else {
				accountRepository.updateMailVerificationStatus(account.getAccountId(), true);
				return true;
			}
		} catch (AccountNotFoundException e) {
			log.error(e.getMessage());
			throw e;
		} catch (SQLException e) {
			log.error(e.getMessage());
			throw e;
		}

	}

	public void resendEmail(String accountId) {
		mailService.sendVerificationMail(accountRepository.getAccountById(Integer.parseInt(accountId)));
	}
}
