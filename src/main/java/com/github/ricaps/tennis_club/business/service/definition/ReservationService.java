package com.github.ricaps.tennis_club.business.service.definition;

import com.github.ricaps.tennis_club.peristence.entity.Reservation;
import com.github.ricaps.tennis_club.peristence.utils.PageableResult;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface ReservationService extends CrudService<Reservation> {

	PageableResult<Reservation> getAllByCourt(UUID courtUID, Pageable pageable);

	PageableResult<Reservation> getAllByPhoneNumber(String phoneNumber, OffsetDateTime fromTime, Pageable pageable);
}
