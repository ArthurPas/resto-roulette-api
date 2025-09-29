package com.roulette.resto.business.social.services;

import com.roulette.resto.business.social.dto.ChangePasswordDto;
import com.roulette.resto.business.social.dto.UserInfoDto;
import com.roulette.resto.business.social.dto.UserInteraction;
import com.roulette.resto.business.social.entity.Account;
import com.roulette.resto.business.social.entity.UserInfo;
import com.roulette.resto.business.social.repository.AccountRepository;
import com.roulette.resto.business.social.repository.InteractionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;
import java.util.List;

import static com.roulette.resto.common.configuration.SecurityConfig.passwordEncoder;

@Service
@Slf4j
public class UserService {

	final AccountRepository accountRepository;
	final InteractionRepository interactionRepository;
	private final AccountService accountService;
	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;

	public UserService(AccountRepository accountRepository, InteractionRepository interactionRepository, AccountService accountService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
		this.accountRepository = accountRepository;
		this.interactionRepository = interactionRepository;
		this.accountService = accountService;
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
	}


	public UserInfoDto getUserInfoByLogin(String login){
		UserInfoDto userInfoDto = new UserInfoDto();
		UserInfo userInfo = accountRepository.getUserInfoByLogin(login);
		userInfoDto.setBasicUserInfo(userInfo);
		List<UserInteraction> interactions = interactionRepository.getInteractionsByAccountLogin(login);
		userInfoDto.setUserInteractions(interactions);
		return userInfoDto;
	}

	public UserInfoDto getUserInfoById(int id) throws AccountNotFoundException {
		try {
			accountService.existsById(id);
		}catch (AccountNotFoundException e){
			log.error("Account not found");
			throw e;
		}
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

	public Account updatePassword(ChangePasswordDto passwordDto) throws AuthenticationException,
			AccountNotFoundException {
		try {
			Account account = accountRepository.getAccountByLogin(passwordDto.getLogin());
			UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(passwordDto.getLogin(),
					passwordDto.getOldPassword());
			authenticationManager.authenticate(authReq);
		 	String encodedNewPassword = passwordEncoder.encode(passwordDto.getNewPassword());
		 	accountRepository.changePassword(account.getAccountId(), encodedNewPassword);
			return account;
		}catch (AuthenticationException e) {
			log.error("Authentication exception {}", e.getMessage());
			throw e;
		}
		catch (AccountNotFoundException e){
			log.error("account not found");
			throw e;
		}
	}
}
