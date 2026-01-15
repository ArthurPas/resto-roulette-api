package com.roulette.resto.data.social.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyTokenDto {
	private String email;
	private String verificationCode;
}
