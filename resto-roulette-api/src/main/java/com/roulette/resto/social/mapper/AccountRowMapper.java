package com.roulette.resto.social.mapper;

import com.roulette.resto.social.entity.Account;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRowMapper implements RowMapper<Account> {

	@Override
	public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
		Account account = new Account();
		account.setAccountId(rs.getInt("account_id"));
		account.setLogin(rs.getString("login"));
		account.setPassword(rs.getString("password"));
		account.setVerificationToken(rs.getString("verification_token"));
		return account;
	}
}
