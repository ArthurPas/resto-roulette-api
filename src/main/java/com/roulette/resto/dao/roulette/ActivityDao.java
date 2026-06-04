package com.roulette.resto.dao.roulette;

import com.roulette.resto.data.roulette.ActivityDto;
import com.roulette.resto.data.social.dto.out.CommentInfo;
import com.roulette.resto.data.social.mapper.ActivityRowMapper;
import com.roulette.resto.data.social.mapper.CommentInfoRowMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

@Repository
@Log4j2
public class ActivityDao {

	final JdbcTemplate jdbcTemplate;
	final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ActivityDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
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

	public List<CommentInfo> getCommentsByActivityId(int activityId) {
		String query = """
				SELECT comment.comment_id, activity_id, account_id as author_id, content as comment_text FROM comment WHERE activity_id = ?
				""";
		return jdbcTemplate.query(
				connection -> {
					PreparedStatement preparedStatement = connection.prepareStatement(query);
					preparedStatement.setInt(1, activityId);
					log.debug(preparedStatement.toString());
					return preparedStatement;
				},
				new CommentInfoRowMapper()
		);
	}

	public void createActivities(List<Integer> participantsId, String description, String sessionId, int restoId) {
		String query = """
          INSERT INTO activity (resto_id, account_id, description, session_id)
          VALUES (?, ?, ?, ?)""";

		jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, restoId);
				ps.setInt(2, participantsId.get(i));
				ps.setString(3, description);
				ps.setString(4, sessionId);
			}

			@Override
			public int getBatchSize() {
				return participantsId.size();
			}
		});
	}

	public int getNbActivityPendingByAccountId(int accountId) {
		String query = """
            SELECT COUNT(*)
            FROM activity 
            WHERE is_uploaded = false AND account_id = ?
            """;
		Integer count = jdbcTemplate.queryForObject(
				query,
				Integer.class,
				accountId
		);

		return (count != null) ? count : 0;
	}

	public List<ActivityDto> getActivitiesByAccountIds(List<Integer> followersIds) {
		if (followersIds == null || followersIds.isEmpty()) {
			return Collections.emptyList();
		}

		String sql = """
            SELECT t1.*,
              (SELECT GROUP_CONCAT(DISTINCT account_id SEPARATOR ',')
               FROM activity t3
               WHERE t3.session_id = t1.session_id) as participantsId
            FROM activity t1
            WHERE t1.session_id IN (
                SELECT DISTINCT session_id
                FROM activity
                WHERE account_id IN (:ids)
            )
            AND t1.account_id IN (:ids)
            """;

		MapSqlParameterSource parameters = new MapSqlParameterSource("ids", followersIds);

		return namedParameterJdbcTemplate.query(sql, parameters, new ActivityRowMapper());
	}

	public int updateActivityDescription(int id, String description) {
		String query = """
					  UPDATE activity SET description =  ? WHERE activity_id = ?;
					""";
			return jdbcTemplate.update(conn -> {
				PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setString(1, description);
				preparedStatement.setInt(2, id);
				log.debug(preparedStatement.toString());
				return preparedStatement;
			});
	}

	public int updateUploadStatus(int id, boolean upload) {
		String query = """
					  UPDATE activity SET is_uploaded =  ? WHERE activity_id = ?;
					""";
		return jdbcTemplate.update(conn -> {
			PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setBoolean(1, upload);
			preparedStatement.setInt(2, id);
			log.debug(preparedStatement.toString());
			return preparedStatement;
		});
	}

	public boolean hasUserLiked(int activityId, int accountId) {
		String query = """
          SELECT account_id FROM activity_user_likes WHERE account_id = ? AND activity_id = ?""";
		try {
			Integer id = jdbcTemplate.queryForObject(
					query,
					Integer.class,
					accountId,
					activityId
			);
			return id != null;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	public void likeActivity(int accountId, int activityId) {
		String query = """
        	INSERT INTO activity_user_likes (account_id, activity_id) VALUES (?, ?);
        	""";
		try {
			jdbcTemplate.update(query, accountId, activityId);
		}catch (DuplicateKeyException e) {
			log.warn(e.getMessage());
			log.warn("Duplicate key exception, activity (id : {}) already liked (account id {})", activityId,
					accountId);
		}
	}

	public void unlikeActivity(int accountId, int activityId) {
		String query = """
        	DELETE FROM activity_user_likes WHERE account_id = ? AND activity_id = ?;
        """;
		jdbcTemplate.update(query, accountId, activityId);
	}
}