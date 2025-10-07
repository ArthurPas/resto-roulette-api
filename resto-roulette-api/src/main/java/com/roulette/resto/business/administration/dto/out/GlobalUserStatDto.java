package com.roulette.resto.business.administration.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalUserStatDto {
	private long totalRegistered;
	private float percentageVariation;
	private VARIATION variationType;
}
