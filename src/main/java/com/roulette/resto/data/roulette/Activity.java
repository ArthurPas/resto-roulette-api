package com.roulette.resto.data.roulette;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.entity.Resto;
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
