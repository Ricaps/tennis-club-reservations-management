package com.github.ricaps.tennis_club.api.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Dto for creation of Reservation entity")
public record ReservationCreateDto(
		@NotNull @Schema(description = "UUID of the court for which user wishes to create reservation") UUID courtUid,
		@NotNull @Schema(description = "Reservation start time, must be before end time, in future",
				example = "2025-11-07T14:30:00+01:00") @Future OffsetDateTime fromTime,
		@NotNull @Schema(description = "Reservation end time, must be after start time, in future",
				example = "2025-11-07T16:30:00+01:00") @Future OffsetDateTime toTime,
		@NotNull @Schema(
				description = "If the reservation is going to be quad game. If yes, price is multiplied.") Boolean isQuadGame

) {

}
