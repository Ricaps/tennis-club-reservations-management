package com.github.ricaps.tennis_club.peristence.dao.definition;

import com.github.ricaps.tennis_club.peristence.entity.Reservation;

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
}
