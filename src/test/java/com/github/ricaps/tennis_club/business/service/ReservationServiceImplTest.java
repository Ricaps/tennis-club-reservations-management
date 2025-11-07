package com.github.ricaps.tennis_club.business.service;

import com.github.ricaps.tennis_club.business.utils.MoneyUtils;
import com.github.ricaps.tennis_club.exception.EntityExistsException;
import com.github.ricaps.tennis_club.exception.EntityNotExistsException;
import com.github.ricaps.tennis_club.exception.ValueIsMissingException;
import com.github.ricaps.tennis_club.peristence.dao.definition.ReservationDao;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.Reservation;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.test_utils.CourtTestData;
import com.github.ricaps.tennis_club.test_utils.MockUtils;
import com.github.ricaps.tennis_club.test_utils.ReservationTestData;
import com.github.ricaps.tennis_club.test_utils.UserTestData;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static com.github.ricaps.tennis_club.business.service.ReservationServiceImpl.QUAD_GAME_MULTIPLIER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

	@Mock
	private ReservationDao reservationDao;

	@InjectMocks
	private ReservationServiceImpl reservationService;

	private Reservation createEntity() {
		Court court = CourtTestData.entity();
		User user = UserTestData.entity(true);
		return ReservationTestData.entity(court, user);
	}

	@Test
	void create_nullEntity_throwsException() {
		assertThatThrownBy(() -> reservationService.create(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(reservationDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void create_nullUuid_throwsException() {
		Reservation reservation = createEntity();
		reservation.setUid(null);

		assertThatThrownBy(() -> reservationService.create(reservation)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(reservationDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void create_entityExists_throwsException() {
		Reservation reservation = createEntity();
		Mockito.when(reservationDao.existsById(reservation.getUid())).thenReturn(true);

		assertThatThrownBy(() -> reservationService.create(reservation)).isInstanceOf(EntityExistsException.class);
		Mockito.verify(reservationDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void create_fromTimeEqualsToTime_throwsException() {
		Reservation reservation = createEntity();
		reservation.setToTime(reservation.getFromTime());

		assertThatThrownBy(() -> reservationService.create(reservation)).isInstanceOf(ValidationException.class)
			.hasMessage("From time must be before to time!");
		Mockito.verify(reservationDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void create_fromAfterToEqualsToTime_throwsException() {
		Reservation reservation = createEntity();
		reservation.setFromTime(reservation.getToTime().plusMinutes(1));

		assertThatThrownBy(() -> reservationService.create(reservation)).isInstanceOf(ValidationException.class)
			.hasMessage("From time must be before to time!");
		Mockito.verify(reservationDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void create_toTimeBeforeFromTime_throwsException() {
		Reservation reservation = createEntity();
		reservation.setToTime(reservation.getFromTime().minusMinutes(1));

		assertThatThrownBy(() -> reservationService.create(reservation)).isInstanceOf(ValidationException.class)
			.hasMessage("From time must be before to time!");
		Mockito.verify(reservationDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void create_allConditionsCorrect_creationSuccessful() {
		Reservation reservation = createEntity();
		Mockito.when(reservationDao.existsById(reservation.getUid())).thenReturn(false);
		Mockito.when(reservationDao.save(reservation)).thenReturn(reservation);

		Reservation result = reservationService.create(reservation);

		assertThat(result).isEqualTo(reservation);
		Mockito.verify(reservationDao, Mockito.times(1)).save(reservation);
	}

	@Test
	void create_isQuadGame_priceMultiplied() {
		BigDecimal basicPrice = new BigDecimal("15.50");
		Reservation reservation = createEntity();
		Duration duration = Duration.between(reservation.getFromTime(), reservation.getToTime());
		reservation.getCourt().getSurface().setPrice(basicPrice);
		reservation.setIsQuadGame(true);
		Mockito.when(reservationDao.existsById(reservation.getUid())).thenReturn(false);
		Mockito.when(reservationDao.save(reservation)).thenReturn(reservation);

		Reservation result = reservationService.create(reservation);

		assertThat(result).isEqualTo(reservation);
		assertThat(result.getTotalPrice().getAmount()).isEqualTo(
				MoneyUtils.multiply(basicPrice, BigDecimal.valueOf(duration.toMinutes() * QUAD_GAME_MULTIPLIER)));
		Mockito.verify(reservationDao, Mockito.times(1)).save(reservation);
	}

	@Test
	void create_isDoubleGame_priceMultiplied() {
		BigDecimal basicPrice = new BigDecimal("15.50");
		Reservation reservation = createEntity();
		Duration duration = Duration.between(reservation.getFromTime(), reservation.getToTime());
		reservation.getCourt().getSurface().setPrice(basicPrice);
		reservation.setIsQuadGame(false);
		Mockito.when(reservationDao.existsById(reservation.getUid())).thenReturn(false);
		Mockito.when(reservationDao.save(reservation)).thenReturn(reservation);

		Reservation result = reservationService.create(reservation);

		assertThat(result).isEqualTo(reservation);
		assertThat(result.getTotalPrice().getAmount())
			.isEqualTo(MoneyUtils.multiply(basicPrice, BigDecimal.valueOf(duration.toMinutes())));
		Mockito.verify(reservationDao, Mockito.times(1)).save(reservation);
	}

	@Test
	void create_reservationOverlap_throwsException() {
		Reservation reservation = createEntity();
		Reservation overlappedReservation = createEntity();

		Mockito.when(reservationDao.getReservationsAtTimeFrame(reservation.getFromTime(), reservation.getToTime(),
				reservation.getCourt().getUid()))
			.thenReturn(List.of(overlappedReservation));

		assertThatThrownBy(() -> reservationService.create(reservation)).isInstanceOf(EntityExistsException.class);

		Mockito.verify(reservationDao, Mockito.never()).save(reservation);
	}

	@Test
	void create_reservationOverlapWithSelf_returnsResult() {
		Reservation reservation = createEntity();

		Mockito.when(reservationDao.existsById(reservation.getUid())).thenReturn(false);
		Mockito.when(reservationDao.getReservationsAtTimeFrame(reservation.getFromTime(), reservation.getToTime(),
				reservation.getCourt().getUid()))
			.thenReturn(List.of(reservation));
		Mockito.when(reservationDao.save(reservation)).thenReturn(reservation);

		Reservation result = reservationService.create(reservation);

		assertThat(result).isEqualTo(reservation);
		Mockito.verify(reservationDao, Mockito.times(1)).save(reservation);
	}

	@Test
	void get_nullUid_throwsException() {
		assertThatThrownBy(() -> reservationService.get(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(reservationDao, Mockito.never()).findById(Mockito.any());
	}

	@Test
	void get_notFoundEntity_returnsEmptyOptional() {
		Reservation reservation = createEntity();
		Mockito.when(reservationDao.findById(reservation.getUid())).thenReturn(Optional.empty());

		Optional<Reservation> result = reservationService.get(reservation.getUid());

		assertThat(result).isEmpty();
		Mockito.verify(reservationDao, Mockito.times(1)).findById(reservation.getUid());
	}

	@Test
	void get_foundEntity_returnsPresentOptional() {
		Reservation reservation = createEntity();
		Mockito.when(reservationDao.findById(reservation.getUid())).thenReturn(Optional.of(reservation));

		Optional<Reservation> result = reservationService.get(reservation.getUid());

		assertThat(result).isPresent().get().isEqualTo(reservation);
		Mockito.verify(reservationDao, Mockito.times(1)).findById(reservation.getUid());
	}

	@Test
	void getAll_nullPageable_throwsException() {
		assertThatThrownBy(() -> reservationService.getAll(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(reservationDao, Mockito.never()).findAll(Mockito.anyInt(), Mockito.anyInt(), Mockito.any());
	}

	@Test
	void getAll_correctInput_returnsMultipleEntities() {
		Reservation court1 = createEntity();
		Reservation court2 = createEntity();

		int pageNumber = 1;
		int pageSize = 10;
		Sort sortBy = Sort.by("uid");

		Pageable pageableMock = MockUtils.mockPageable(pageNumber, pageSize, sortBy);

		Mockito.when(reservationDao.findAll(pageNumber, pageSize, sortBy)).thenReturn(List.of(court1, court2));

		List<Reservation> result = reservationService.getAll(pageableMock);

		assertThat(result).hasSize(2);
		Mockito.verify(reservationDao, Mockito.times(1)).findAll(pageNumber, pageSize, sortBy);
	}

	@Test
	void update_nullEntity_throwsException() {
		assertThatThrownBy(() -> reservationService.update(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(reservationDao, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_nullUuid_throwsException() {
		Reservation reservation = createEntity();
		reservation.setUid(null);

		assertThatThrownBy(() -> reservationService.update(reservation)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(reservationDao, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_entityNotExists_throwsException() {
		Reservation reservation = createEntity();
		Mockito.when(reservationDao.existsById(reservation.getUid())).thenReturn(false);

		assertThatThrownBy(() -> reservationService.update(reservation)).isInstanceOf(EntityNotExistsException.class);
		Mockito.verify(reservationDao, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_allConditionsCorrect_updateSuccessful() {
		Reservation reservation = createEntity();
		Mockito.when(reservationDao.existsById(reservation.getUid())).thenReturn(true);
		Mockito.when(reservationDao.update(reservation)).thenReturn(reservation);

		Reservation result = reservationService.update(reservation);

		assertThat(result).isEqualTo(reservation);
		Mockito.verify(reservationDao, Mockito.times(1)).update(reservation);
	}

	@Test
	void delete_nullUuid_throwsException() {
		assertThatThrownBy(() -> reservationService.delete(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(reservationDao, Mockito.never()).delete(Mockito.any());
	}

	@Test
	void delete_entityNotExists_throwsException() {
		Reservation reservation = createEntity();
		Mockito.when(reservationDao.existsById(reservation.getUid())).thenReturn(false);

		assertThatThrownBy(() -> reservationService.delete(reservation.getUid()))
			.isInstanceOf(EntityNotExistsException.class);
		Mockito.verify(reservationDao, Mockito.never()).delete(Mockito.any());
	}

	@Test
	void delete_allConditionsCorrect_deleteSuccessful() {
		Reservation reservation = createEntity();
		Mockito.when(reservationDao.existsById(reservation.getUid())).thenReturn(true);

		reservationService.delete(reservation.getUid());

		Mockito.verify(reservationDao, Mockito.times(1)).delete(reservation.getUid());
	}

	@Test
	void count_returnsCount() {
		long countRef = 5L;
		Mockito.when(reservationDao.count()).thenReturn(countRef);

		long returned = reservationService.count();

		assertThat(returned).isEqualTo(countRef);
		Mockito.verify(reservationDao, Mockito.times(1)).count();
	}

}