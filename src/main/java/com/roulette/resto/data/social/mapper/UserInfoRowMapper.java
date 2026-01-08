package com.roulette.resto.data.social.mapper;

import com.roulette.resto.data.social.entity.UserInfo;
import com.roulette.resto.data.social.entity.UserRole;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class UserInfoRowMapper implements RowMapper<UserInfo> {
	@Override
	public UserInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserInfo userInfo = new UserInfo();
		userInfo.setEmail(rs.getString("email"));
		userInfo.setFirstName(rs.getString("first_name"));
		userInfo.setLastName(rs.getString("last_name"));
		userInfo.setEmailVerified(rs.getBoolean("email_verified"));
		userInfo.setLastLoginAt(rs.getTimestamp("last_login_at"));
		if(hasColumn(rs, "avatar")) {
			userInfo.setAvatar(rs.getString("avatar"));
		}
		return userInfo;
	}
	private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columns = rsmd.getColumnCount();
		for (int x = 1; x <= columns; x++) {
			if (columnName.equalsIgnoreCase(rsmd.getColumnName(x))) {
				return true;
			}
		}
		return false;
	}
}
