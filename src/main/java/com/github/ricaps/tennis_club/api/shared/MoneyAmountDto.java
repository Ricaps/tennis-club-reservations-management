package com.github.ricaps.tennis_club.api.shared;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Currency;

@Schema(description = "Pair of amount and currency")
public record MoneyAmountDto(@NotNull @Schema(description = "Price per minute", example = "15.50") BigDecimal amount,
		@NotNull @Schema(description = "Currency of the price in ISO 4217 format", example = "CZK") Currency currency) {
}
