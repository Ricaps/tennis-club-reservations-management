package com.github.ricaps.tennis_club.api.shared;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Error schema common for all thrown errors")
public record ErrorDto(@Schema(description = "Error description") @NotNull String message,
		@Schema(description = "HTTP status code") int statusCode,
		@Schema(description = "List of field errors") List<FieldErrorDto> fieldErrors) {

}
