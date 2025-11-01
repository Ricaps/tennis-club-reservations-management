package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.peristence.dao.definition.SurfaceDao;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(SurfaceDaoImpl.class)
class SurfaceDaoImplIT extends AbstractDaoTest<Surface> {

	@Autowired
	protected SurfaceDaoImplIT(SurfaceDao surfaceDao) {
		super(surfaceDao);
	}

	@Override
	protected Surface createEntity() {
		return Surface.builder()
			.name("test")
			.price(new BigDecimal("12.5"))
			.uid(UUID.randomUUID())
			.currency(Currency.getInstance("CZK"))
			.build();
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