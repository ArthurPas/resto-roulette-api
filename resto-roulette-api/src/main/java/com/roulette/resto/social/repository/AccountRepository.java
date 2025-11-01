package com.roulette.resto.social.repository;

import com.roulette.resto.social.dao.AccountDao;
import com.roulette.resto.social.dto.in.UpdateAccountInfo;
import com.roulette.resto.social.dto.in.UpdateUserInfo;
import com.roulette.resto.social.entity.Account;
import com.roulette.resto.social.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class AccountRepository {
	final JdbcTemplate jdbcTemplate;
	final AccountDao accountDao;

	public AccountRepository(JdbcTemplate jdbcTemplate, AccountDao accountDao) {
		this.jdbcTemplate = jdbcTemplate;
		this.accountDao = accountDao;
	}

	public int registerAccount(Account account) {
		return accountDao.registerAccount(account);
	}

	public int registerUserInfo(Account account) {
		return accountDao.registerUserInfo(account);
	}

	public Account getAccountByLogin(String login) throws AccountNotFoundException {
		return accountDao.getAccountByLogin(login);
	}

	public Account getAccountByEmail(String email) throws AccountNotFoundException {
		try {
			return accountDao.getAccountByEmail(email);
		}catch (DataAccessException e) {
			throw new AccountNotFoundException("Account not found");
		}
	}

	public UserInfo getUserInfoByLogin(String login) {
		return accountDao.getUserInfoByLogin(login);
	}

	public UserInfo getUserInfoById(int id) {
		return accountDao.getUserInfoById(id);
	}

	public Account getAccountById(int id) {
		return accountDao.getAccountById(id);
	}

	public int updateUserInfo(String id, UpdateUserInfo newUserInfo) throws SQLException {
		return accountDao.updateUserInfo(id, newUserInfo);
	}

	public void changePassword(int id, String newPassword) {
		accountDao.changePassword(id, newPassword);
	}

	public void updateMailVerificationStatus(int accountId, boolean isVerified) throws SQLException {
		accountDao.updateMailVerificationStatus(accountId, isVerified);
	}

	public void updateVerificationToken(String token, int accountId) {
		accountDao.updateVerificationToken(token, accountId);

	}

	public List<Account> getAll(int nbResult, int offset) {
		return accountDao.getall(nbResult,offset);
	}
	public void updateAccountRole(UpdateAccountInfo updateAccountInfo) throws AccountNotFoundException {
		int accountId = accountDao.getAccountByLogin(updateAccountInfo.getLogin()).getAccountId();;
		accountDao.updateAccountRole(updateAccountInfo.getNewRole().roleId, accountId);
	}
}
