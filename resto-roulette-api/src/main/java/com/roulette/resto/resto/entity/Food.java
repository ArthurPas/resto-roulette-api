package com.roulette.resto.resto.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum Food {
	@Schema(example = "INDIAN")
	INDIAN(1),
	@Schema(example = "BURGER")
	BURGER(2),
	@Schema(example = "ASIAT")
	ASIAT(3),
	@Schema(example = "FRENCH")
	FRENCH(4);

	public final int foodId;

	Food(int foodId) {
		this.foodId = foodId;
	}

	public static Food fromValue(int value) {
		for (Food food : Food.values()) {
			if(food.foodId == value) {
				return food;
			}
		}
		throw new IllegalArgumentException("Unknown role value: " + value);
	}
	public static Food fromValue(String value) {
		for (Food food : Food.values()) {
			if (food.name().equalsIgnoreCase(value)) {
				return food;
			}
		}
		throw new IllegalArgumentException("Unknown role value: " + value);
	}
}
