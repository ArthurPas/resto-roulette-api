package com.roulette.resto.social.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
	@Schema(example = "fan2resto")
	String login;
	@Schema(example = "fan@resto.com")
	String email;
	@Schema(example = "azerty123", requiredMode = REQUIRED)
	@NotBlank(message = "Email is mandatory")
	String password;

	public LoginDto(String login, String password) {
		this.login = login;
		this.password = password;
	}
}
