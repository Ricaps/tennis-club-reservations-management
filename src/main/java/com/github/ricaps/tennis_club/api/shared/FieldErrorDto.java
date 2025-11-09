package com.github.ricaps.tennis_club.api.shared;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Field error in case of validation error")
public record FieldErrorDto(@Schema(description = "Field name", example = "name") String field,
		@Schema(description = "Error message bound to the specific field",
				example = "size must be between 1 and 255") String message) {
}
