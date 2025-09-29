package com.roulette.resto.business.social.dto.out;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInteraction {
	@Schema(example = "1")
	Integer accountId;
	@Schema(example = "2")
	int restoId;
	@Schema(example = "pizza de la mama")
	String restoName;
	@Schema(example = "Je me suis ré-ga-lé")
	String comment;
	@Schema(example = "True")
	Boolean has_liked;
}
