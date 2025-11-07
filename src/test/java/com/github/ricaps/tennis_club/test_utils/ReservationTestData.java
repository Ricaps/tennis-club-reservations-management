package com.github.ricaps.tennis_club.test_utils;

import com.github.ricaps.tennis_club.api.court.CourtViewDto;
import com.github.ricaps.tennis_club.api.reservation.ReservationCreateDto;
import com.github.ricaps.tennis_club.api.reservation.ReservationViewDto;
import com.github.ricaps.tennis_club.api.shared.MoneyAmountDto;
import com.github.ricaps.tennis_club.api.user.UserBasicView;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.MoneyAmount;
import com.github.ricaps.tennis_club.peristence.entity.Reservation;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.utils.UUIDUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationTestData {

	private ReservationTestData() {
		super();
	}

	public static Reservation entity(Court court, User user) {
		OffsetDateTime startTime = Instant.parse("2025-01-01T14:00:00Z").atOffset(ZoneOffset.UTC);
		return Reservation.builder()
			.uid(UUID.randomUUID())
			.fromTime(startTime)
			.toTime(startTime.plusHours(2))
			.isQuadGame(true)
			.totalPrice(new MoneyAmount(new BigDecimal("15.30"), Currency.getInstance("CZK")))
			.court(court)
			.user(user)
			.build();
	}

	public static ReservationCreateDto createReservation(UUID courtUID) {
		OffsetDateTime startTime = Instant.parse("2025-01-01T14:00:00Z").atOffset(ZoneOffset.UTC);

		return new ReservationCreateDto(courtUID, startTime, startTime.plusHours(1), false);
	}

	public static ReservationCreateDto createInvalid(UUID surfaceUID) {
		OffsetDateTime startTime = Instant.parse("2025-01-01T14:00:00Z").atOffset(ZoneOffset.UTC);

		return new ReservationCreateDto(surfaceUID, startTime, startTime.plusHours(1), false);
	}

	public static ReservationViewDto viewReservation(UUID uuid) {
		CourtViewDto court = CourtTestData.viewCourt(UUIDUtils.generate());
		UserBasicView userBasicView = UserTestData.viewBasicUser(UUIDUtils.generate());
		OffsetDateTime startTime = Instant.parse("2025-01-01T14:00:00Z").atOffset(ZoneOffset.UTC);
		MoneyAmountDto totalPrice = new MoneyAmountDto(new BigDecimal("10.50"), Currency.getInstance("CZK"));
		return new ReservationViewDto(uuid, court, userBasicView, startTime, startTime.plusHours(1),
				startTime.minusDays(1), false, totalPrice);
	}

	public static void compareViewAndCreate(ReservationViewDto reservationViewDto, ReservationCreateDto createDto,
			Court court, User user) {
		assertThat(reservationViewDto.uid()).isNotNull();
		assertThat(reservationViewDto.fromTime()).isEqualTo(createDto.fromTime());
		assertThat(reservationViewDto.toTime()).isEqualTo(createDto.toTime());
		assertThat(reservationViewDto.isQuadGame()).isEqualTo(createDto.isQuadGame());
		assertThat(reservationViewDto.totalPrice()).isNotNull();
		CourtTestData.compareViewAndEntity(reservationViewDto.court(), court, court.getSurface());
		UserTestData.compareViewAndEntity(reservationViewDto.user(), user);
	}

	public static void compareViewAndEntity(ReservationViewDto reservationViewDto, Reservation entity) {
		assertThat(reservationViewDto.uid()).isEqualTo(entity.getUid());
		assertThat(reservationViewDto.fromTime()).isEqualTo(entity.getFromTime());
		assertThat(reservationViewDto.toTime()).isEqualTo(entity.getToTime());
		assertThat(reservationViewDto.isQuadGame()).isEqualTo(entity.getIsQuadGame());
		assertThat(reservationViewDto.totalPrice()).isEqualTo(entity.getTotalPrice());
		CourtTestData.compareViewAndEntity(reservationViewDto.court(), entity.getCourt(),
				entity.getCourt().getSurface());
		UserTestData.compareViewAndEntity(reservationViewDto.user(), entity.getUser());
	}

}
