package com.roulette.resto.business.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {
	@Schema(example = "fan2resto", requiredMode = REQUIRED)
	private String login;
	@Schema(example = "azerty123", requiredMode = REQUIRED)
	private String password;
	@Schema(example = "fan@resto.com", requiredMode = REQUIRED)
	private String email;
	@Schema(example = "fan", requiredMode = REQUIRED)
	private String firstName;
	@Schema(example = "d'resto", requiredMode = NOT_REQUIRED)
	private String lastName;
}
