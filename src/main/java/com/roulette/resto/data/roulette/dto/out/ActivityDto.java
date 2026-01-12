package com.roulette.resto.data.roulette.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.roulette.resto.data.roulette.Activity;
import com.roulette.resto.data.social.entity.Account;
import com.roulette.resto.data.social.entity.Resto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityDto {
	public int activityId;
	public int accountId;
	public int restoId;
	public String description;

	public ActivityDto(Activity activity) {
		this.activityId = activity.getActivityId();
		this.accountId = activity.getAccount().getAccountId();
		this.restoId = activity.getResto().getId();
		this.description = activity.getDescription();
	}
}
