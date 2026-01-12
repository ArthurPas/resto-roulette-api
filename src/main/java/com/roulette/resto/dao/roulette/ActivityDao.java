package com.roulette.resto.dao.roulette;

import com.roulette.resto.data.roulette.Activity;
import com.roulette.resto.data.roulette.dto.out.ActivityDto;
import com.roulette.resto.data.social.mapper.AccountUserRowMapper;
import com.roulette.resto.data.social.mapper.ActivityRowMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
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
		String query = "SELECT activity_id, account_id, resto_id, description FROM activity WHERE account_id = ?";
		try {
			return jdbcTemplate.query(
					connection -> {
						PreparedStatement preparedStatement = connection.prepareStatement(query);
						preparedStatement.setInt(1, accountId);
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
}
