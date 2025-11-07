package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.peristence.dao.definition.ReservationDao;
import com.github.ricaps.tennis_club.peristence.entity.Reservation;
import com.github.ricaps.tennis_club.utils.ValidationHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class ReservationDaoImpl extends AbstractDao<Reservation> implements ReservationDao {

	private final EntityManager entityManager;

	public ReservationDaoImpl(EntityManager entityManager) {
		super(entityManager);
		this.entityManager = entityManager;
	}

	@Override
	protected Class<Reservation> getEntityClass() {
		return Reservation.class;
	}

	@Override
	public List<Reservation> getReservationsAtTimeFrame(OffsetDateTime from, OffsetDateTime to, UUID courtID) {
		ValidationHelper.requireNonNull(from, "From cannot be null!");
		ValidationHelper.requireNonNull(to, "To cannot be null!");
		ValidationHelper.requireNonNull(courtID, "Court ID cannot be null!");

		TypedQuery<Reservation> query = entityManager
			.createQuery("FROM Reservation r WHERE ((:from >= fromTime AND :from <= toTime) OR "
					+ "(:to >= fromTime AND :to <= toTime) OR " + "(:from <= fromTime AND :to >= toTime)) "
					+ "AND court.id = :courtUid", Reservation.class);

		query.setParameter("from", from);
		query.setParameter("to", to);
		query.setParameter("courtUid", courtID);

		return query.getResultList();
	}

}
