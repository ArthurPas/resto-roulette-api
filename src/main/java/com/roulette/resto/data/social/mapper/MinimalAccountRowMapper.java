package com.roulette.resto.data.social.mapper;

import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.entity.MinimalAccountInfo;
import com.roulette.resto.data.social.entity.UserInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MinimalAccountRowMapper implements RowMapper<MinimalAccountInfo> {

	@Override
	public MinimalAccountInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		MinimalAccountInfo account = new MinimalAccountInfo();
		account.setLogin(rs.getString("login"));
		account.setAvatar(rs.getString("avatar"));
		account.setAccountId(rs.getInt("account_id"));
//		account.setFirstname(rs.getString("first_name"));
		account.setLastname(rs.getString("last_name"));
		return account;
	}
}
