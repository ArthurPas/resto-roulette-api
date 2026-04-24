package com.roulette.resto.data.social.mapper;

import com.roulette.resto.data.social.dto.out.CommentInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommentInfoRowMapper implements RowMapper<CommentInfo> {
	@Override
	public CommentInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		CommentInfo commentInfo = new CommentInfo();
		commentInfo.commentId = rs.getString("comment_id");
		commentInfo.setActivityId(rs.getInt("activity_id"));
		commentInfo.commentText = rs.getString("comment_text");
		commentInfo.authorId = rs.getInt("author_id");
		return commentInfo;
	}
}
