package com.roulette.resto.social.entity;

import lombok.Getter;

@Getter
public enum UserRole {
	ROLE_USER(1),
	ROLE_RESTAURANT_OWNER(2),
	ROLE_MODERATOR(3),
	ROLE_ADMIN(4);

	public final int roleId;

	UserRole(int roleId) {
		this.roleId = roleId;
	}

	public static UserRole fromValue(int value) {
		for (UserRole role : UserRole.values()) {
			if (role.roleId == value) {
				return role;
			}
		}
		throw new IllegalArgumentException("Unknown role value: " + value);
	}

}
