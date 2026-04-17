package com.roulette.resto.configuration;

import com.roulette.resto.exception.APIError;
import com.roulette.resto.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<?> handleMalformedPayloadInput(Exception ex){
		log.error("Exception caught in GlobalExceptionHandler calling {}", ex.getClass());
		log.error(ex.getMessage());
		record Message(String message){};
		return new ResponseEntity<>(new Message(ex.getMessage()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(APIError.class)
	public ResponseEntity<?> handleAPIError(Exception ex){
		log.error("Exception caught in GlobalExceptionHandler{}", ex.getClass());
		log.error(ex.getMessage());

		ErrorResponse errorResponse = new ErrorResponse((APIError) ex);
		return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<?> handleACredsError(Exception ex){
		log.error("Exception caught in GlobalExceptionHandler {}", ex.getClass());
		log.error(ex.getMessage());
		ErrorResponse errorResponse = new ErrorResponse(70, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
	}
	@ExceptionHandler(IOException.class)
	public ResponseEntity<?> handleIOException(Exception ex){
		log.error("Exception caught in GlobalExceptionHandler {}", ex.getClass());
		log.error(ex.getMessage());
		ErrorResponse errorResponse = new ErrorResponse(95, HttpStatus.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
	}
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<?> handleMaxUploadSize(Exception ex) {
		log.error("Exception caught in GlobalExceptionHandler {}", ex.getClass());
		log.error(ex.getMessage());
		ErrorResponse errorResponse = new ErrorResponse(90, HttpStatus.PAYLOAD_TOO_LARGE);
		return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
	}
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleGeneralException(Exception ex) {
		log.error("Exception caught in GlobalExceptionHandler {}", ex.getClass());
		log.error(ex.getMessage());
		ex.printStackTrace();
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

}