package com.github.ricaps.tennis_club.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EntityNotExistsException extends ResponseStatusException {

	public EntityNotExistsException(String message) {
		super(HttpStatus.NOT_FOUND, message);
	}

}
