package com.roulette.resto.data.social.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MinimalAccountInfo {
	private int accountId;
	private String login;
	private String avatar = "";
}
