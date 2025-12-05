package com.roulette.resto.data.administration.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrendDto {
	private long total; // All time total
	private float percentageVariation;
	private Variation variationType;
}
