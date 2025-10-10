package com.roulette.resto.business.social.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDto {
	String email;
	String verificationToken;
	String newPassword;
}
