package com.roulette.resto.business.social.dto;

import com.roulette.resto.business.social.entity.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
	private String token;
	private Long expiresIn;
	private UserInfo userInfo;
}
