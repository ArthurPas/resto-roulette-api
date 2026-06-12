package com.roulette.resto.data.social.dto;

import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.entity.FollowingStatus;
import com.roulette.resto.service.social.SocialService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MinimalAccountInfo {
	private int accountId;
	private String login;
	private String avatar = "";
	private String firstname;
	private String lastname;
	private FollowingStatus followingStatus;

	public MinimalAccountInfo(Account account) {
		this.setAvatar(account.getUserInfo().getAvatar());
		this.setLogin(account.getLogin());
		this.setAccountId(account.getAccountId());
		this.setFirstname(account.getUserInfo().getFirstName());
		this.setLastname(account.getUserInfo().getLastName());
	}

	public MinimalAccountInfo(Account account, SocialService.FollowersStatus followersStatus) {
		this.setAccountId(account.getAccountId());
		this.setLogin(account.getLogin());
		this.setAvatar(account.getUserInfo().getAvatar());
		this.setFirstname(account.getUserInfo().getFirstName());
		this.setLastname(account.getUserInfo().getLastName());
		this.setFollowingStatus(followersStatus.followingStatus());
	}

	@Override
	public boolean equals(Object object) {
		if(object == null || getClass() != object.getClass()) return false;
		MinimalAccountInfo that = (MinimalAccountInfo) object;
		return accountId == that.accountId && Objects.equals(login, that.login);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountId, login);
	}
}
