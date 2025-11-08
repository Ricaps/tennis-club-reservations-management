package com.github.ricaps.tennis_club.exception;

import com.github.ricaps.tennis_club.api.shared.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorDto> handleValidationException(MethodArgumentNotValidException ex) {
		final List<ErrorDto.FieldError> fieldErrors = ex.getFieldErrors()
			.stream()
			.map(fieldError -> new ErrorDto.FieldError(fieldError.getField(), fieldError.getDefaultMessage()))
			.toList();

		final ErrorDto error = new ErrorDto(ex.getBody().getDetail(), ex.getStatusCode().value(), fieldErrors);
		return ResponseEntity.status(ex.getStatusCode()).body(error);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErrorDto> handleResponseStatusException(ResponseStatusException ex) {
		final ErrorDto error = new ErrorDto(ex.getBody().getDetail(), ex.getStatusCode().value(), List.of());
		return ResponseEntity.status(ex.getStatusCode()).body(error);
	}


	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDto> handleOtherExceptions(Exception ex) {
		final ErrorDto error = new ErrorDto(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), List.of());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}
