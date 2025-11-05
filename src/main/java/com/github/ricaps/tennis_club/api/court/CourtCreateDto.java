package com.github.ricaps.tennis_club.api.court;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Dto for creation of Court entity")
public record CourtCreateDto(
		@Schema(description = "Name of the court", example = "Philippe-Chatrie") @Size(min = 1,
				max = 255) @NotNull String name,
		@NotNull @Schema(description = "Reference uid to the Surface entity",
				example = "a25b42b8-ee95-4222-853b-45a70934fc55") UUID surfaceUid) {
}
