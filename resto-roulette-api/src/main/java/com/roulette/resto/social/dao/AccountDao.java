package com.roulette.resto.social.dao;

import com.roulette.resto.social.dto.in.UpdateUserInfo;
import com.roulette.resto.social.entity.Account;
import com.roulette.resto.social.entity.UserInfo;
import com.roulette.resto.social.mapper.AccountUserRowMapper;
import com.roulette.resto.social.mapper.UserInfoRowMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
@Repository
@Log4j2
public class AccountDao {
	final JdbcTemplate jdbcTemplate;

	public AccountDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public int registerAccount(Account account) {
		int userInfoId = registerUserInfo(account);
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

		String query = "INSERT INTO account (login, password, user_info_id, verification_token) " +
				"VALUES (?, ?, ?, ?)";
		try {
			jdbcTemplate.update(conn -> {
				PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setString(1, account.getLogin());
				preparedStatement.setString(2, account.getPassword());
				preparedStatement.setString(3, String.valueOf(userInfoId));
				preparedStatement.setString(4, account.getVerificationToken());
				return preparedStatement;
			}, generatedKeyHolder);
			return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
		} catch (DuplicateKeyException e) {
			log.error("Error registering account {}", account.getLogin());
			log.error(e.getMessage());
			throw e;
		}
	}

	public int registerUserInfo(Account account) {
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
		String query = "INSERT INTO user_info (last_name, first_name,email, type_id) " +
				"VALUES (?, ?, ?, ?)";
		try {
			jdbcTemplate.update(conn -> {
				PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setString(1, account.getUserInfo().getLastName());
				preparedStatement.setString(2, account.getUserInfo().getFirstName());
				preparedStatement.setString(3, account.getUserInfo().getEmail());
				preparedStatement.setInt(4, account.getUserInfo().getRole().getRoleId());
				return preparedStatement;
			}, generatedKeyHolder);
			return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
		} catch (DataAccessException e) {
			log.error("Error registering user_info {}", account.getLogin());
			log.error(e.getMessage());
			return -1;
		}
	}

	public Account getAccountByLogin(String login) throws AccountNotFoundException {
		String query = "SELECT account_id,login,password, verification_token, email, type_id as role, last_name, " +
				"first_name, email_verified " +
				"FROM account " +
				"JOIN user_info on account.user_info_id = user_info.user_info_id " +
				"WHERE login = ?";
		try {
			return jdbcTemplate.queryForObject(query, new AccountUserRowMapper(), login);
		} catch (EmptyResultDataAccessException e) {
			log.info("No user found with login {}", login);
			throw new AccountNotFoundException(e.getMessage());
		}
	}

	public Account getAccountByEmail(String email) throws AccountNotFoundException {
		String query = "SELECT account_id,login,password, verification_token, email, type_id as role, last_name, " +
				"first_name, email_verified " +
				"FROM account " +
				"JOIN user_info on account.user_info_id = user_info.user_info_id " +
				"WHERE email = ? ";
		try {
			return jdbcTemplate.queryForObject(query, new AccountUserRowMapper(), email);
		} catch (EmptyResultDataAccessException e) {
			log.info("No user found with email {}", email);

			throw new AccountNotFoundException(e.getMessage());
		}
	}

	public UserInfo getUserInfoByLogin(String login) {
		String query = "SELECT last_name, first_name, email, type_id as role, login " +
				"FROM user_info " +
				"JOIN account on user_info.user_info_id = account.user_info_id " +
				"WHERE account.login = ? ";
		try {
			return jdbcTemplate.queryForObject(query, new UserInfoRowMapper(), login);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("SQl error", e);
			throw e;
		}
	}

	public UserInfo getUserInfoById(int id) {
		String query = "SELECT last_name, first_name, email, type_id as role, login, email_verified " +
				"FROM user_info " +
				"JOIN account on user_info.user_info_id = account.user_info_id " +
				"WHERE account.account_id = ? ";
		try {
			return jdbcTemplate.queryForObject(query, new UserInfoRowMapper(), id);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("SQl error", e);
			throw e;
		}
	}

	public Account getAccountById(int id) {
		String query = "SELECT account_id,login,password, verification_token, email, type_id as role, last_name, " +
				"first_name, email_verified " +
				"FROM account " +
				"JOIN resto_roulette.user_info on account.user_info_id = user_info.user_info_id " +
				"WHERE account.account_id = ?";
		try {
			return jdbcTemplate.queryForObject(query, new AccountUserRowMapper(), id);
		} catch (DataAccessException e) {
			log.error("failed to acces users" + e.getMessage());
			throw e;
		}
	}

	public int updateUserInfo(String id, UpdateUserInfo newUserInfo) throws SQLException {
		String query = "UPDATE user_info " +
				" JOIN resto_roulette.account a on  user_info.user_info_id = a.user_info_id " +
				" SET user_info.email = ?, user_info.last_name = ?, user_info.first_name = ?" +
				" WHERE account_id = ?";
		try {
			return jdbcTemplate.update(query, newUserInfo.getEmail(),
					newUserInfo.getLastName(), newUserInfo.getFirstName(), id);
		} catch (DuplicateKeyException e) {
			log.error(e.getMessage());
			throw new SQLException(e);
		}
	}

	public void changePassword(int id, String newPassword) {
		String query = "UPDATE account " +
				" SET password = ? " +
				" WHERE account_id = ?";
		jdbcTemplate.update(query, newPassword, id);
	}

	public void updateMailVerificationStatus(int accountId, boolean isVerified) throws SQLException {
		String query = "UPDATE user_info " +
				" JOIN resto_roulette.account a on  user_info.user_info_id = a.user_info_id " +
				" SET user_info.email_verified = ? WHERE account_id = ?";
		try {
			jdbcTemplate.update(query, isVerified ? 1 : 0, accountId);
		} catch (DuplicateKeyException e) {
			log.error(e.getMessage());
			throw new SQLException(e);
		}
	}

	public void updateVerificationToken(String token, int accountId) {
		String query = "UPDATE account " +
				"SET account.verification_token = ? " +
				"WHERE account_id = ?";
		try {
			jdbcTemplate.update(query, token, accountId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}

	}
}
