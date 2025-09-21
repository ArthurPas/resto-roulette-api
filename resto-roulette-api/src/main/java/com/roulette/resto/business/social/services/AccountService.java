package com.roulette.resto.business.social.services;

import com.roulette.resto.business.social.dto.RegisterDto;
import com.roulette.resto.business.social.entity.Account;
import com.roulette.resto.business.social.entity.UserInfo;
import com.roulette.resto.business.social.entity.UserRole;
import com.roulette.resto.business.social.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class AccountService implements UserDetailsService {
	final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;


	public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
		this.accountRepository = accountRepository;
		this.passwordEncoder = passwordEncoder;
	}


	public boolean existsByLogin(String login) {
		try {
			accountRepository.getAccountByLogin(login);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	public boolean existsByEmail(String email) {
		try {
			accountRepository.getAccountByEmail(email);
			return true;
		}
		catch (EmptyResultDataAccessException e) {
			return false;
		}
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
		userInfo.setRole(UserRole.ROLE_USER);
		account.setUserInfo(userInfo);
		accountRepository.registerAccount(account);
	}
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return accountRepository.getAccountByLogin(username);
	}
}
