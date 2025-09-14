package com.roulette.resto.business.social.repository;

import com.roulette.resto.business.social.dto.mapper.AccountRowMapper;
import com.roulette.resto.business.social.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Slf4j
@Repository
public class AccountRepository {
	final JdbcTemplate jdbcTemplate;

	public AccountRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void registerAccount(Account account) {
		String query = "INSERT INTO account (login, password, user_info_id)" +
				" VALUES (?, ?, ?)";
		try {
			jdbcTemplate.update(query, account.getLogin(), account.getPassword(), 1);
		} catch (DataAccessException e){
			log.error("Error registering account {}", account.getLogin());
			log.error(e.getMessage());
		}
	}

	public Account getAccountByLogin(String login) throws UsernameNotFoundException {
		String query = "SELECT account_id,login,password FROM account WHERE login = ?";
		return jdbcTemplate.queryForObject(query, new AccountRowMapper(), login);
	}

	public Account getAccountByEmail(String email) {
		String query = "SELECT account_id,login,password,mail  FROM account JOIN user_info on account.user_info_id =" +
				" " +
				"user_info.user_info_id  WHERE" +
				" mail = ?";
		try {
			return jdbcTemplate.queryForObject(query, new AccountRowMapper(), email);
		}
		catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
	}
}
