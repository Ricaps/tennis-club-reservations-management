package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.peristence.dao.definition.SurfaceDao;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(SurfaceDaoImpl.class)
@ExtendWith(MockitoExtension.class)
class SurfaceDaoImplIT {

	@Autowired
	@MockitoSpyBean
	private EntityManager entityManager;

	@Autowired
	private SurfaceDao surfaceDao;

	private static Surface createSurface() {
		return Surface.builder()
			.name("test")
			.price(new BigDecimal("12.5"))
			.uid(UUID.randomUUID())
			.currency(Currency.getInstance("CZK"))
			.build();
	}

	private static void checkSurface(Surface createdSurface, Surface surface) {
		assertThat(createdSurface.getUid()).isEqualTo(surface.getUid());
		assertThat(createdSurface.getName()).isEqualTo(surface.getName());
		assertThat(createdSurface.getCurrency()).isEqualTo(surface.getCurrency());
		assertThat(createdSurface.getPrice()).isEqualTo(surface.getPrice());
	}

	@Test
	void createSurface_savedSuccessfully() {
		Surface surface = createSurface();

		surfaceDao.save(surface);
		Optional<Surface> surfaceOpt = surfaceDao.findById(surface.getUid());

		assertThat(surfaceOpt).isPresent();

		Surface createdSurface = surfaceOpt.get();
		checkSurface(createdSurface, surface);

	}

	@Test
	void existsById_notExistingEntity_returnsFalse() {
		boolean result = surfaceDao.existsById(UUID.randomUUID());

		assertThat(result).isFalse();
	}

	@Test
	void existsById_notExistingEntity_returnsTrue() {
		Surface surface = createSurface();

		surfaceDao.save(surface);

		boolean result = surfaceDao.existsById(UUID.randomUUID());

		assertThat(result).isFalse();
	}

	@Test
	void updateEntity_existingEntity_updatedSuccessfully() {
		String newName = "New name";
		Surface surface = createSurface();

		surface = surfaceDao.save(surface);
		surface.setName(newName);
		surfaceDao.update(surface);

		Optional<Surface> surfaceOptional = surfaceDao.findById(surface.getUid());
		assertThat(surfaceOptional).isPresent()
			.get()
			.matches(updatedSurface -> updatedSurface.getName().equals(newName));
	}

	@Test
	void updateEntity_notExistingEntity_created() {
		Surface surface = createSurface();

		surfaceDao.update(surface);

		Optional<Surface> actual = surfaceDao.findById(surface.getUid());
		assertThat(actual).isPresent();

		checkSurface(actual.get(), surface);
	}

	@Test
	void saveAll_emptyList_nothingIsSaved() {
		surfaceDao.saveAll(List.of());

		Mockito.verify(entityManager, Mockito.never()).persist(Mockito.any());
	}

	@Test
	void saveAll_fewEntities_allAreSaved() {
		List<Surface> entities = List.of(createSurface(), createSurface());
		surfaceDao.saveAll(entities);

		assertThat(surfaceDao.findById(entities.getFirst().getUid())).isPresent().get().isEqualTo(entities.getFirst());
		assertThat(surfaceDao.findById(entities.getLast().getUid())).isPresent().get().isEqualTo(entities.getLast());

		Mockito.verify(entityManager, Mockito.times(1)).persist(entities.getFirst());
		Mockito.verify(entityManager, Mockito.times(1)).persist(entities.getLast());
		Mockito.verify(entityManager, Mockito.never()).flush();
		Mockito.verify(entityManager, Mockito.never()).clear();
	}

	@Test
	void saveAll_moreThan50Entities_allAreSavedBatched() {
		List<Surface> entities = new ArrayList<>();
		for (int i = 0; i <= 50; i++) {
			entities.add(createSurface());
		}
		surfaceDao.saveAll(entities);

		assertThat(surfaceDao.count()).isEqualTo(entities.size());

		Mockito.verify(entityManager, Mockito.times(entities.size())).persist(Mockito.any());
		Mockito.verify(entityManager, Mockito.times(1)).flush();
		Mockito.verify(entityManager, Mockito.times(1)).clear();
	}

	@Test
	void delete_deleteEntity_successfullyDeleted() {
		Surface surface = createSurface();

		surfaceDao.save(surface);
		assertThat(surfaceDao.existsById(surface.getUid())).isTrue();

		surfaceDao.delete(surface);

		assertThat(surfaceDao.existsById(surface.getUid())).isFalse();
	}

}