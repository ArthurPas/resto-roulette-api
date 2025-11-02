package com.roulette.resto.administration.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrendDto {
	private long total;
	private float percentageVariation;
	private Variation variationType;
}
