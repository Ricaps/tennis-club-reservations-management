package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.peristence.dao.definition.CourtDao;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class CourtDaoImpl extends AbstractDao<Court> implements CourtDao {

	public CourtDaoImpl(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	protected Class<Court> getEntityClass() {
		return Court.class;
	}

}
