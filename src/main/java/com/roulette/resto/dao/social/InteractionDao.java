package com.roulette.resto.dao.social;

import com.roulette.resto.data.social.dto.out.SocialInteraction;
import com.roulette.resto.data.social.mapper.InteractionRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Slf4j
public class InteractionDao {
	final JdbcTemplate jdbcTemplate;
	public InteractionDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public List<SocialInteraction> getInteractionsByAccountId(int id) {
		String query = "select account.account_id, i.resto_id, r.display_name as resto_name, text from " +
				"account " +
				"JOIN resto_roulette.interaction i on account.account_id = i.account_id " +
				"JOIN resto_roulette.resto r on i.resto_id = r.resto_id " +
				"LEFT JOIN resto_roulette.comment c on i.comment_id = c.comment_id " +
				"WHERE account.account_id = ?";
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
}
