package com.roulette.resto.dao.roulette;

import com.roulette.resto.data.roulette.dto.out.ActivityDto;
import com.roulette.resto.data.roulette.dto.out.RouletteSessionDto;
import com.roulette.resto.data.social.mapper.ActivityRowMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Repository
@Log4j2
public class ActivityDao {

	final JdbcTemplate jdbcTemplate;

	public ActivityDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public int removeAccountFromActivity(int accountId, int activityId) {
		String query = """
             DELETE FROM activity WHERE account_id = ? AND activity_id = ?
             """;
		return jdbcTemplate.update(query,accountId,activityId);
	}

	public List<ActivityDto> getActivitiesByAccountId(int accountId) {
		String query = """
             SELECT t1.*,
               (SELECT GROUP_CONCAT(DISTINCT account_id SEPARATOR ',')
                FROM activity t3
                WHERE t3.session_id = t1.session_id) as participantsId
             FROM activity t1
             WHERE t1.session_id IN (
                 SELECT DISTINCT session_id
                 FROM activity
                 WHERE account_id = ?
             )
             AND t1.account_id = ?
             """;
		try {
			return jdbcTemplate.query(
					connection -> {
						PreparedStatement preparedStatement = connection.prepareStatement(query);
						preparedStatement.setInt(1, accountId);
						preparedStatement.setInt(2, accountId);
						log.debug(preparedStatement.toString());
						return preparedStatement;
					},
					new ActivityRowMapper()
			);
		} catch (DataAccessException e) {
			log.warn("failed to get all activities from database");
			log.error(e.getMessage());
			return Collections.emptyList();
		}
	}

	public List<ActivityDto> getActivitiesBySessionId(String sessionId) {
		String query = """
             SELECT activity_id, account_id, resto_id, description, session_id FROM activity WHERE session_id
             = ?
             """;
		try {
			return jdbcTemplate.query(
					connection -> {
						PreparedStatement preparedStatement = connection.prepareStatement(query);
						preparedStatement.setString(1, sessionId);
						log.debug(preparedStatement.toString());
						return preparedStatement;
					},
					new ActivityRowMapper()
			);
		} catch (DataAccessException e) {
			log.warn("failed to get all actvities from database");
			log.error(e.getMessage());
			return Collections.emptyList();
		}
	}

	public ActivityDto getActivityById(int activityId) {
		try {
			String query = """
                SELECT t1.*,
                  (SELECT GROUP_CONCAT(DISTINCT account_id SEPARATOR ',')
                   FROM activity t3
                   WHERE t3.session_id = t1.session_id) as participantsId
                FROM activity t1
                WHERE t1.activity_id = ?
                """;
			List<ActivityDto> results = jdbcTemplate.query(
					connection -> {
						PreparedStatement preparedStatement = connection.prepareStatement(query);
						preparedStatement.setInt(1, activityId);
						log.debug(preparedStatement.toString());
						return preparedStatement;
					},
					new ActivityRowMapper()
			);
			return DataAccessUtils.requiredSingleResult(results);
		}catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	public String createActivity(int accountId, String description, int restoId) {
		String uuid = UUID.randomUUID().toString().replace("-", "");
		return this.createActivity(accountId, description, restoId, uuid);
	}

	public String createActivity(int accountId, String description, int restoId, String sessionId) {
		String query = """
             INSERT INTO activity (resto_id, account_id, description, session_id)
             VALUES (?, ?,?, ?)
             """;
		try {
			jdbcTemplate.update(conn -> {
				PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setInt(1, restoId);
				preparedStatement.setInt(2, accountId);
				preparedStatement.setString(3, description);
				preparedStatement.setString(4,sessionId);
				log.debug("Executing query {}",preparedStatement);
				return preparedStatement;
			});
			return sessionId;
		} catch (DuplicateKeyException e) {
			log.error(e.getMessage());
			throw e;
		}
	}
}