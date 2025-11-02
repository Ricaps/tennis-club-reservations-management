package com.github.ricaps.tennis_club.api.surface;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Currency;

@Schema(description = "Dto for creating Surface entity")
public record SurfaceCreateDto(
		@NotNull @Size(min = 1, max = 255) @Schema(description = "Name of the surface", example = "Grass") String name,
		@NotNull @Schema(description = "Price per minute", example = "15.50") BigDecimal price,
		@NotNull @Schema(description = "Currency of the price in ISO 4217 format", example = "CZK") Currency currency) {

}
