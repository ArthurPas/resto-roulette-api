package com.roulette.resto.data.social.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SocialInteraction {
	@Schema(example = "1")
	Integer accountId;
	@Schema(example = "2")
	Integer restoId;
	@Schema(example = "pizza de la mama")
	String restoName;
	@Schema(example = "Je me suis ré-ga-lé")
	String comment;
	Integer commentId;
	@Schema(example = "True")
	Boolean has_liked;
}
