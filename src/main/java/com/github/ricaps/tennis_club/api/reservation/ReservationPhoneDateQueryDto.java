package com.github.ricaps.tennis_club.api.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.OffsetDateTime;

public record ReservationPhoneDateQueryDto(
		@PathVariable @NotNull @Schema(description = "Phone number bound to reservation's user") String phoneNumber,
		@RequestParam @NotNull @Schema(
				description = "Ability to filter reservations by date and time. Shows reservations with datetime greater than defined.") OffsetDateTime fromTime,
		@ParameterObject @PageableDefault(sort = "createdAt") Pageable pageable) {
}
