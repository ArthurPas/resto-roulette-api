package com.roulette.resto.business.social.repository;

import com.roulette.resto.business.social.dto.mapper.AccountRowMapper;
import com.roulette.resto.business.social.entity.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepository {
	final JdbcTemplate jdbcTemplate;

	public AccountRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void registerAccount(Account account) {
		String query = "INSERT INTO account (login, password, user_info_id)" +
				" VALUES (?, ?, ?)";
		jdbcTemplate.update(query, account.getLogin(), account.getPassword(), 1);
	}

	public Account getAccountByLogin(String login) {
		String query = "SELECT account_id,login,password FROM account WHERE login = ?";
		return jdbcTemplate.queryForObject(query, new AccountRowMapper(), login);
	}
}
