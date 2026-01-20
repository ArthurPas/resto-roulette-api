package com.roulette.resto.repository.social;

import com.roulette.resto.dao.social.AccountDao;
import com.roulette.resto.data.common.entity.MediaResource;
import com.roulette.resto.data.common.entity.MediaType;
import com.roulette.resto.data.social.dto.in.DeleteAccount;
import com.roulette.resto.data.social.dto.in.UpdateAccountInfo;
import com.roulette.resto.data.social.dto.in.UpdateUserInfo;
import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.entity.MinimalAccountInfo;
import com.roulette.resto.data.social.entity.UserInfo;
import com.roulette.resto.exception.APIError;
import com.roulette.resto.service.common.MediaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.imageio.ImageIO;
import javax.security.auth.login.AccountNotFoundException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class AccountRepository {
	final JdbcTemplate jdbcTemplate;
	final AccountDao accountDao;
	final MediaService mediaService;
	public AccountRepository(JdbcTemplate jdbcTemplate, AccountDao accountDao,MediaService mediaService) {
		this.jdbcTemplate = jdbcTemplate;
		this.accountDao = accountDao;
		this.mediaService = mediaService;
	}

	public int registerAccount(Account account) {
		return accountDao.registerAccount(account);
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

	public Account getAccountById(int id) throws AccountNotFoundException {
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
		return accountDao.getAll(nbResult,offset);
	}
	public void updateAccountRole(UpdateAccountInfo updateAccountInfo) throws AccountNotFoundException {
		int accountId = accountDao.getAccountByLogin(updateAccountInfo.getLogin()).getAccountId();
		accountDao.updateAccountRole(updateAccountInfo.getNewRole().roleId, accountId);
	}

	public void deleteAccount(DeleteAccount deleteAccount) throws AccountNotFoundException, APIError {
		int accountIdLogin = accountDao.getAccountByLogin(deleteAccount.getLogin()).getAccountId();
		int accountIdEmail = accountDao.getAccountByEmail(deleteAccount.getEmail()).getAccountId();
		if(accountIdLogin != accountIdEmail) {
			throw new APIError(604,
					HttpStatus.BAD_REQUEST);
		}else {
			accountDao.deleteAccount(accountIdEmail);
		}
	}
	public void recoverAccount(DeleteAccount deleteAccount) throws AccountNotFoundException, APIError {
		int accountIdLogin = accountDao.getAccountByLogin(deleteAccount.getLogin()).getAccountId();
		int accountIdEmail = accountDao.getAccountByEmail(deleteAccount.getEmail()).getAccountId();
		if(accountIdLogin != accountIdEmail) {
			throw new APIError(604,
					HttpStatus.BAD_REQUEST);
		}else {
			accountDao.recoverAccount(accountIdEmail);
		}
	}

	public void updateLoginDate(Account account) throws SQLException {
		accountDao.updateLoginDate(account.getAccountId());
	}

	public String saveAvatar(int accountId, MediaType mediaType, byte[] avatar) throws IOException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(avatar);
		try {
			BufferedImage newImage = ImageIO.read(byteArrayInputStream);
			String uuid = mediaService.saveImage(newImage, mediaType);
			accountDao.saveMedia(accountId, uuid, mediaType);
			return uuid;
		} catch (IOException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public List<MediaResource> getAccountMedias(int id) {
		return accountDao.getAccountPictures(id);
	}

	public String updateAvatar(int accountId, byte[] bytes) throws IOException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		try {
			BufferedImage newImage = ImageIO.read(byteArrayInputStream);
			String uuid = mediaService.saveImage(newImage, MediaType.AVATAR);
			accountDao.updateAvatar(accountId, uuid);
			return uuid;
		} catch (IOException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public void askForFollow(int accountAsker, String accountAsked) throws AccountNotFoundException {

		accountDao.newFollowRequest(accountAsker,Integer.parseInt(accountAsked));
	}

	public void acceptFollow(String accountAskerId, int accountAskedId) throws AccountNotFoundException {
		accountDao.acceptFollowRequest(Integer.parseInt(accountAskerId),accountAskedId);
	}

	public void unfollow(int accountId, String accountToUnfollow) throws AccountNotFoundException {
		accountDao.unfollow(Integer.parseInt(accountToUnfollow),accountId);
	}

	public List<MinimalAccountInfo> getFollowersByAccountId(int accountId) {
		return accountDao.getFollowersByAccountId(accountId);
	}

	public List<MinimalAccountInfo> getFollowingRequestByAccount(int accountId) {
		return accountDao.getFollowingRequest(accountId);
	}
}
