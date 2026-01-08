package com.roulette.resto.data.social.mapper;

import com.roulette.resto.data.social.dto.out.SocialInteraction;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class InteractionRowMapper implements RowMapper<SocialInteraction> {
	@Override
	public SocialInteraction mapRow(ResultSet rs, int rowNum) throws SQLException {
		SocialInteraction interaction = new SocialInteraction();
		interaction.setComment(rs.getString("content"));
		interaction.setActivityId(rs.getInt("activity_id"));
		interaction.setAccountId(rs.getInt("account_id"));
		interaction.setCommentId(rs.getInt("comment_id"));
		return interaction;
	}
}
