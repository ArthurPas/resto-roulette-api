package com.roulette.resto.dao.roulette;

import com.roulette.resto.data.roulette.dto.out.ActivityDto;
import com.roulette.resto.data.social.mapper.ActivityRowMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;

@Repository
@Log4j2
public class ActivityDao {

	final JdbcTemplate jdbcTemplate;

	public ActivityDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public int removeAccountFromActivity(int accountId, int activityId) {
		String query = "DELETE FROM activity WHERE account_id = ? AND activity_id = ?";
		return jdbcTemplate.update(query,accountId,activityId);
	}

	public List<ActivityDto> getActivitiesByAccountId(int accountId) {
		String query =
				"SELECT t1.*, " +
						"  (SELECT GROUP_CONCAT(DISTINCT account_id SEPARATOR ',') " +
						"   FROM activity t3 " +
						"   WHERE t3.session_id = t1.session_id) as participantsId " +
						"FROM activity t1 " +
						"WHERE t1.session_id IN ( " +
						"    SELECT DISTINCT session_id " +
						"    FROM activity " +
						"    WHERE account_id = ? " +
						") " +
						"AND t1.account_id = ?";
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
			log.warn("failed to get all accounts from database");
			return Collections.emptyList();
		}
	}
	
	public List<ActivityDto> getActivitiesBySessionId(String sessionId) {
		String query = "SELECT activity_id, account_id, resto_id, description, session_id FROM activity WHERE session_id " +
				"= ?";
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
			log.warn("failed to get all accounts from database");
			return Collections.emptyList();
		}
	}

	public ActivityDto getActivityById(int activityId) {
		String query =
				"SELECT t1.*, " +
						"  (SELECT GROUP_CONCAT(DISTINCT account_id SEPARATOR ',') " +
						"   FROM activity t3 " +
						"   WHERE t3.session_id = t1.session_id) as participantsId " +
						"FROM activity t1 " +
						"WHERE t1.activity_id = ?";
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
	}
}
