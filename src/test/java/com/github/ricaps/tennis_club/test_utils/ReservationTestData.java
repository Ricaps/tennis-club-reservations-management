package com.github.ricaps.tennis_club.test_utils;

import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.MoneyAmount;
import com.github.ricaps.tennis_club.peristence.entity.Reservation;
import com.github.ricaps.tennis_club.peristence.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

public class ReservationTestData {

	private ReservationTestData() {
		super();
	}

	public static Reservation entity(Court court, User user) {
		return Reservation.builder()
			.uid(UUID.randomUUID())
			.fromTime(LocalDateTime.now())
			.toTime(LocalDateTime.now())
			.isQuadGame(true)
			.totalPrice(new MoneyAmount(new BigDecimal("15.30"), Currency.getInstance("CZK")))
			.court(court)
			.user(user)
			.build();
	}

	// public static ReservationCreateDto createReservation(UUID surfaceUID) {
	// return new ReservationCreateDto("Name", surfaceUID);
	// }
	//
	// public static ReservationCreateDto createInvalid(UUID surfaceUID) {
	// return new ReservationCreateDto("", surfaceUID);
	// }
	//
	// public static ReservationViewDto viewReservation(UUID uuid) {
	// SurfaceViewDto surface = SurfaceTestData.createSurfaceView(UUIDUtils.generate());
	// return new ReservationViewDto(uuid, "Name", surface);
	// }
	//
	// public static void compareViewAndCreate(ReservationViewDto courtViewDto,
	// ReservationCreateDto createDto, Surface surface) {
	// assertThat(courtViewDto.uid()).isNotNull();
	// assertThat(courtViewDto.name()).isEqualTo(createDto.name());
	// assertThat(courtViewDto.surface().uid()).isEqualTo(createDto.surfaceUid());
	// SurfaceTestData.compareViewAndEntity(courtViewDto.surface(), surface);
	// }
	//
	// public static void compareViewAndEntity(ReservationViewDto courtViewDto,
	// Reservation entity, Surface surfaceEntity) {
	// assertThat(courtViewDto.uid()).isEqualTo(entity.getUid());
	// assertThat(courtViewDto.name()).isEqualTo(entity.getName());
	// SurfaceTestData.compareViewAndEntity(courtViewDto.surface(), surfaceEntity);
	// }

}
