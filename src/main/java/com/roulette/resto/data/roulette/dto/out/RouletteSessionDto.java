package com.roulette.resto.data.roulette.dto.out;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.roulette.resto.data.social.entity.MinimalAccountInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouletteSessionDto {
	@JsonIgnore
	public String sessionId;
	public int restoId;
	@JsonIgnore
	public Set<Integer> participantIds;
	public List<MinimalAccountInfo> participantInfos;
}
