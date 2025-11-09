package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.exception.ValueIsMissingException;
import com.github.ricaps.tennis_club.peristence.dao.definition.CrudDao;
import com.github.ricaps.tennis_club.peristence.dao.definition.ReservationDao;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.MoneyAmount;
import com.github.ricaps.tennis_club.peristence.entity.Reservation;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.peristence.utils.PageableResult;
import com.github.ricaps.tennis_club.test_utils.CourtTestData;
import com.github.ricaps.tennis_club.test_utils.ReservationTestData;
import com.github.ricaps.tennis_club.test_utils.SurfaceTestData;
import com.github.ricaps.tennis_club.test_utils.UserTestData;
import com.github.ricaps.tennis_club.utils.UUIDUtils;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(ReservationDaoImpl.class)
class ReservationDaoImplTest extends AbstractDaoTest<Reservation> {

	@Autowired
	EntityManager entityManager;

	@Autowired
	ReservationDao reservationDao;

	private Court court;

	private User user;

	private Surface surface;

	@Autowired
	public ReservationDaoImplTest(CrudDao<Reservation> entityDao) {
		super(entityDao);
	}

	private static Stream<Arguments> provideIntervalTestArguments() {
		Reservation reservation = ReservationTestData.entity(null, null);

		return Stream.of(
				Arguments.of(reservation, reservation.getFromTime().minusHours(1),
						reservation.getFromTime().minusMinutes(1), false, "No overlap under interval"),
				Arguments.of(reservation, reservation.getToTime().plusMinutes(1), reservation.getToTime().plusHours(1),
						false, "No overlap above interval"),
				Arguments.of(reservation, reservation.getFromTime().minusHours(1), reservation.getFromTime(), true,
						"Overlap at interval beginning"),
				Arguments.of(reservation, reservation.getToTime(), reservation.getToTime().plusHours(1), true,
						"Overlap at interval end"),
				Arguments.of(reservation, reservation.getFromTime().minusMinutes(30),
						reservation.getFromTime().plusMinutes(30), true, "Overlap over beginning"),
				Arguments.of(reservation, reservation.getToTime().minusMinutes(30),
						reservation.getToTime().plusMinutes(30), true, "Overlap over end"),
				Arguments.of(reservation, reservation.getFromTime().minusMinutes(30),
						reservation.getToTime().plusMinutes(30), true, "Full overlap of interval"),
				Arguments.of(reservation, reservation.getFromTime().plusMinutes(1),
						reservation.getToTime().minusMinutes(1), true, "Inside interval"),
				Arguments.of(reservation, reservation.getFromTime(), reservation.getToTime(), true,
						"Exact same interval"));
	}

	@BeforeEach
	void setup() {
		surface = SurfaceTestData.createSurface();
		court = CourtTestData.entity(surface);
		user = UserTestData.entity(true);

		entityManager.persist(surface);
		entityManager.persist(court);
		entityManager.persist(user);

		Mockito.reset(entityManager);
	}

	@Override
	protected Reservation createEntity() {
		return ReservationTestData.entity(court, user);
	}

	@Override
	protected void checkEntity(Reservation actualEntity, Reservation referenceEntity) {
		assertThat(actualEntity.getUid()).isEqualTo(referenceEntity.getUid());
		assertThat(actualEntity.getCourt()).isEqualTo(referenceEntity.getCourt());
		assertThat(actualEntity.getUser()).isEqualTo(referenceEntity.getUser());
		assertThat(actualEntity.getFromTime()).isEqualTo(referenceEntity.getFromTime());
		assertThat(actualEntity.getToTime()).isEqualTo(referenceEntity.getToTime());
		assertThat(actualEntity.getTotalPrice()).isEqualTo(referenceEntity.getTotalPrice());
	}

	@Override
	protected Reservation updateEntity(Reservation entity) {
		entity.setTotalPrice(new MoneyAmount(new BigDecimal("25.5"), Currency.getInstance("CZK")));
		return entity;
	}

	@Test
	void getReservationsAtTimeFrame_fromNull_throwsException() {
		OffsetDateTime offsetDateTime = OffsetDateTime.now();
		assertThatThrownBy(() -> reservationDao.getReservationsAtTimeFrame(null, offsetDateTime, UUIDUtils.generate()))
			.isInstanceOf(ValueIsMissingException.class);
	}

	@Test
	void getReservationsAtTimeFrame_toNull_throwsException() {
		OffsetDateTime offsetDateTime = OffsetDateTime.now();
		assertThatThrownBy(() -> reservationDao.getReservationsAtTimeFrame(offsetDateTime, null, UUIDUtils.generate()))
			.isInstanceOf(ValueIsMissingException.class);
	}

	@Test
	void getReservationsAtTimeFrame_uuidNull_throwsException() {
		OffsetDateTime offsetDateTime = OffsetDateTime.now();
		assertThatThrownBy(() -> reservationDao.getReservationsAtTimeFrame(offsetDateTime, offsetDateTime, null))
			.isInstanceOf(ValueIsMissingException.class);
	}

