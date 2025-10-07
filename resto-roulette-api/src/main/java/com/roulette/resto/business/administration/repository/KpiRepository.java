package com.roulette.resto.business.administration.repository;

import com.roulette.resto.business.administration.dto.UserRegistrationHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class KpiRepository {
	final JdbcTemplate jdbcTemplate;

	public KpiRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
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
				userRegistrationHistory.add(users);
			}
			return userRegistrationHistory;
		}
		catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
	}
}
