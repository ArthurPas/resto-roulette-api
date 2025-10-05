package com.roulette.resto.business.social.dto.in;

import lombok.Data;

@Data
public class ResetPasswordDto {
	String email;
	String verificationToken;
	String newPassword;
}
