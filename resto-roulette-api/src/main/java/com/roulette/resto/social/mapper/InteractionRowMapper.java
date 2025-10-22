package com.roulette.resto.social.mapper;

import com.roulette.resto.social.dto.out.UserInteraction;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class InteractionRowMapper implements RowMapper<UserInteraction> {
	@Override
	public UserInteraction mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserInteraction interaction = new  UserInteraction();
		interaction.setComment(rs.getString("text"));
		interaction.setHas_liked(rs.getBoolean("has_liked"));
		interaction.setRestoId(rs.getInt("resto_id"));
		interaction.setAccountId(rs.getInt("account_id"));
		interaction.setRestoName(rs.getString("resto_name"));
		return interaction;
	}
}
