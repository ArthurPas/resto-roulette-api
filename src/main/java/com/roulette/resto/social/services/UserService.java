package com.roulette.resto.social.services;

import com.roulette.resto.administration.dto.out.AccountsInfos;
import com.roulette.resto.common.entity.MediaResource;
import com.roulette.resto.common.entity.MediaType;
import com.roulette.resto.common.exception.APIError;
import com.roulette.resto.social.dao.AccountDao;
import com.roulette.resto.social.dto.in.ChangePasswordDto;
import com.roulette.resto.social.dto.in.ResetPasswordDto;
import com.roulette.resto.social.dto.in.UpdateAccountInfo;
import com.roulette.resto.social.dto.in.UpdateUserInfo;
import com.roulette.resto.social.dto.out.UserInfoDto;
import com.roulette.resto.social.dto.out.UserInteraction;
import com.roulette.resto.social.entity.Account;
import com.roulette.resto.social.entity.UserInfo;
import com.roulette.resto.social.repository.AccountRepository;
import com.roulette.resto.social.repository.InteractionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.login.AccountNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

	final AccountRepository accountRepository;
	final InteractionRepository interactionRepository;
	private final AccountService accountService;
	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;

	public UserService(AccountRepository accountRepository, InteractionRepository interactionRepository,
					   AccountService accountService, AuthenticationManager authenticationManager,
					   PasswordEncoder passwordEncoder,AccountDao accountDao) {
		this.accountRepository = accountRepository;
		this.interactionRepository = interactionRepository;
		this.accountService = accountService;
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
	}


	public UserInfoDto getUserInfoByLogin(String login) {
		UserInfoDto userInfoDto = new UserInfoDto();
		UserInfo userInfo = accountRepository.getUserInfoByLogin(login);
		userInfoDto.setUserInfo(userInfo);
		List<UserInteraction> interactions = interactionRepository.getInteractionsByAccountLogin(login);
		userInfoDto.setUserInteractions(interactions);
		return userInfoDto;
	}

	public UserInfoDto getUserInfoById(int id) throws AccountNotFoundException {
		if(!accountService.existsById(id)) {
			throw new AccountNotFoundException();
		}
		UserInfoDto userInfoDto = new UserInfoDto();
		Account account = accountRepository.getAccountById(id);
		userInfoDto.setUserInfo(account.getUserInfo());
		List<UserInteraction> interactions = interactionRepository.getInteractionsByAccountId(id);
		userInfoDto.setUserInteractions(interactions);
		userInfoDto.setLogin(account.getLogin());
		List<MediaResource> mediaResources = accountRepository.getAccountMedias(id);
		userInfoDto.setMedias(mediaResources);
		return userInfoDto;
	}

	public UserInfo updateUserPersonalInfo(int userId, UpdateUserInfo newUserInfo) throws AccountNotFoundException,
			SQLException {
		try {
			UserInfo olduserInfo = accountRepository.getUserInfoById(userId);
			//If email has changed
			if(!olduserInfo.getEmail().equals(newUserInfo.getEmail())) {
				accountRepository.updateMailVerificationStatus(userId, false);
			}
			int updatedRows = accountRepository.updateUserInfo(String.valueOf(userId), newUserInfo);
			if(updatedRows == 0) {
				throw new SQLException("no rows updated");
			}
			return accountRepository.getUserInfoById(userId);
		} catch (SQLException e) {
			log.error(e.getMessage());
			throw new SQLException(e);
		}
	}

	public Account updatePassword(ChangePasswordDto passwordDto, int accountId) throws AuthenticationException,
			AccountNotFoundException {
		try {
			Account account = accountRepository.getAccountById(accountId);
			UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(account.getLogin(),
					passwordDto.getOldPassword());
			authenticationManager.authenticate(authReq);
			String encodedNewPassword = passwordEncoder.encode(passwordDto.getNewPassword());
			accountRepository.changePassword(account.getAccountId(), encodedNewPassword);
			return account;
		} catch (AuthenticationException e) {
			log.error("Authentication exception {}", e.getMessage());
			throw e;
		} catch (DataAccessException e) {
			log.error("account not found");
			throw new AccountNotFoundException(e.getMessage());
		}
	}


	public void resetPassword(ResetPasswordDto resetPasswordDto) throws APIError {
		try {
			Account account = accountRepository.getAccountByEmail(resetPasswordDto.getEmail());
			if(!(Objects.equals(account.getVerificationToken(), resetPasswordDto.getVerificationToken()))) {
				throw new SecurityException("security code doesnt match");
			}
			String encodedNewPassword = passwordEncoder.encode(resetPasswordDto.getNewPassword());
			accountRepository.changePassword(account.getAccountId(), encodedNewPassword);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new APIError(500, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (AccountNotFoundException e) {
			throw new APIError(64, HttpStatus.NOT_FOUND);
		}
	}

	public List<AccountsInfos> getAccounts(int offset) {
		int nbResult = 25;
		return accountRepository.getAll(nbResult, offset*nbResult)
								.stream()
								.map(AccountsInfos::new)
								.collect(Collectors.toList());
	}
	public int updateUserRole(UpdateAccountInfo updateAccountInfo) throws AccountNotFoundException {
		accountRepository.updateAccountRole(updateAccountInfo);
		return accountRepository.getAccountByLogin(updateAccountInfo.getLogin()).getAccountId();
	}

	public void updateLoginDate(Account account) {
		try {

			accountRepository.updateLoginDate(account);
		}catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public MediaResource addUserAvatar(int accountId, MultipartFile avatar) throws APIError {

		try{
			if(hasUserAvatar(accountId)) {
				return updateUserAvatar(accountId, avatar);
			}
			else {
			String resourceId = accountRepository.saveAvatar(accountId, MediaType.AVATAR, avatar.getBytes());
			return new MediaResource(resourceId, MediaType.AVATAR);
			}
		}catch (IOException e){
			log.error(e.getMessage());
			throw new APIError(95, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public MediaResource updateUserAvatar(int accountId, MultipartFile avatar) throws APIError, IOException {
		try {
			String resourceId = accountRepository.updateAvatar(accountId, avatar.getBytes());
			return new MediaResource(resourceId, MediaType.AVATAR);
		}catch (IOException e) {
			log.error(e.getMessage());
			throw new APIError(95, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	private boolean hasUserAvatar(int accountId) {
		List<MediaResource> mediaResources = accountRepository.getAccountMedias(accountId);
		for(MediaResource mediaResource : mediaResources) {
			if(mediaResource.getMediaType().equals(MediaType.AVATAR)) {
				return true;
			}
		}
		return false;
	}
}
