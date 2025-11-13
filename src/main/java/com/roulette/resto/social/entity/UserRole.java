package com.roulette.resto.social.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public enum UserRole {
	@Schema(example = "ROLE_USER")
	ROLE_USER(1),
	@Schema(example = "ROLE_RESTAURANT_OWNER")
	ROLE_RESTAURANT_OWNER(2),
	@Schema(example = "ROLE_MODERATOR")
	ROLE_MODERATOR(3),
	@Schema(example = "ROLE_ADMIN")
	ROLE_ADMIN(4);

	public final int roleId;

	UserRole(int roleId) {
		this.roleId = roleId;
	}

	public static UserRole fromValue(int value) {
		for (UserRole role : UserRole.values()) {
			if(role.roleId == value) {
				return role;
			}
		}
		throw new IllegalArgumentException("Unknown role value: " + value);
	}
	public static UserRole fromValue(String value) {
		for (UserRole role : UserRole.values()) {
			if (role.name().equalsIgnoreCase(value)) {
				return role;
			}
		}
		throw new IllegalArgumentException("Unknown role value: " + value);
	}

}
