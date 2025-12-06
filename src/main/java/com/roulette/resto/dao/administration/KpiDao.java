package com.roulette.resto.dao.administration;

import com.roulette.resto.data.administration.dto.UserRegistrationHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Repository
@Slf4j
public class KpiDao {
	final JdbcTemplate jdbcTemplate;
	final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public KpiDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	public List<UserRegistrationHistory> getNewUsersByYear(String year) {
		String query = "SELECT" +
				"    m.month," +
				"    IFNULL(a.total, 0) AS total" +
				" FROM (" +
				"         SELECT 1 AS month UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION" +
				"         SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION" +
				"         SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12" +
				"     ) AS m" +
				"         LEFT JOIN (" +
				"    SELECT" +
				"        MONTH(created_at) AS month," +
				"        COUNT(account_id) AS total" +
				"    FROM account" +
				"    WHERE YEAR(created_at) = ?" +
				"    GROUP BY YEAR(created_at), MONTH(created_at)) AS a ON m.month = a.month" +
				" ORDER BY m.month";
		try {
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, year);
			List<UserRegistrationHistory> userRegistrationHistory = new ArrayList<>();
			for (Map<String, Object> row : rows) {
				UserRegistrationHistory users = new UserRegistrationHistory();
				users.setTotal((Long) row.get("total"));
				users.setMonth((Long) row.get("month"));
				userRegistrationHistory.add(users);
			}
			return userRegistrationHistory;
		} catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public long getNbUsers() {
		String query = "SELECT COUNT(*) FROM account";
		try {
			return jdbcTemplate.queryForObject(query, Long.class);
		} catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public long getWheelLaunched() {
		String query = "SELECT SUM(wheel_launched) FROM user_info";
		try {
			return jdbcTemplate.queryForObject(query, Long.class);
		} catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public Float getRegistrationTrend(int currentMonth, int comparedMonth, int year) {
		String query = """
				    SELECT
				        curr.total AS current_month_total,
				        prev.total AS previous_month_total,
				        (curr.total - prev.total) AS difference,
				        ROUND(
				                    IF(prev.total > 0, ((curr.total - prev.total) / prev.total) * 100, NULL), 2
				        ) AS percentage_change
				    FROM (
				        SELECT COUNT(account_id) AS total
				        FROM account
				        WHERE MONTH(created_at) = :currentMonth AND YEAR(created_at) = :year
				    ) AS curr
				    JOIN (
				        SELECT COUNT(account_id) AS total
				        FROM account
				        WHERE MONTH(created_at) = :comparedMonth  AND YEAR(created_at) = :year
				    ) AS prev;
				""";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("currentMonth", currentMonth);
		params.addValue("comparedMonth", comparedMonth);
		params.addValue("year", year);
		Map<String, Object> data = namedParameterJdbcTemplate.queryForMap(query, params);

		BigDecimal percentage = (BigDecimal) data.get("percentage_change");
		return percentage != null ? percentage.floatValue() : null;
	}

	public Float getWheelTrend(int currentMonth, int comparedMonth, int year) {
		String query = """
				SELECT
				    curr.total AS current_month_total,
				    prev.total AS previous_month_total,
				    (curr.total - prev.total) AS difference,
				    ROUND(
				        IF(prev.total > 0, ((curr.total - prev.total) / prev.total) * 100, NULL), 2
				    ) AS percentage_change
				FROM (
				    SELECT SUM(u.wheel_launched) AS total
				    FROM account a
				    JOIN user_info u ON a.user_info_id = u.user_info_id
				    WHERE MONTH(a.created_at) = :currentMonth AND YEAR(a.created_at) = :year
				) AS curr
				JOIN (
				    SELECT SUM(u.wheel_launched) AS total
				    FROM account a
				    JOIN user_info u ON a.user_info_id = u.user_info_id
				    WHERE MONTH(a.created_at) = :comparedMonth AND YEAR(a.created_at) = :year
				) AS prev
				""";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("currentMonth", currentMonth);
		params.addValue("comparedMonth", comparedMonth);
		params.addValue("year", year);

		Map<String, Object> data = namedParameterJdbcTemplate.queryForMap(query, params);
		BigDecimal percentage = (BigDecimal) data.get("percentage_change");
		return percentage != null ? percentage.floatValue() : 0;
	}
}
