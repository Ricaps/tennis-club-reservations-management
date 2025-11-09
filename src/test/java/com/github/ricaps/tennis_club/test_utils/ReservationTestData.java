package com.github.ricaps.tennis_club.test_utils;

import com.github.ricaps.tennis_club.api.reservation.ReservationCreateDto;
import com.github.ricaps.tennis_club.api.reservation.ReservationViewDto;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.MoneyAmount;
import com.github.ricaps.tennis_club.peristence.entity.Reservation;
import com.github.ricaps.tennis_club.peristence.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class ReservationTestData {

	private final Clock clock;

	public ReservationTestData(Clock clock) {
		this.clock = clock;
	}

	private OffsetDateTime getCurrentTime() {
		return clock.instant().atOffset(ZoneOffset.UTC);
	}

	public Reservation entity(Court court, User user) {
		OffsetDateTime startTime = clock.instant().atOffset(ZoneOffset.UTC);
		return entity(court, user, startTime);
	}

	public Reservation entity(Court court, User user, OffsetDateTime referenceTime) {
		return Reservation.builder()
			.uid(UUID.randomUUID())
			.fromTime(referenceTime.plusMinutes(1))
			.toTime(referenceTime.plusHours(2))
			.isQuadGame(true)
			.totalPrice(new MoneyAmount(new BigDecimal("15.30"), Currency.getInstance("CZK")))
			.court(court)
			.user(user)
			.build();
	}

	public ReservationCreateDto createReservation(UUID courtUID) {
		OffsetDateTime startTime = getCurrentTime();

		return new ReservationCreateDto(courtUID, startTime.plusMinutes(1), startTime.plusHours(1), false);
	}

	public ReservationCreateDto createInvalid(UUID surfaceUID) {
		OffsetDateTime startTime = getCurrentTime();

		// move start day to the past (test date & time defined in TimeConfig#clock bean)
		return new ReservationCreateDto(surfaceUID, startTime.minusMonths(1), startTime.plusHours(1), false);
	}

	public void compareViewAndCreate(ReservationViewDto reservationViewDto, ReservationCreateDto createDto, Court court,
			User user) {
		assertThat(reservationViewDto.uid()).isNotNull();
		assertThat(reservationViewDto.fromTime()).isEqualTo(createDto.fromTime());
		assertThat(reservationViewDto.toTime()).isEqualTo(createDto.toTime());
		assertThat(reservationViewDto.isQuadGame()).isEqualTo(createDto.isQuadGame());
		assertThat(reservationViewDto.totalPrice()).isNotNull();
		CourtTestData.compareViewAndEntity(reservationViewDto.court(), court, court.getSurface());
		UserTestData.compareViewAndEntity(reservationViewDto.user(), user);
	}

	public void compareViewAndEntity(ReservationViewDto reservationViewDto, Reservation entity) {
		assertThat(reservationViewDto.uid()).isEqualTo(entity.getUid());
		assertThat(reservationViewDto.fromTime()).isEqualTo(entity.getFromTime());
		assertThat(reservationViewDto.toTime()).isEqualTo(entity.getToTime());
		assertThat(reservationViewDto.isQuadGame()).isEqualTo(entity.getIsQuadGame());
		assertThat(reservationViewDto.totalPrice().amount()).isEqualTo(entity.getTotalPrice().getAmount());
		assertThat(reservationViewDto.totalPrice().currency()).isEqualTo(entity.getTotalPrice().getCurrency());
		CourtTestData.compareViewAndEntity(reservationViewDto.court(), entity.getCourt(),
				entity.getCourt().getSurface());
		UserTestData.compareViewAndEntity(reservationViewDto.user(), entity.getUser());
	}

}
