package com.roulette.resto.data.resto.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class LabelDto {
	Set<String> labels;
	public LabelDto(Set<String> labels) {
		this.labels = labels;
	}
}
