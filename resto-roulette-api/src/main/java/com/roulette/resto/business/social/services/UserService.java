package com.roulette.resto.business.social.services;

import com.roulette.resto.business.social.dto.UserInfoDto;
import com.roulette.resto.business.social.entity.UserInfo;
import com.roulette.resto.business.social.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;

@Service
@Slf4j
public class UserService {

	final AccountRepository accountRepository;

	public UserService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}


	public UserInfoDto getUserInfoByLogin(String login){
		UserInfoDto userInfoDto = new UserInfoDto();
		UserInfo userInfo = accountRepository.getUserInfoByLogin(login);
		userInfoDto.setBasicUserInfo(userInfo);
		return userInfoDto;
	}
}
