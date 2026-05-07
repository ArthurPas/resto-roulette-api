package com.roulette.resto.data.resto.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;
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
	double lat;
	double lon;
}
