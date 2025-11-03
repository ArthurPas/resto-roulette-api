package com.roulette.resto.common.exception;

public class RestoNotFoundException extends Throwable {
	private final String message;
	public RestoNotFoundException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
