package com.challenge.server.comoon.exception;

import org.springframework.beans.NotReadablePropertyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.challenge.server.comoon.response.ResponseHandler;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ExceptionHandler(value = CustomException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<?> handleCustomException(CustomException customException) {
		log.warn("[handleCustomException] : {} \n message: {}", customException.getErrorCode(),
			customException.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ResponseHandler.errorResponse(customException.getErrorCode(), customException.getMessage()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleMethodArgumentNotValid(HttpMessageNotReadableException exception) {
		log.warn("[HttpMessageNotReadableException] message: {}", exception.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ResponseHandler.errorResponse(ErrorCode.REQUEST_VALIDATION, exception.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> illegalArgumentException(IllegalArgumentException exception) {
		log.warn("[IllegalArgumentException] message: {}", exception.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ResponseHandler.errorResponse(ErrorCode.REQUEST_VALIDATION, exception.getMessage()));
	}

	@ExceptionHandler(NotReadablePropertyException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> notReadablePropertyException(NotReadablePropertyException exception) {
		log.warn("[NotReadablePropertyException] message: {}", exception.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ResponseHandler.errorResponse(ErrorCode.REQUEST_VALIDATION, exception.getMessage()));
	}

}
