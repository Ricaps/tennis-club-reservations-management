package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.peristence.dao.definition.ReservationDao;
import com.github.ricaps.tennis_club.peristence.entity.Reservation;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class ReservationDaoImpl extends AbstractDao<Reservation> implements ReservationDao {

	public ReservationDaoImpl(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	protected Class<Reservation> getEntityClass() {
		return Reservation.class;
	}

}
