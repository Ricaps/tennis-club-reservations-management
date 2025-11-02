package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.peristence.dao.definition.SurfaceDao;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import com.github.ricaps.tennis_club.test_utils.TestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(SurfaceDaoImpl.class)
class SurfaceDaoImplIT extends AbstractDaoTest<Surface> {

	@Autowired
	protected SurfaceDaoImplIT(SurfaceDao surfaceDao) {
		super(surfaceDao);
	}

	@Override
	protected Surface createEntity() {
		return TestData.createSurface();
	}

	@Override
	protected void checkEntity(Surface actualEntity, Surface referenceEntity) {
		assertThat(actualEntity.getUid()).isEqualTo(referenceEntity.getUid());
		assertThat(actualEntity.getName()).isEqualTo(referenceEntity.getName());
		assertThat(actualEntity.getCurrency()).isEqualTo(referenceEntity.getCurrency());
		assertThat(actualEntity.getPrice()).isEqualTo(referenceEntity.getPrice());
	}

	@Override
	protected Surface updateEntity(Surface entity) {
		entity.setName("new name");

		return entity;
	}

}