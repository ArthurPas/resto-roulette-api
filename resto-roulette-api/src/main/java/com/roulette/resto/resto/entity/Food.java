package com.roulette.resto.resto.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum Food {
	@Schema(example = "INDIAN")
	INDIAN,
	@Schema(example = "BURGER")
	BURGER,
	@Schema(example = "ASIAT")
	ASIAT,
	@Schema(example = "FRENCH")
	FRENCH;
	public static Food fromValue(String value) {
		for (Food food : Food.values()) {
			if (food.name().equalsIgnoreCase(value)) {
				return food;
			}
		}
		throw new IllegalArgumentException("Unknown role value: " + value);
	}
}
