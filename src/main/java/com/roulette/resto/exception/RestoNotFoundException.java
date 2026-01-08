package com.roulette.resto.exception;

public class RestoNotFoundException extends RuntimeException {
	private final String message;
	public RestoNotFoundException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
