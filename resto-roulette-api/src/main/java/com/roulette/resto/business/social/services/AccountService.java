package com.roulette.resto.business.social.services;

import com.roulette.resto.business.social.dto.RegisterDto;
import com.roulette.resto.business.social.entity.Account;
import com.roulette.resto.business.social.entity.UserInfo;
import com.roulette.resto.business.social.entity.UserRole;
import com.roulette.resto.business.social.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;



@Slf4j
@Service
public class AccountService implements UserDetailsService {
	final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;


	public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
		this.accountRepository = accountRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public Account getAccountByLogin(String login) throws AccountNotFoundException {
		return accountRepository.getAccountByLogin(login);
	}
	public Account getAccountById(int id) throws AccountNotFoundException {
		return accountRepository.getAccountById(id);
	}


	public boolean existsByLogin(String login) {
			Account account = accountRepository.getAccountByLogin(login);
			return account != null;
	}

	public boolean existsByEmail(String email) {
			Account account = accountRepository.getAccountByEmail(email);
			return account != null;
	}
	public boolean existsById(int id) {
		Account account = accountRepository.getAccountById(id);
		return account != null;
	}

	public Account registerAccount(RegisterDto registerDto) throws DuplicateKeyException{

		Account account = new Account();
		account.setLogin(registerDto.getLogin());
		account.setPassword(registerDto.getPassword());
		account.setPassword(passwordEncoder.encode(registerDto.getPassword()));

		UserInfo userInfo = new UserInfo();
		userInfo.setEmail(registerDto.getEmail());
		userInfo.setFirstName(registerDto.getFirstName());
		userInfo.setLastName(registerDto.getLastName());
		userInfo.setRole(UserRole.ROLE_USER);
		account.setUserInfo(userInfo);
		try {
			accountRepository.registerAccount(account);
			return account;
		}catch (DuplicateKeyException e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return accountRepository.getAccountByLogin(username);
	}
}
