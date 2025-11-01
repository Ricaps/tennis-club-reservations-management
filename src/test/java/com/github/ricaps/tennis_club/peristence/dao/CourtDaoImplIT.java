package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.peristence.dao.definition.CrudDao;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(CourtDaoImpl.class)
class CourtDaoImplIT extends AbstractDaoTest<Court> {

	@Autowired
	EntityManager entityManager;

	private Surface surface;

	@Autowired
	protected CourtDaoImplIT(CrudDao<Court> surfaceDao) {
		super(surfaceDao);
	}

	@BeforeEach
	void setup() {
		surface = Surface.builder()
			.name("test")
			.price(new BigDecimal("12.5"))
			.uid(UUID.randomUUID())
			.currency(Currency.getInstance("CZK"))
			.build();

		entityManager.persist(surface);

		Mockito.reset(entityManager);
	}

	@Override
	protected Court createEntity() {
		return Court.builder().uid(UUID.randomUUID()).name("Court").surface(surface).build();
	}

	@Override
	protected void checkEntity(Court actualEntity, Court referenceEntity) {
		assertThat(actualEntity.getUid()).isEqualTo(referenceEntity.getUid());
		assertThat(actualEntity.getName()).isEqualTo(referenceEntity.getName());
		assertThat(actualEntity.getSurface()).isEqualTo(referenceEntity.getSurface());
	}

	@Override
	protected Court updateEntity(Court entity) {
		entity.setName("New name");

		return entity;
	}

}