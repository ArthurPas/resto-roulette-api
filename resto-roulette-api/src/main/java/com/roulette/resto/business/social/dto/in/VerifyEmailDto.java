package com.roulette.resto.business.social.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailDto {
	private String accountId;
	private String verificationCode;
}
