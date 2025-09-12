package com.roulette.resto.business.social.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {
	private String login;
	private String password;
	private String email;
	private String firstName;
	private String lastName;
}
