package com.github.ricaps.tennis_club.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthenticationFailed extends ResponseStatusException {

	public AuthenticationFailed(String message) {
		super(HttpStatus.UNAUTHORIZED, message);
	}

}
