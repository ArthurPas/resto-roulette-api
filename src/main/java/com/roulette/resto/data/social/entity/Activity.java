package com.roulette.resto.data.social.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Activity {
	public int activityId;
	public Account account;
	public Resto resto;
	public String description;
	public String sessionId;

}
