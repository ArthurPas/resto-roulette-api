package com.roulette.resto.dao.social;

import com.roulette.resto.data.common.entity.MediaResource;
import com.roulette.resto.data.common.entity.MediaType;
import com.roulette.resto.data.common.entity.mappers.MediaMapper;
import com.roulette.resto.data.social.dto.in.UpdateUserInfo;
import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.entity.MinimalAccountInfo;
import com.roulette.resto.data.social.entity.UserInfo;
import com.roulette.resto.data.social.mapper.AccountUserRowMapper;
import com.roulette.resto.data.social.mapper.MinimalAccountRowMapper;
import com.roulette.resto.data.social.mapper.UserInfoRowMapper;
import com.roulette.resto.exception.APIError;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.*;
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
				log.debug(preparedStatement.toString());
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
		String query = "INSERT INTO user_info (last_name, first_name,email, type_id,last_login_at) " +
				"VALUES (?, ?, ?, ?, ?)";
		try {
			jdbcTemplate.update(conn -> {
				PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setString(1, account.getUserInfo().getLastName());
				preparedStatement.setString(2, account.getUserInfo().getFirstName());
				preparedStatement.setString(3, account.getUserInfo().getEmail());
				preparedStatement.setInt(4, account.getUserInfo().getRole().getRoleId());
				preparedStatement.setTimestamp(5, account.getUserInfo().getLastLoginAt());
				log.debug(preparedStatement.toString());
				return preparedStatement;
			}, generatedKeyHolder);
			return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
		} catch (DataAccessException e) {
			log.error("Error registering user_info {}", account.getLogin());
			log.error(e.getMessage());
			throw new RuntimeException();
		}
	}

	public Account getAccountByLogin(String login) throws AccountNotFoundException {
		String query =
				"SELECT account.account_id, login, password, verification_token, email, type_id AS role, " +
						"last_name, first_name, email_verified, account.created_at, user_info.last_login_at, is_deleted, " +
						"uum.resource_id AS avatar " +
						"FROM account " +
						"JOIN user_info ON account.user_info_id = user_info.user_info_id " +
						"LEFT JOIN resto_roulette.user_user_medias uum " +
						"  ON account.account_id = uum.account_id " +
						" AND uum.media_type_id = (SELECT media_type_id FROM media_type WHERE type = 'AVATAR') " +
						"WHERE login = ?";

		try {
			List<Account> accounts = jdbcTemplate.query(
					connection -> {
						PreparedStatement preparedStatement = connection.prepareStatement(query);
						preparedStatement.setString(1, login);
						log.debug("SQL request : {}", preparedStatement.toString());
						log.debug(preparedStatement.toString());
						return preparedStatement;
					},
					new AccountUserRowMapper()
			);
			return DataAccessUtils.requiredSingleResult(accounts);
		} catch (EmptyResultDataAccessException e) {
			log.info("No user found with login {}", login);
			throw new AccountNotFoundException(e.getMessage());
		}
	}

	public Account getAccountByEmail(String email) throws AccountNotFoundException {
		String query =
				"SELECT account.account_id, login, password, verification_token, email, type_id AS role, " +
						"last_name, first_name, email_verified, account.created_at, user_info.last_login_at, is_deleted, " +
						"uum.resource_id AS avatar " +
						"FROM account " +
						"JOIN user_info ON account.user_info_id = user_info.user_info_id " +
						"LEFT JOIN resto_roulette.user_user_medias uum " +
						"  ON account.account_id = uum.account_id " +
						" AND uum.media_type_id = (SELECT media_type_id FROM media_type WHERE type = 'AVATAR') " +
						"WHERE email = ?";
		try {
			List<Account> accounts = jdbcTemplate.query(
					connection -> {
						PreparedStatement preparedStatement = connection.prepareStatement(query);
						preparedStatement.setString(1, email);
						log.debug(preparedStatement.toString());
						return preparedStatement;
					},
					new AccountUserRowMapper()
			);
			return DataAccessUtils.requiredSingleResult(accounts);
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
			List<UserInfo> users = jdbcTemplate.query(
					connection -> {
						PreparedStatement preparedStatement = connection.prepareStatement(query);
						preparedStatement.setString(1, login);
						log.debug(preparedStatement.toString());
						return preparedStatement;
					},
					new UserInfoRowMapper()
			);
			return DataAccessUtils.requiredSingleResult(users);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("SQl error", e);
			throw e;
		}
	}

	public UserInfo getUserInfoById(int id) {
		String query = "SELECT last_name, first_name, email, type_id as role, login, email_verified,last_login_at " +
				"FROM user_info " +
				"JOIN account on user_info.user_info_id = account.user_info_id " +
				"WHERE account.account_id = ? ";
		try {
			List<UserInfo> users = jdbcTemplate.query(
					connection -> {
						PreparedStatement preparedStatement = connection.prepareStatement(query);
						preparedStatement.setInt(1, id);
						log.debug(preparedStatement.toString());
						return preparedStatement;
					},
					new UserInfoRowMapper()
			);
			return DataAccessUtils.requiredSingleResult(users);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("SQl error", e);
			throw e;
		}
	}

	public Account getAccountById(int id) throws AccountNotFoundException {
		String query =
				"SELECT account.account_id, login, password, verification_token, email, type_id AS role, " +
						"last_name, first_name, email_verified, account.created_at, user_info.last_login_at, is_deleted, " +
						"uum.resource_id AS avatar " +
						"FROM account " +
						"JOIN user_info ON account.user_info_id = user_info.user_info_id " +
						"LEFT JOIN resto_roulette.user_user_medias uum " +
						"  ON account.account_id = uum.account_id " +
						" AND uum.media_type_id = (SELECT media_type_id FROM media_type WHERE type = 'AVATAR') " +
							"WHERE account.account_id = ? ";
		try {
			List<Account> accounts = jdbcTemplate.query(
					connection -> {
						PreparedStatement preparedStatement = connection.prepareStatement(query);
						preparedStatement.setInt(1, id);
						log.debug(preparedStatement.toString());
						return preparedStatement;
					},
					new AccountUserRowMapper()
			);
			return DataAccessUtils.requiredSingleResult(accounts);
		} catch (EmptyResultDataAccessException e) {
			log.error("failed to get account id : {}, error :{}",id, e.getMessage());
			throw new AccountNotFoundException(e.getMessage());
		}
	}

	public int updateUserInfo(String id, UpdateUserInfo newUserInfo) throws SQLException {

		String query = "UPDATE user_info " +
				" JOIN resto_roulette.account a on  user_info.user_info_id = a.user_info_id " +
				" SET user_info.email = ?, user_info.last_name = ?, user_info.first_name = ?" +
				" WHERE account_id = ?";
		try {
			return jdbcTemplate.update(connection -> {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, newUserInfo.getEmail());
				preparedStatement.setString(2, newUserInfo.getLastName());
				preparedStatement.setString(3, newUserInfo.getFirstName());
				preparedStatement.setString(4, id);
				log.debug(preparedStatement.toString());
				return preparedStatement;
			});
		}catch (DuplicateKeyException ex){
			log.error(ex.getMessage());
			throw new APIError(600, HttpStatus.BAD_REQUEST);
		}
		catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new SQLException(e);
		}
	}

	public void changePassword(int id, String newPassword) {
		String query = "UPDATE account " +
				" SET password = ? " +
				" WHERE account_id = ?";
		try {

			jdbcTemplate.update(connection -> {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, newPassword);
				preparedStatement.setInt(2, id);
				log.debug(preparedStatement.toString());
				return preparedStatement;
			});
		}catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public void updateMailVerificationStatus(int accountId, boolean isVerified) throws SQLException {
		String query = "UPDATE user_info " +
				" JOIN resto_roulette.account a on  user_info.user_info_id = a.user_info_id " +
				" SET user_info.email_verified = ? WHERE account_id = ?";
		try {
			jdbcTemplate.update(connection -> {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setInt(1, isVerified ? 1 : 0);
				preparedStatement.setInt(2, accountId);
				log.debug(preparedStatement.toString());
				return preparedStatement;
			});
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
			jdbcTemplate.update(connection -> {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, token);
				preparedStatement.setInt(2, accountId);
				log.debug(preparedStatement.toString());
				return preparedStatement;
			});
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public List<Account> getAll(int limit, int offset) {
		String query =
				"SELECT account.account_id, login, password, verification_token, email, type_id AS role, " +
						"last_name, first_name, email_verified, account.created_at, user_info.last_login_at, is_deleted, " +
						"uum.resource_id AS avatar " +
						"FROM account " +
						"JOIN user_info ON account.user_info_id = user_info.user_info_id " +
						"LEFT JOIN resto_roulette.user_user_medias uum " +
						"  ON account.account_id = uum.account_id " +
						" AND uum.media_type_id = (SELECT media_type_id FROM media_type WHERE type = 'AVATAR') " +
						"ORDER BY account_id " +
						"LIMIT ? "+
						"OFFSET ? ";
		try {
			return jdbcTemplate.query(
					connection -> {
						PreparedStatement preparedStatement = connection.prepareStatement(query);
						preparedStatement.setInt(1, limit);
						preparedStatement.setInt(2, offset);
						log.debug(preparedStatement.toString());
						return preparedStatement;
					},
					new AccountUserRowMapper()
			);
		} catch (DataAccessException e) {
			log.warn("failed to get all accounts from database");
			return Collections.emptyList();
		}
	}

	public void updateAccountRole(int roleId, int accountId) {
		String query = "UPDATE user_info " +
				" JOIN resto_roulette.account a on  user_info.user_info_id = a.user_info_id " +
				" SET user_info.type_id = ? WHERE account_id = ?";
		try {

			jdbcTemplate.update(connection -> {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setInt(1, roleId);
				preparedStatement.setInt(2, accountId);
				log.debug(preparedStatement.toString());
				return preparedStatement;
			});
		}catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public void deleteAccount(int accountId) {
		String query = "UPDATE account SET account.is_deleted = true WHERE account_id = ?";
		try {

			jdbcTemplate.update(connection -> {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setInt(1, accountId);
				log.debug(preparedStatement.toString());
				return preparedStatement;
			});
		}catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	public void recoverAccount(int accountId) {
		String query = "UPDATE account SET account.is_deleted = false WHERE account_id = ?";
		try {

			jdbcTemplate.update(connection -> {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setInt(1, accountId);
				log.debug(preparedStatement.toString());
				return preparedStatement;
			});
		}catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public int getAccountId(Account account) throws AccountNotFoundException {
		String query = "SELECT account_id FROM account WHERE login = ? LIMIT 1";
		try {
			List<Integer> ids = jdbcTemplate.query(
					connection -> {
						PreparedStatement preparedStatement = connection.prepareStatement(query);
						preparedStatement.setString(1, account.getLogin());
						log.debug(preparedStatement.toString());
						return preparedStatement;
					},
					(rs, rowNum) -> rs.getInt("account_id")
			);
			return DataAccessUtils.requiredSingleResult(ids);
		} catch (DataAccessException | NullPointerException e) {
			throw new AccountNotFoundException(e.getMessage());
		}
	}

	public void updateLoginDate(int accountId) throws SQLException {
		String query = "UPDATE user_info " +
				" JOIN resto_roulette.account a on  user_info.user_info_id = a.user_info_id " +
				" SET user_info.last_login_at = ? WHERE account_id = ?";
		try {
			jdbcTemplate.update(connection -> {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setTimestamp(1, Timestamp.from(Instant.now()));
				preparedStatement.setInt(2, accountId);
				log.debug(preparedStatement.toString());
				return preparedStatement;
			});
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public void saveMedia(int accountId, String uuid, MediaType mediaType) {
		try {
			String query = "INSERT INTO resto_roulette.user_user_medias (account_id,resource_id, media_type_id) " +
					"VALUES (?, ?, ?)";
			jdbcTemplate.update(connection -> {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setInt(1, accountId);
				preparedStatement.setString(2, uuid);
				preparedStatement.setInt(3, mediaType.typeId);
				log.debug(preparedStatement.toString());
				return preparedStatement;
			});
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public List<MediaResource> getAccountPictures(int id) {
		String query = "SELECT resource_id, media_type_id " +
				" FROM resto_roulette.user_user_medias m " +
				" WHERE m.account_id = ? ";
		try {
			return jdbcTemplate.query(
					connection -> {
						PreparedStatement preparedStatement = connection.prepareStatement(query);
						preparedStatement.setInt(1, id);
						log.debug(preparedStatement.toString());
						return preparedStatement;
					},
					new MediaMapper()
			);
		} catch (EmptyResultDataAccessException e) {
			return Collections.emptyList();
		}
	}

	public void updateAvatar(int accountId, String uuid) {
		String query = "UPDATE resto_roulette.user_user_medias m " +
				" SET m.resource_id = ? " +
				" WHERE m.account_id = ? AND m.media_type_id = ?";
		try {
			jdbcTemplate.update(connection -> {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, uuid);
				preparedStatement.setInt(2, accountId);
				preparedStatement.setInt(3, MediaType.AVATAR.typeId);
				log.debug(preparedStatement.toString());
				return preparedStatement;
			});
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public void newFollowRequest(int accountAsker, int accountAsked) throws AccountNotFoundException {
		try {
			String query = "INSERT INTO follower (ask_account_id,asked_account_id) " +
					"VALUES (?, ?)";
			jdbcTemplate.update(connection -> {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setInt(1, accountAsker);
				preparedStatement.setInt(2, accountAsked);
				log.debug(preparedStatement.toString());
				return preparedStatement;
			});
		} catch (DataIntegrityViolationException e) {
			log.error(e.getMessage());
			throw new AccountNotFoundException("accountAsked for follow doesnt exist");
		}
	}

	public void acceptFollowRequest(int accountAskerId, int accountAskedId) throws AccountNotFoundException {
		try {
			String query = 	"UPDATE follower SET accepted_date = ? " +
							"WHERE asked_account_id = ? AND ask_account_id = ?";
			jdbcTemplate.update(connection -> {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setDate(1, new Date(System.currentTimeMillis()));
				preparedStatement.setInt(2, accountAskedId);
				preparedStatement.setInt(3, accountAskerId);
				log.debug(preparedStatement.toString());
				return preparedStatement;
			});
		} catch (DataIntegrityViolationException e) {
			log.error(e.getMessage());
			throw new AccountNotFoundException("accountAsked for follow doesnt exist");
		}
	}

	public void unfollow(int accountToUnfollow, int accountId) throws AccountNotFoundException {
		try {
			String query = 	"DELETE FROM follower WHERE asked_account_id = ? AND ask_account_id = ?";
			jdbcTemplate.update(connection -> {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.setInt(1, accountToUnfollow);
				preparedStatement.setInt(2, accountId);
				log.debug(preparedStatement.toString());
				return preparedStatement;
			});
		} catch (DataIntegrityViolationException e) {
			log.error(e.getMessage());
			throw new AccountNotFoundException("accountAsked for follow doesnt exist");
		}
	}

	public List<MinimalAccountInfo> getFollowersByAccountId(int accountId) {
		String query =	"SELECT  account.login, account.account_id, uum.resource_id AS avatar " +
				"FROM account " +
				"JOIN user_info ON account.user_info_id = user_info.user_info_id " +
				"LEFT JOIN resto_roulette.user_user_medias uum " +
				"  ON account.account_id = uum.account_id " +
				" AND uum.media_type_id = (SELECT media_type_id FROM media_type WHERE type = 'AVATAR') " +
				"WHERE account.account_id IN (SELECT follower.asked_account_id " +
						"FROM account " +
						"JOIN follower on  account.account_id = follower.ask_account_id " +
						"WHERE follower.accepted_date IS NOT NULL and account.account_id = ?)";

		List<MinimalAccountInfo> accounts = jdbcTemplate.query(
				connection -> {
					PreparedStatement preparedStatement = connection.prepareStatement(query);
					preparedStatement.setInt(1, accountId);
					log.debug(preparedStatement.toString());
					return preparedStatement;
				},
				new MinimalAccountRowMapper()
		);
		return accounts;
	}
}