	@ParameterizedTest
	@MethodSource("provideIntervalTestArguments")
	void getReservationsAtTimeFrame_exactSameInterval_reservationReturned(Reservation reservation, OffsetDateTime from,
			OffsetDateTime to, boolean expectValue, String message) {
		reservation.setUser(user);
		reservation.setCourt(court);
		reservationDao.save(reservation);

		List<Reservation> reservationList = reservationDao.getReservationsAtTimeFrame(from, to,
				reservation.getCourt().getUid());

		if (expectValue) {
			assertThat(reservationList).withFailMessage(message).hasSize(1);
			assertThat(reservationList.getFirst()).isEqualTo(reservation);
		}
		else {
			assertThat(reservationList).withFailMessage(message).isEmpty();
		}
	}

	@Test
	void getReservationAtCourt_pageable_returnsResultWithCount() {
		// Prepare data
		Court anotherCourt = CourtTestData.entity(surface);
		entityManager.persist(anotherCourt);
		List<Reservation> reservations = createReservations(anotherCourt);

		PageableResult<Reservation> result = reservationDao.getReservationsAtCourt(anotherCourt.getUid(), 0, 20,
				Sort.by("createdAt"));
		assertThat(result.totalCount()).isEqualTo(10);
		assertThat(result.data()).containsAll(reservations);
	}

	@Test
	void getReservationAtCourt_pageable_returnsOnlyFilteredResult() {
		// Prepare data
		Court court = CourtTestData.entity(surface);
		Court anotherCourt = CourtTestData.entity(surface);
		entityManager.persist(court);
		entityManager.persist(anotherCourt);

		List<Reservation> reservations1 = createReservations(court);
		List<Reservation> reservations2 = createReservations(anotherCourt);

		PageableResult<Reservation> result = reservationDao.getReservationsAtCourt(anotherCourt.getUid(), 0, 20,
				Sort.by("createdAt"));
		assertThat(result.totalCount()).isEqualTo(10);
		assertThat(result.data()).doesNotContainAnyElementsOf(reservations1);
		assertThat(result.data()).containsAll(reservations2);
	}

	private List<Reservation> createReservations(Court court) {
		List<Reservation> reservations = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Reservation reservation = ReservationTestData.entity(court, user);
			reservations.add(reservation);
		}
		reservationDao.saveAll(reservations);

		return reservations;
	}

	@Test
	void getReservationsByPhoneNumber_allInFuture_returnsOnlyFilteredResult() {
		OffsetDateTime startTime = Instant.parse("2025-01-01T14:00:00Z").atOffset(ZoneOffset.UTC);
		User user = UserTestData.entity(true);
		entityManager.persist(user);

		List<Reservation> reservations = createReservations(user, startTime.plusDays(1));
		PageableResult<Reservation> result = reservationDao.getReservationsByPhoneNumber(user.getPhoneNumber(),
				startTime, 0, 20, Sort.by("createdAt"));

		assertThat(result.data()).containsAll(reservations);
		assertThat(result.totalCount()).isEqualTo(reservations.size());
	}

	@Test
	void getReservationsByPhoneNumber_futureAndPast_returnsResultsOnlyInFuture() {
		OffsetDateTime startTime = Instant.parse("2025-01-01T14:00:00Z").atOffset(ZoneOffset.UTC);
		User user = UserTestData.entity(true);
		entityManager.persist(user);

		List<Reservation> reservationsInPast = createReservations(user, startTime.minusDays(1));
		List<Reservation> reservationsInFuture = createReservations(user, startTime.plusDays(1));
		PageableResult<Reservation> result = reservationDao.getReservationsByPhoneNumber(user.getPhoneNumber(),
				startTime, 0, 20, Sort.by("createdAt"));

		assertThat(result.data()).containsAll(reservationsInFuture);
		assertThat(result.data()).doesNotContainAnyElementsOf(reservationsInPast);
		assertThat(result.totalCount()).isEqualTo(reservationsInFuture.size());
	}

	@Test
	void getReservationsByPhoneNumber_inFutureAndDifferentPhoneNumbers_returnsOnlyFutureReservationsWithCorrectPhoneNumber() {
		OffsetDateTime startTime = Instant.parse("2025-01-01T14:00:00Z").atOffset(ZoneOffset.UTC);
		User user1 = UserTestData.entity(true);
		User user2 = UserTestData.entity(true);
		entityManager.persist(user1);
		entityManager.persist(user2);

		List<Reservation> reservationsInPast = createReservations(user1, startTime.minusDays(1));
		List<Reservation> reservationsInFuture = createReservations(user1, startTime.plusDays(1));
		List<Reservation> reservationsInFutureUser2 = createReservations(user2, startTime.plusDays(1));
		PageableResult<Reservation> result = reservationDao.getReservationsByPhoneNumber(user1.getPhoneNumber(),
				startTime, 0, 20, Sort.by("createdAt"));

		assertThat(result.data()).containsAll(reservationsInFuture);
		assertThat(result.data()).doesNotContainAnyElementsOf(reservationsInPast);
		assertThat(result.data()).doesNotContainAnyElementsOf(reservationsInFutureUser2);
		assertThat(result.totalCount()).isEqualTo(reservationsInFuture.size());
	}

	private List<Reservation> createReservations(User user, OffsetDateTime referenceTime) {
		List<Reservation> reservations = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Reservation reservation = ReservationTestData.entity(court, user, referenceTime);
			reservations.add(reservation);
		}
		reservationDao.saveAll(reservations);

		return reservations;
	}

}