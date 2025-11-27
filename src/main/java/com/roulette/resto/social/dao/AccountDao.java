package com.roulette.resto.social.dao;

import com.roulette.resto.common.entity.MediaResource;
import com.roulette.resto.common.entity.MediaType;
import com.roulette.resto.common.entity.mappers.MediaMapper;
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
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
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
				"first_name, email_verified, account.created_at, user_info.last_login_at, is_deleted " +
				"FROM account " +
				"JOIN user_info on account.user_info_id = user_info.user_info_id " +
				"WHERE login = ? ";
		try {
			return jdbcTemplate.queryForObject(query, new AccountUserRowMapper(), login);
		} catch (EmptyResultDataAccessException e) {
			log.info("No user found with login {}", login);
			throw new AccountNotFoundException(e.getMessage());
		}
	}

	public Account getAccountByEmail(String email) throws AccountNotFoundException {
		String query = "SELECT account_id,login,password, verification_token, email, type_id as role, last_name, " +
				"first_name, email_verified,account.created_at, user_info.last_login_at,is_deleted " +
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
				"first_name, email_verified,account.created_at, user_info.last_login_at,is_deleted " +
				"FROM account " +
				"JOIN resto_roulette.user_info on account.user_info_id = user_info.user_info_id " +
				"WHERE account.account_id = ? ";
		try {
			return jdbcTemplate.queryForObject(query, new AccountUserRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			log.error("failed to get account id : {}, error :{}",id, e.getMessage());
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
	public List<Account> getAll(int limit, int offset) {
		log.warn("Limit {}, Offset {}", limit, offset);
		String query = 	"SELECT account_id,login,password, verification_token, email, type_id as role, last_name, " +
				"first_name, email_verified, last_login_at, account.created_at, account.is_deleted " +
				"FROM account " +
				"JOIN user_info on account.user_info_id = user_info.user_info_id "+
				"WHERE is_deleted = false "+
				"ORDER BY account_id " +
				"LIMIT ? "+
				"OFFSET ? ";
		try {
			return jdbcTemplate.query(query, new AccountUserRowMapper(),  limit, offset);
		}catch (EmptyResultDataAccessException e){
			log.warn(e.getMessage());
			return null;
		}
	}
	public void updateAccountRole(int roleId, int accountId) {

		String query = "UPDATE user_info " +
				" JOIN resto_roulette.account a on  user_info.user_info_id = a.user_info_id " +
				" SET user_info.type_id = ? WHERE account_id = ?";
		jdbcTemplate.update(query, roleId, accountId);

	}

	public void deleteAccount(int accountId) {
		String query = "UPDATE account SET account.is_deleted = true WHERE account_id = ?";
		jdbcTemplate.update(query, accountId);
	}
	public void recoverAccount(int accountId) {
		String query = "UPDATE account SET account.is_deleted = false WHERE account_id = ?";
		jdbcTemplate.update(query, accountId);
	}
	
	public int getAccountId(Account account) throws AccountNotFoundException {
		String query = "SELECT account_id FROM account WHERE login = ? LIMIT 1";
		try {
			return jdbcTemplate.queryForObject(query, Integer.class, account.getLogin());
		}catch (NullPointerException e){
			log.warn(e.getMessage());
			throw new AccountNotFoundException(e.getMessage());
		}
	}

	public void updateLoginDate(int accountId) throws SQLException {
		String query = "UPDATE user_info " +
				" JOIN resto_roulette.account a on  user_info.user_info_id = a.user_info_id " +
				" SET user_info.last_login_at = ? WHERE account_id = ?";
		try {
			jdbcTemplate.update(query, Timestamp.from(Instant.now()), accountId);
		} catch (DuplicateKeyException e) {
			log.error(e.getMessage());
			throw new SQLException(e);
		}

	}

	public void saveMedia(int accountId, String uuid, MediaType mediaType) {
		try {
			String query = "INSERT INTO resto_roulette.user_user_medias (account_id,resource_id, media_type_id) " +
					"VALUES (?, ?, ?)";
			jdbcTemplate.update(query, accountId, uuid,mediaType.typeId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public List<MediaResource> getAccountPictures(int id) {
		String query = "SELECT resource_id, media_type_id " +
				" FROM resto_roulette.user_user_medias m " +
				" WHERE m.account_id = ? ";
		try {
			return jdbcTemplate.query(query, new MediaMapper(), id);
		}catch (EmptyResultDataAccessException e){
			return Collections.emptyList();
		}
	}

	public void updateAvatar(int accountId, String uuid) {
		String query = "UPDATE resto_roulette.user_user_medias m " +
				" SET m.resource_id = ? " +
				" WHERE m.account_id = ? AND m.media_type_id = ?";
		try {
			jdbcTemplate.update(query, uuid, accountId, MediaType.AVATAR.typeId );
		}catch (DataAccessException e){
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
