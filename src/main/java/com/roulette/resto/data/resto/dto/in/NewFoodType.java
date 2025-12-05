package com.roulette.resto.data.resto.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewFoodType {
	@Schema(example = "PIZZA", requiredMode = REQUIRED)
	String foodType;
}
