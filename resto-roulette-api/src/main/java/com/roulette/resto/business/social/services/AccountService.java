package com.roulette.resto.business.social.services;

import com.roulette.resto.business.social.repository.AccountRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;

@Service
public class AccountService implements UserDetailsService {
	final AccountRepository accountRepository;

	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public int getAccountIdByLogin(String login) throws AccountNotFoundException {
		return accountRepository.getAccountByLogin(login).getAccountId();
	}

	public boolean existsByLogin(String login) {
		try {
			accountRepository.getAccountByLogin(login);
			return true;
		}
		catch (EmptyResultDataAccessException e) {
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

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return accountRepository.getAccountByLogin(username);
	}
}
