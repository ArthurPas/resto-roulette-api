package com.roulette.resto.service.social;

import com.roulette.resto.data.administration.dto.out.AccountsInfos;
import com.roulette.resto.data.common.entity.MediaResource;
import com.roulette.resto.data.common.entity.MediaType;
import com.roulette.resto.data.social.dto.in.ChangePasswordDto;
import com.roulette.resto.data.social.dto.in.ResetPasswordDto;
import com.roulette.resto.data.social.dto.in.UpdateAccountInfo;
import com.roulette.resto.data.social.dto.in.UpdateUserInfo;
import com.roulette.resto.data.social.dto.out.UserInfoDto;
import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.entity.MinimalAccountInfo;
import com.roulette.resto.data.social.entity.UserInfo;
import com.roulette.resto.exception.APIError;
import com.roulette.resto.repository.social.AccountRepository;
import com.roulette.resto.repository.social.InteractionRepository;
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

import static com.roulette.resto.service.common.MediaService.buildMediaUrl;

@Service
@Slf4j
public class UserService {

	final AccountRepository accountRepository;
	final InteractionRepository interactionRepository;
	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;

	public UserService(AccountRepository accountRepository, InteractionRepository interactionRepository,
					   AuthenticationManager authenticationManager,
					   PasswordEncoder passwordEncoder) {
		this.accountRepository = accountRepository;
		this.interactionRepository = interactionRepository;
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
	}



	public UserInfoDto getUserInfoById(int id)  {
		UserInfoDto userInfoDto = new UserInfoDto();
		Account account = null;
		try {
			account = accountRepository.getAccountById(id);
		} catch (AccountNotFoundException e) {
			throw new APIError(64, HttpStatus.NOT_FOUND);
		}
		userInfoDto.setUserInfo(account.getUserInfo());
		userInfoDto.setLogin(account.getLogin());

		List<MediaResource> mediaResources = accountRepository.getAccountMedias(id);
		log.info(mediaResources.toString()+"COUCOU");
		userInfoDto.setMedias(
				buildMediaUrl(mediaResources.stream()
						.filter(mediaResource -> !mediaResource.getMediaType().equals(MediaType.AVATAR)).toList()));
		return userInfoDto;

	}
	public UserInfoDto getUserInfoByLogin(String login) {
		try {
			Account account = accountRepository.getAccountByLogin(login);
			UserInfoDto userInfoDto = new UserInfoDto();
			userInfoDto.setUserInfo(account.getUserInfo());
			userInfoDto.setLogin(account.getLogin());
			List<MediaResource> mediaResources = accountRepository.getAccountMedias(account.getAccountId());
			userInfoDto.setMedias(buildMediaUrl(mediaResources));
			return userInfoDto;
		} catch (AccountNotFoundException e) {
			throw new APIError(64, HttpStatus.NOT_FOUND);
		}


	}

	public UserInfo updateUserPersonalInfo(int userId, UpdateUserInfo newUserInfo)  {
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
			return accountRepository.getAccountById(userId).getUserInfo();
		} catch (SQLException e) {
			log.error(e.getMessage());
			throw new APIError(500, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (AccountNotFoundException e) {
			throw new APIError(64, HttpStatus.NOT_FOUND);
		}
	}

	public Account updatePassword(ChangePasswordDto passwordDto, int accountId) throws AuthenticationException {
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
			throw new APIError(64, HttpStatus.NOT_FOUND);
		} catch (AccountNotFoundException e) {
			throw new RuntimeException(e);
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
	public int updateUserRole(UpdateAccountInfo updateAccountInfo) {
		try {

			accountRepository.updateAccountRole(updateAccountInfo);
			return accountRepository.getAccountByLogin(updateAccountInfo.getLogin()).getAccountId();
		}catch (AccountNotFoundException e) {
			log.error(e.getMessage());
			throw new APIError(64, HttpStatus.NOT_FOUND);
		}
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

	public void askForFollow(int accountAsker, String accountAsked) {
		try {
			accountRepository.askForFollow(accountAsker, accountAsked);
		}
		catch (AccountNotFoundException e) {
			throw new APIError(64, HttpStatus.NOT_FOUND);
		}
	}

	public void acceptFollow(int accountAskedId, String accountAskerId) {
		try {
			accountRepository.acceptFollow(accountAskerId, accountAskedId);
		}
		catch (AccountNotFoundException e) {
			throw new APIError(64, HttpStatus.NOT_FOUND);
		}
	}

	public void unfollow(int accountId, String accountToUnfollow) {
		try {
			accountRepository.unfollow(accountId, accountToUnfollow);
		}
		catch (AccountNotFoundException e) {
			throw new APIError(64, HttpStatus.NOT_FOUND);
		}
	}

	public List<MinimalAccountInfo> getFollowersByAccountId(int accountId) {
		return accountRepository.getFollowersByAccountId(accountId);
	}

	public List<MinimalAccountInfo> getFollowingRequest(int accountId) {
		return accountRepository.getFollowingRequestByAccount(accountId);


	}
}
