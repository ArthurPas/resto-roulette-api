package com.roulette.resto.data.roulette.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouletteSessionDto {
	public String sessionId;
	public int restoId;
	public List<Integer> participantIds;
}
