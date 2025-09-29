package com.roulette.resto.business.social.services;

import com.roulette.resto.business.social.dto.UserInfoDto;
import com.roulette.resto.business.social.dto.UserInteraction;
import com.roulette.resto.business.social.entity.Account;
import com.roulette.resto.business.social.entity.UserInfo;
import com.roulette.resto.business.social.repository.AccountRepository;
import com.roulette.resto.business.social.repository.InteractionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class UserService {

	final AccountRepository accountRepository;
	final InteractionRepository interactionRepository;
	private final AccountService accountService;

	public UserService(AccountRepository accountRepository, InteractionRepository interactionRepository, AccountService accountService) {
		this.accountRepository = accountRepository;
		this.interactionRepository = interactionRepository;
		this.accountService = accountService;
	}


	public UserInfoDto getUserInfoByLogin(String login){
		UserInfoDto userInfoDto = new UserInfoDto();
		UserInfo userInfo = accountRepository.getUserInfoByLogin(login);
		userInfoDto.setBasicUserInfo(userInfo);
		List<UserInteraction> interactions = interactionRepository.getInteractionsByAccountLogin(login);
		userInfoDto.setUserInteractions(interactions);
		return userInfoDto;
	}

	public UserInfoDto getUserInfoById(int id){
		UserInfoDto userInfoDto = new UserInfoDto();
		UserInfo userInfo = accountRepository.getUserInfoById(id);
		userInfoDto.setBasicUserInfo(userInfo);
		List<UserInteraction> interactions = interactionRepository.getInteractionsByAccountId(id);
		userInfoDto.setUserInteractions(interactions);
		return userInfoDto;
	}

	public UserInfo updateUserInfo(String userId, UserInfo newUserInfo) throws AccountNotFoundException, SQLException {
		try {
			int rowupdated = accountRepository.updateUserInfo(userId, newUserInfo);
			if (rowupdated == 0) {
				throw new SQLException("no rows updated");
			}
			return newUserInfo;
		}catch (SQLException e) {
			log.error(e.getMessage());
			throw new SQLException(e);
		}
	}
}
