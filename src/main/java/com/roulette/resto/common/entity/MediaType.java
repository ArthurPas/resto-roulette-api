package com.roulette.resto.common.entity;

import com.roulette.resto.social.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

public enum MediaType {
	@Schema(example = "LOGO")
	LOGO(1),
	@Schema(example = "MENU")
	MENU(2),
	@Schema(example = "RESTO")
	RESTO(3),
	@Schema(example = "AVATAR")
	AVATAR(4);

	public final int typeId;

	MediaType(int typeId) {
		this.typeId = typeId;
	}

	public static MediaType fromValue(int value) {
		for (MediaType type : MediaType.values()) {
			if(type.typeId == value) {
				return type;
			}
		}
		throw new IllegalArgumentException("Unknown type value: " + value);
	}
	public static MediaType fromValue(String value) {
		for (MediaType type : MediaType.values()) {
			if (type.name().equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Unknown type value: " + value);
	}

}
