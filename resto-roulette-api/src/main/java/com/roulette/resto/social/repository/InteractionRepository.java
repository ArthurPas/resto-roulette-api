package com.roulette.resto.social.repository;

import com.roulette.resto.social.mapper.InteractionRowMapper;
import com.roulette.resto.social.dto.out.UserInteraction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class InteractionRepository {
	final JdbcTemplate jdbcTemplate;

	public InteractionRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<UserInteraction> getInteractionsByAccountLogin(String login) {
		String query ="select account.account_id, has_liked, i.resto_id, r.display_name as resto_name, text from " +
				"account " +
				"JOIN resto_roulette.interaction i on account.account_id = i.account_id " +
				"JOIN resto_roulette.resto r on i.resto_id = r.resto_id " +
				"LEFT JOIN resto_roulette.comment c on i.comment_id = c.comment_id " +
				"WHERE login = ?";
		try {
			return jdbcTemplate.query(query, new InteractionRowMapper(), login);
		}
		catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}

	}
	public List<UserInteraction> getInteractionsByAccountId(int id) {
		String query ="select account.account_id, has_liked, i.resto_id, r.display_name as resto_name, text from " +
				"account " +
				"JOIN resto_roulette.interaction i on account.account_id = i.account_id " +
				"JOIN resto_roulette.resto r on i.resto_id = r.resto_id " +
				"LEFT JOIN resto_roulette.comment c on i.comment_id = c.comment_id " +
				"WHERE account.account_id = ?";
		try {
			return jdbcTemplate.query(query, new InteractionRowMapper(), id);
		}
		catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}

	}
}
