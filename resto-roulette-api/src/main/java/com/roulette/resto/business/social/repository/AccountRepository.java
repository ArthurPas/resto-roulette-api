package com.roulette.resto.business.social.repository;

import com.roulette.resto.business.social.dto.UserInfoDto;
import com.roulette.resto.business.social.dto.mapper.AccountRowMapper;
import com.roulette.resto.business.social.dto.mapper.UserInfoRowMapper;
import com.roulette.resto.business.social.entity.Account;
import com.roulette.resto.business.social.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;

@Slf4j
@Repository
public class AccountRepository {
	final JdbcTemplate jdbcTemplate;

	public AccountRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public int registerUserInfo(Account account) {
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
		String query = 	"INSERT INTO user_info (last_name, first_name,email, type_id) " +
						"VALUES (?, ?, ?, ?)";
		try {
			jdbcTemplate.update(conn -> {
				PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setString(1, account.getUserInfo().getLastName());
				preparedStatement.setString(2, account.getUserInfo().getFirstName());
				preparedStatement.setString(3, account.getUserInfo().getEmail());
				preparedStatement.setInt(4, account.getUserInfo().getRole().roleId);
				return preparedStatement;
			},generatedKeyHolder);
			return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
		}catch (DataAccessException e) {
			log.error("Error registering user_info {}", account.getLogin());
			log.error(e.getMessage());
			return -1;
		}
	}


	public int registerAccount(Account account) {
		int userInfoId = registerUserInfo(account);
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

		String query = 	"INSERT INTO account (login, password, user_info_id) " +
						"VALUES (?, ?, ?)";
		try {
			jdbcTemplate.update(conn -> {
				PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setString(1, account.getLogin());
				preparedStatement.setString(2, account.getPassword());
				preparedStatement.setString(3, String.valueOf(userInfoId));
				return preparedStatement;
				},generatedKeyHolder);
			return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
		} catch (DataAccessException e){
			log.error("Error registering account {}", account.getLogin());
			log.error(e.getMessage());
			return -1;
		}
	}

	public Account getAccountByLogin(String login) throws UsernameNotFoundException {
		String query = 	"SELECT account_id,login,password " +
						"FROM account " +
						"WHERE login = ?";
		try {
			return jdbcTemplate.queryForObject(query, new AccountRowMapper(), login);
		}catch (DataAccessException e){
			log.error("Error retrieving account by login {}", login);
			log.error(e.getMessage());
			throw e;
		}
	}

	public Account getAccountByEmail(String email) {
		String query = 	"SELECT account_id,login,password,email " +
						"FROM account " +
						"JOIN user_info on account.user_info_id = user_info.user_info_id " +
						"WHERE email = ? ";
		try {
			return jdbcTemplate.queryForObject(query, new AccountRowMapper(), email);
		}
		catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public UserInfo getUserInfoByLogin(String login)  {
		String query = 	"SELECT last_name, first_name, email, type_id as role, login " +
						"FROM user_info " +
						"JOIN account on user_info.user_info_id = account.user_info_id "+
						"WHERE account.login = ? ";
		try {
			return jdbcTemplate.queryForObject(query, new UserInfoRowMapper(), login);
		}
		catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
		catch (Exception e){
			log.error("SQl error",e);
			throw e;
		}
	}
}
