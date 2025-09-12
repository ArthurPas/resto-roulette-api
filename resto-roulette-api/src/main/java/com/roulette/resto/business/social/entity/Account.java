package com.roulette.resto.business.social.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
	private String accountId;
	private String login;
	private String password;
//	TODO: userinfo
}
