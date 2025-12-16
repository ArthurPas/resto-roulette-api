package com.roulette.resto.data.administration.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.roulette.resto.data.social.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountsInfos {
	String login;
	String email;
	Timestamp lastLoginAt;
	Timestamp createdAt;
	String firstName;
	String lastName;
	Boolean verified;
	String role;
	String avatar;

	public AccountsInfos(Account account) {
		this.login = account.getLogin();
		this.createdAt = account.getCreatedAt();
		this.verified = account.getUserInfo().isEmailVerified();
		this.email = account.getUserInfo().getEmail();
		this.lastLoginAt = account.getUserInfo().getLastLoginAt();
		this.firstName = account.getUserInfo().getFirstName();
		this.lastName = account.getUserInfo().getLastName();
		this.role = account.getUserInfo().getRole().toString();
		this.avatar = account.getUserInfo().getAvatar();
	}
}
