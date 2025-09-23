package com.roulette.resto.business.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
	@Schema(example = "fan2resto", requiredMode = REQUIRED)
	String login;
	@Schema(example = "azerty123", requiredMode = REQUIRED)
	String password;

}
