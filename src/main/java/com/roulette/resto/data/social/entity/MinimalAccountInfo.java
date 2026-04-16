package com.roulette.resto.data.social.entity;

import com.roulette.resto.controller.social.socialInteraction.SocialController;
import com.roulette.resto.data.social.dto.out.UserInfoDto;
import jakarta.validation.constraints.Min;
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
	private boolean following;
	private boolean followed;
	private String firstname;
	private String lastname;

	public MinimalAccountInfo(Account account) {
		this.setAvatar(account.getUserInfo().getAvatar());
		this.setLogin(account.getLogin());
		this.setAccountId(account.getAccountId());
		this.setFirstname(account.getUserInfo().getFirstName());
		this.setLastname(account.getUserInfo().getLastName());
	}

	public MinimalAccountInfo(Account account, SocialController.FollowersStatus followersStatus) {
		this.setAccountId(account.getAccountId());
		this.setLogin(account.getLogin());
		this.setAvatar(account.getUserInfo().getAvatar());
		this.setFollowing(followersStatus.isFollower());
		this.setFollowed(followersStatus.isFollowed());
		this.setFirstname(account.getUserInfo().getFirstName());
		this.setLastname(account.getUserInfo().getLastName());
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
