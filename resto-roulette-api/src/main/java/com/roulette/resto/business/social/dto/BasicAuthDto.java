package com.roulette.resto.business.social.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicAuthDto {
	private String token;
	private Long expiresIn;

}
