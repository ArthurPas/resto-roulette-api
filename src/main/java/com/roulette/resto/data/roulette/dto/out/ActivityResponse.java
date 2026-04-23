package com.roulette.resto.data.roulette.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.roulette.resto.data.resto.dto.out.MinimalRestoInfo;
import com.roulette.resto.data.roulette.ActivityDto;
import com.roulette.resto.data.social.entity.MinimalAccountInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityResponse {
	String description;
	@JsonProperty("isUploaded")
	boolean isUploaded;
	public MinimalRestoInfo restoInfo;
	public List<MinimalAccountInfo> participantInfos;
	public Date activityDate;

	public ActivityResponse(ActivityDto activityDto){
		this.description = activityDto.getDescription();
		this.participantInfos = activityDto.getDetails().getParticipantInfos();
		this.isUploaded = activityDto.isUploaded();
		this.activityDate = activityDto.getActivityDate();
	}

}
