package com.roulette.resto.data.roulette;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.roulette.resto.data.social.dto.out.CommentInfo;
import com.roulette.resto.data.roulette.dto.out.RouletteSessionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityDto {
	public int activityId;
	public int accountId;
	public String description;
	public RouletteSessionDto details;
	public boolean isUploaded;
	public Date activityDate;
	public List<CommentInfo> comments;
}
