package com.roulette.resto.business.social.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDto {
	private String oldPassword;
	private String newPassword;
	private String login;
}
