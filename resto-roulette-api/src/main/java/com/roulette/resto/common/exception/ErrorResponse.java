package com.roulette.resto.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
	String message;
	public ErrorResponse(APIError error) {
		this.message = error.getMessage();
	}
}
