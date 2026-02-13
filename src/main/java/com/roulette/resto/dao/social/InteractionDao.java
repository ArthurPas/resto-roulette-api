package com.roulette.resto.dao.social;

import com.roulette.resto.configuration.JwtService;
import com.roulette.resto.dao.roulette.ActivityDao;
import com.roulette.resto.data.social.dto.in.NewComment;
import com.roulette.resto.data.social.dto.SocialInteraction;
import com.roulette.resto.data.social.mapper.InteractionRowMapper;
import com.roulette.resto.exception.APIError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class InteractionDao {
	final JdbcTemplate jdbcTemplate;
	final JwtService jwtService;
	private final ActivityDao activityDao;

	public InteractionDao(JdbcTemplate jdbcTemplate, JwtService jwtService, ActivityDao activityDao) {
		this.jdbcTemplate = jdbcTemplate;
		this.jwtService = jwtService;
		this.activityDao = activityDao;
	}


	public List<SocialInteraction> getInteractionsByAccountId(int id) {
		String query = """
							select account.account_id, content, c.comment_id, a.activity_id, a.resto_id
							FROM
								account
							JOIN resto_roulette.comment c on account.account_id = c.account_id
							JOIN activity a on c.activity_id = a.activity_id
							WHERE
							    account.account_id = ?
						""";
		try {
			return jdbcTemplate.query(query, new InteractionRowMapper(), id);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public boolean setLikeToResto(int restoId, int accountId) {
		String query = "INSERT INTO user_like (account_id, resto_id) VALUES (?, ?)";
		try {
			return jdbcTemplate.update(query,accountId,restoId)==1;
		}catch (DuplicateKeyException ex){
			log.warn(ex.getMessage());
			log.warn("User {} tried to like {} which was already liked ",accountId, restoId);
			throw ex;
		}
	}
	public boolean isRestoLikedByUser(int restoId, int accountId) {
		String query = "SELECT COUNT(*) FROM user_like WHERE resto_id = ? AND account_id = ?";
		return jdbcTemplate.queryForObject(query, Integer.class, restoId, accountId) == 1;
	}
	public boolean removeLikeToResto(int restoId, int accountId) {
		if(isRestoLikedByUser(restoId, accountId)){
			String query = "DELETE FROM user_like WHERE resto_id = ? AND account_id = ?";
			return jdbcTemplate.update(query,restoId,accountId)!=1;
		}else {
			log.warn("User {} tried to dislike resto {} which was not already liked", accountId, restoId);
			return false;
		}
	}

	public int newCommentByUserToResto(int activity_id, int accountId, NewComment comment) {
		int commentId = insertComment(accountId,activity_id, comment);
		return commentId;

	}

	private int insertComment(int accountId, int activity_id, NewComment comment) {
		String insertCommentQuery = "INSERT INTO comment (content,account_id, activity_id) VALUES (?, ?, ?)";
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(conn -> {
			PreparedStatement preparedStatement = conn.prepareStatement(insertCommentQuery, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, comment.getComment());
			preparedStatement.setInt(2, accountId);
			preparedStatement.setInt(3, activity_id);
			log.debug(preparedStatement.toString());
			return preparedStatement;
		}, generatedKeyHolder);
		return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
	}
	

	public String getCommentById(int commentId) {
		String query = "SELECT content FROM comment WHERE comment_id = ?";
		try {
			return jdbcTemplate.queryForObject(query, String.class, commentId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public List<Integer> getLikedRestos(int accountId) {
		String query = "SELECT DISTINCT resto_id FROM user_like WHERE account_id = ?";
		try {
			return jdbcTemplate.queryForList(query, Integer.class, accountId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public int editComment(int id, String comment) {
		String query = "UPDATE comment SET content = ? WHERE comment_id = ?";
		int rows = jdbcTemplate.update(conn -> {
			PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, comment);
			preparedStatement.setInt(2, id);
			log.debug(preparedStatement.toString());
			return preparedStatement;
		});
		if (rows != 1) {
			throw new APIError(14, HttpStatus.NOT_FOUND);
		}
		return id;
	}
	public boolean deleteComment(int id) {
		String query = "DELETE FROM comment WHERE comment_id = ?";
		int rows = jdbcTemplate.update(conn -> {
			PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setInt(1, id);
			log.debug(preparedStatement.toString());
			return preparedStatement;
		});
		return rows == 1;
	}

}
