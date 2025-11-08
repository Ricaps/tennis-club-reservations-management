package com.github.ricaps.tennis_club.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ValueIsMissingException extends ResponseStatusException {

	public ValueIsMissingException(String message) {
		super(HttpStatus.BAD_REQUEST, message);
	}

}
