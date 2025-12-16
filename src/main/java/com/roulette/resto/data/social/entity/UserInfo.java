package com.roulette.resto.data.social.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfo {
	@Schema(example = "fan@resto.com", requiredMode = REQUIRED)
	private String email;
	@Schema(example = "fan", requiredMode = REQUIRED)
	private String firstName;
	@Schema(example = "d'resto", requiredMode = NOT_REQUIRED)
	private String lastName;
	private Timestamp lastLoginAt;
	UserRole role;
	private boolean emailVerified;
	private String avatar;
}
