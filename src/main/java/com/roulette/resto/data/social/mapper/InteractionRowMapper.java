package com.roulette.resto.data.social.mapper;

import com.roulette.resto.data.social.dto.out.SocialInteraction;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class InteractionRowMapper implements RowMapper<SocialInteraction> {
	@Override
	public SocialInteraction mapRow(ResultSet rs, int rowNum) throws SQLException {
		SocialInteraction interaction = new SocialInteraction();
		interaction.setComment(rs.getString("text"));
		interaction.setHas_liked(rs.getBoolean("has_liked"));
		interaction.setRestoId(rs.getInt("resto_id"));
		interaction.setAccountId(rs.getInt("account_id"));
		interaction.setRestoName(rs.getString("resto_name"));
		return interaction;
	}
}
