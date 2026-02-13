package com.roulette.resto.data.social.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SocialInteraction {
	Integer accountId;
	int activityId;
	int restoId;
	String comment;
	Integer commentId;
}
