package com.roulette.resto.social.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
	UserRole role;
	@Schema(example = "fan@resto.com", requiredMode = REQUIRED)
	private String email;
	@Schema(example = "fan", requiredMode = REQUIRED)
	private String firstName;
	@Schema(example = "d'resto", requiredMode = NOT_REQUIRED)
	private String lastName;
	private boolean emailVerified;

}
