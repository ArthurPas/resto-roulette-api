package com.roulette.resto.exception;

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
	private int errorCode;
	private HttpStatus status;

	public APIError(int message) {
		this.errorCode = message;
	}

}
