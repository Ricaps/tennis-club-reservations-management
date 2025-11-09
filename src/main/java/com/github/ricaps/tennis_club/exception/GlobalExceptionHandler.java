package com.github.ricaps.tennis_club.exception;

import com.github.ricaps.tennis_club.api.shared.ErrorDto;
import com.github.ricaps.tennis_club.api.shared.FieldErrorDto;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorDto> handleValidationException(MethodArgumentNotValidException ex) {
		final List<FieldErrorDto> fieldErrors = ex.getFieldErrors()
			.stream()
			.map(fieldError -> new FieldErrorDto(fieldError.getField(), fieldError.getDefaultMessage()))
			.toList();

		final ErrorDto error = new ErrorDto(ex.getBody().getDetail(), ex.getStatusCode().value(), fieldErrors);
		log.error("An validation error occurred while running request", ex);
		return ResponseEntity.status(ex.getStatusCode()).body(error);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErrorDto> handleResponseStatusException(ResponseStatusException ex) {
		final ErrorDto error = new ErrorDto(ex.getBody().getDetail(), ex.getStatusCode().value(), List.of());

		log.error("An application error occurred while running request", ex);
		return ResponseEntity.status(ex.getStatusCode()).body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDto> handleOtherExceptions(Exception ex) {
		final ErrorDto error = new ErrorDto(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), List.of());

		log.error("An error occurred while running request", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

	@ExceptionHandler(MissingRequestHeaderException.class)
	public ResponseEntity<ErrorDto> handleMissingErrorHeader(MissingRequestHeaderException ex) {
		final ErrorDto error = new ErrorDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), List.of());

		log.error("An missing header error occurred while running request", ex);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ErrorDto> handleValidationException(ValidationException ex) {
		final ErrorDto error = new ErrorDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), List.of());

		log.error("An validation exception occurred during running request", ex);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

}
