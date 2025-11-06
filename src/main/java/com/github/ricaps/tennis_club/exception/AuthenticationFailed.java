package com.github.ricaps.tennis_club.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationFailed extends RuntimeException {

	public AuthenticationFailed(String message) {
		super(message);
	}

}
