package com.roulette.resto.social.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
public class SendEmailDto {
	@Schema(example = "arthur.pascal@epitech.eu", requiredMode = REQUIRED)
	String email;
}
