package com.roulette.resto.social.mapper;

import com.roulette.resto.social.entity.UserInfo;
import com.roulette.resto.social.entity.UserRole;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserInfoRowMapper implements RowMapper<UserInfo> {
	@Override
	public UserInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserInfo userInfo = new UserInfo();
		userInfo.setEmail(rs.getString("email"));
		userInfo.setFirstName(rs.getString("first_name"));
		userInfo.setLastName(rs.getString("last_name"));
		userInfo.setRole(UserRole.fromValue(rs.getInt("role")));
		userInfo.setEmailVerified(rs.getBoolean("email_verified"));
		userInfo.setLastLoginAt(rs.getTimestamp("last_login_at"));
		return userInfo;
	}
}
