package com.roulette.resto.business.social.services;

import com.roulette.resto.business.social.dto.RegisterDto;
import com.roulette.resto.business.social.entity.Account;
import com.roulette.resto.business.social.entity.UserInfo;
import com.roulette.resto.business.social.repository.AccountRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;



@Service
public class AccountService implements UserDetailsService {
	final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;


	public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
		this.accountRepository = accountRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public int getAccountIdByLogin(String login) throws AccountNotFoundException {
		return accountRepository.getAccountByLogin(login).getAccountId();
	}

	public boolean existsByLogin(String login) {
			Account account = accountRepository.getAccountByLogin(login);
			return account != null;
	}

	public boolean existsByEmail(String email) {
			Account account = accountRepository.getAccountByEmail(email);
			return account != null;
	}

	public void registerAccount(RegisterDto registerDto) {

		Account account = new Account();
		account.setLogin(registerDto.getLogin());
		account.setPassword(registerDto.getPassword());
		account.setPassword(passwordEncoder.encode(registerDto.getPassword()));

		UserInfo userInfo = new UserInfo();
		userInfo.setEmail(registerDto.getEmail());
		userInfo.setFirstName(registerDto.getFirstName());
		userInfo.setLastName(registerDto.getLastName());

		account.setUserInfo(userInfo);
		accountRepository.registerAccount(account);
	}
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return accountRepository.getAccountByLogin(username);
	}
}
