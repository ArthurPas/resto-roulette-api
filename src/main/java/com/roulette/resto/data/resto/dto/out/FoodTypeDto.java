package com.roulette.resto.data.resto.dto.out;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
@Data
@NoArgsConstructor
public class FoodTypeDto {
	Set<String> foodTypes;
	public FoodTypeDto(Set<String> foodTypes) {
		this.foodTypes = foodTypes;
	}
}
