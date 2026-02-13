package com.roulette.resto.data.social.dto.out;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicAuthDto {

	private String login;
	@Schema(example = "eyAZea12588[...]")
	private String token;
	@Schema(example = "36000")
	private Long expiresIn;

}
