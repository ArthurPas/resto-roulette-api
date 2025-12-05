package com.roulette.resto.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
	int errorCode;
	HttpStatus status;
	public ErrorResponse(APIError error) {
		this.errorCode = error.getErrorCode();
		this.status = error.getStatus();
	}
}
