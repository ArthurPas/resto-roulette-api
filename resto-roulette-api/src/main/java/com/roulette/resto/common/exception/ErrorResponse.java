package com.roulette.resto.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
	String message;
	HttpStatus status;
	public ErrorResponse(APIError error) {
		this.message = error.getMessage();
		this.status = error.getStatus();
	}
}
