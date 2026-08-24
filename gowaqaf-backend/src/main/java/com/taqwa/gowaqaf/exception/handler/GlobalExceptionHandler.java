package com.taqwa.gowaqaf.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.exception.custom.BaseException;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.exception.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {

		return buildResponse(ex, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(BadRequestException ex) {

		return buildResponse(ex, HttpStatus.BAD_REQUEST);
	}

	private ResponseEntity<ErrorResponse> buildResponse(BaseException ex, HttpStatus status) {
		ErrorResponse response = new ErrorResponse(ex.getErrorCode().name(), ex.getMessage(), status.value());

		return ResponseEntity.status(status).body(response);
	}

}
