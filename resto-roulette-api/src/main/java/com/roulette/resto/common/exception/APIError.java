package com.roulette.resto.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIError extends Throwable {
	private String message;
	private HttpStatus status;

	public APIError(String message) {
		this.message = message;
	}
}
