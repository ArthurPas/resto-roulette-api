package com.roulette.resto.business.social.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInteraction {
	Integer accountId;
	int restoId;
	String restoName;
	String comment;
	Boolean has_liked;
}
