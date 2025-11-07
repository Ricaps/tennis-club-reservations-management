package com.github.ricaps.tennis_club.api.reservation;

import com.github.ricaps.tennis_club.api.court.CourtViewDto;
import com.github.ricaps.tennis_club.api.shared.MoneyAmountDto;
import com.github.ricaps.tennis_club.api.user.UserBasicView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Dto for projection of Reservation entity")
public record ReservationViewDto(@NotNull @Schema(description = "UID of the reservation") UUID uid,
		@NotNull @Schema(description = "Reserved court", exampleClasses = CourtViewDto.class) CourtViewDto court,
		@NotNull @Schema(description = "Information about user who created reservation",
				exampleClasses = UserBasicView.class) UserBasicView user,
		@NotNull @Schema(description = "Start time of the reservation") OffsetDateTime fromTime,
		@NotNull @Schema(description = "End time of the reservation") OffsetDateTime toTime,
		@NotNull @Schema(description = "Time of reservation creation") OffsetDateTime createdAt,
		@NotNull @Schema(description = "If the reservation is going to be quad game.") Boolean isQuadGame,
		@NotNull @Schema(description = "Total price for the reservation") MoneyAmountDto totalPrice

) {

}
