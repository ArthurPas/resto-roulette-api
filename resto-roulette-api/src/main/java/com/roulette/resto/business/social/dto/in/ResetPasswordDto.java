package com.roulette.resto.business.social.dto.in;

import lombok.Data;

@Data
public class ResetPasswordDto {
	String verificationToken;
	String newPassword;
}
