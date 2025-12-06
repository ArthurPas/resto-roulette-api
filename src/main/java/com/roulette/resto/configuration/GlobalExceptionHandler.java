package com.roulette.resto.configuration;

import com.roulette.resto.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<?> handleMalformedPayloadInput(Exception ex, HttpServletRequest request){
		log.error("Exception caught in GlobalExceptionHandler calling");
		log.error(ex.getMessage());
		record Message(String message){};
		return new ResponseEntity<>(new Message("invalid request body, it might be an json typo"), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleGeneralException(Exception ex, HttpServletRequest request) {
		log.error("Exception caught in GlobalExceptionHandler", ex);
		log.error(ex.getMessage());
		log.error(request.getRequestURL().toString());
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}