package com.roulette.resto.data.resto.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.roulette.resto.data.resto.entity.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MinimalRestoInfo {
	int restoId;
	String restoName;
	String logoUrl;
}
