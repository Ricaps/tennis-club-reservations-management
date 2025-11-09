package com.github.ricaps.tennis_club.peristence.dao.definition;

import com.github.ricaps.tennis_club.peristence.entity.Reservation;
import com.github.ricaps.tennis_club.peristence.utils.PageableResult;
import org.springframework.data.domain.Sort;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservationDao extends CrudDao<Reservation> {

	/**
	 * Gets reservations at specified time for specified courtID
	 * @param from start range of the time
	 * @param to end range of the time
	 * @param courtID ID of the court
	 * @return List of reservations if such reservation exists
	 */
	List<Reservation> getReservationsAtTimeFrame(OffsetDateTime from, OffsetDateTime to, UUID courtID);

	/**
	 * Gets paged reservations at given court
	 * @param courtUid ID of the court to filter by
	 * @param pageNumber page number
	 * @param pageSize size of each page
	 * @param sort sort definition
	 * @return result in pageable result (total count with data)
	 */
	PageableResult<Reservation> getReservationsAtCourt(UUID courtUid, int pageNumber, int pageSize, Sort sort);

	/**
	 * Gets paged reservation for given user's phone number, and only in the future if
	 * wanted
	 * @param phoneNumber user's phone number
	 * @param fromTime includes only records that are newer (has greater fromTime
	 * argument)
	 * @param pageNumber page number
	 * @param pageSize size of each page
	 * @param sort sort definition
	 * @return reservations in pageable result (total count with data)
	 */
	PageableResult<Reservation> getReservationsByPhoneNumber(String phoneNumber, OffsetDateTime fromTime,
			int pageNumber, int pageSize, Sort sort);

}
