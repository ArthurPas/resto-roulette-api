package com.roulette.resto.business.social.entity;

public enum UserRole {
	ROLE_USER(1),
	ROLE_RESTAURANT_OWNER(2),
	ROLE_MODERATOR(3),
	ROLE_ADMIN(4);

	public final int roleId;
	UserRole(int roleId) {
		this.roleId = roleId;
	}
	public int getRoleId() {
		return roleId;
	}
}
