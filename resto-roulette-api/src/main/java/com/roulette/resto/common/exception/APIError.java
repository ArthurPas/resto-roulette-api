package com.roulette.resto.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIError {
	private String message;
	private String description;

	public APIError(String message) {
		this.message = message;
	}
}
