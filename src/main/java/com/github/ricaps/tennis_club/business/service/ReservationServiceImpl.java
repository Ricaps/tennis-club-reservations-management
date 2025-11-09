package com.github.ricaps.tennis_club.business.service;

import com.github.ricaps.tennis_club.business.service.definition.GenericService;
import com.github.ricaps.tennis_club.business.service.definition.ReservationService;
import com.github.ricaps.tennis_club.business.utils.MoneyUtils;
import com.github.ricaps.tennis_club.exception.EntityExistsException;
import com.github.ricaps.tennis_club.exception.EntityNotExistsException;
import com.github.ricaps.tennis_club.peristence.dao.definition.ReservationDao;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.MoneyAmount;
import com.github.ricaps.tennis_club.peristence.entity.Reservation;
import com.github.ricaps.tennis_club.peristence.utils.PageableResult;
import com.github.ricaps.tennis_club.utils.ValidationHelper;
import jakarta.validation.ValidationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReservationServiceImpl implements ReservationService {

	public static final double QUAD_GAME_MULTIPLIER = 1.5;

	private final ReservationDao reservationDao;

	private final GenericService<Reservation> genericService;

	public ReservationServiceImpl(ReservationDao reservationDao) {
		this.reservationDao = reservationDao;
		this.genericService = new GenericService<>(reservationDao, Reservation.class);
	}

	private static BigDecimal calculateTotalPrice(Reservation reservation) {
		final BigDecimal minutePrice = reservation.getCourt().getSurface().getPrice();
		final long reservationDuration = Duration.between(reservation.getFromTime(), reservation.getToTime())
			.toMinutes();
		final double multiplier = reservation.getIsQuadGame() ? QUAD_GAME_MULTIPLIER : 1;
		return MoneyUtils.multiply(minutePrice, new BigDecimal(reservationDuration * multiplier));

	}

	private static void validateTimeRange(Reservation entity) {
		if (!entity.getFromTime().isBefore(entity.getToTime())) {
			throw new ValidationException("From time must be before to time!");
		}
	}

	@Override
	public Reservation create(Reservation entity) {
		ValidationHelper.requireNonNull(entity, "Reservation must not be null!");
		validateTimeRange(entity);
		validateExistingReservation(entity);

		final BigDecimal totalPrice = calculateTotalPrice(entity);
		final Currency priceCurrency = entity.getCourt().getSurface().getCurrency();
		entity.setTotalPrice(new MoneyAmount(totalPrice, priceCurrency));

		return genericService.create(entity);
	}

	private void validateExistingReservation(Reservation entity) {
		final Court court = entity.getCourt();
		final List<Reservation> reservationList = reservationDao.getReservationsAtTimeFrame(entity.getFromTime(),
				entity.getToTime(), court.getUid());

		if (reservationList.size() == 1 && reservationList.getFirst().equals(entity)) {
			// If there is only one reservation and the reservation is equal to current
			// one
			// In case of update
			return;
		}

		if (!reservationList.isEmpty()) {
			throw new EntityExistsException("There is already existing reservation for the specified time frame!");
		}
	}

	@Override
	public Optional<Reservation> get(UUID uid) {
		return genericService.get(uid);
	}

	@Override
	public Reservation getReference(UUID uuid) throws EntityNotExistsException {
		return genericService.getReference(uuid);
	}

	@Override
	public List<Reservation> getAll(Pageable pageable) {
		return genericService.getAll(pageable);
	}

	@Override
	public Reservation update(Reservation entity) {
		ValidationHelper.requireNonNull(entity, "Reservation must not be null!");
		validateTimeRange(entity);
		validateExistingReservation(entity);

		final BigDecimal totalPrice = calculateTotalPrice(entity);
		final Currency priceCurrency = entity.getCourt().getSurface().getCurrency();
		entity.setTotalPrice(new MoneyAmount(totalPrice, priceCurrency));

		return genericService.update(entity);
	}

	@Override
	public void delete(UUID uid) {
		genericService.delete(uid);
	}

	@Override
	public long count() {
		return genericService.count();
	}

	@Override
	public PageableResult<Reservation> getAllByCourt(UUID courtUID, Pageable pageable) {
		return reservationDao.getReservationsAtCourt(courtUID, pageable.getPageNumber(), pageable.getPageSize(),
				pageable.getSort());
	}

	@Override
	public PageableResult<Reservation> getAllByPhoneNumber(String phoneNumber, OffsetDateTime fromTime,
			Pageable pageable) {
		return reservationDao.getReservationsByPhoneNumber(phoneNumber, fromTime, pageable.getPageNumber(),
				pageable.getPageSize(), pageable.getSort());
	}

}
