package com.roulette.resto.data.roulette.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.roulette.resto.data.resto.dto.out.MinimalRestoInfo;
import com.roulette.resto.data.roulette.ActivityDto;
import com.roulette.resto.data.social.entity.MinimalAccountInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityResponse {
	String description;
	public MinimalRestoInfo restoInfo;
	public List<MinimalAccountInfo> participantInfos;
	public RouletteSessionDto details;

	public ActivityResponse(ActivityDto activityDto){
		this.description = activityDto.getDescription();
		this.details = activityDto.getDetails();
	}

}
