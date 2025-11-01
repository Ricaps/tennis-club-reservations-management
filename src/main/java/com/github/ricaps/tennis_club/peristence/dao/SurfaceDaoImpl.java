package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.peristence.dao.definition.SurfaceDao;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class SurfaceDaoImpl extends AbstractDao<Surface> implements SurfaceDao {

	public SurfaceDaoImpl(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	protected Class<Surface> getEntityClass() {
		return Surface.class;
	}

}
