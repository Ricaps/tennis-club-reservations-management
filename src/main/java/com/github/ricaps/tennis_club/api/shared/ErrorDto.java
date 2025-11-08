package com.github.ricaps.tennis_club.api.shared;

import java.util.List;

public record ErrorDto(String message, int statusCode, List<FieldError> fieldErrors) {

	public record FieldError(String field, String message) {
	}
}
