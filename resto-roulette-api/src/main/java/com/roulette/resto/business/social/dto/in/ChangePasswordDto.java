package com.roulette.resto.business.social.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDto {
	@Schema(example = "azerty123", requiredMode = REQUIRED)
	private String oldPassword;
	@Schema(example = "azerty123", requiredMode = REQUIRED)
	private String newPassword;
	@Schema(example = "fan2resto", requiredMode = REQUIRED)
	String login;
}
