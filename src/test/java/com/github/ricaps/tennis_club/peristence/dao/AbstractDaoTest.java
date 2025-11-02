package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.peristence.dao.definition.CrudDao;
import com.github.ricaps.tennis_club.peristence.entity.IdentifiedEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
public abstract class AbstractDaoTest<EntityType extends IdentifiedEntity> {

	private final CrudDao<EntityType> entityDao;

	@Autowired
	@MockitoSpyBean
	private EntityManager entityManager;

	protected AbstractDaoTest(CrudDao<EntityType> entityDao) {
		this.entityDao = entityDao;
	}

	protected abstract EntityType createEntity();

	protected abstract void checkEntity(EntityType actualEntity, EntityType referenceEntity);

	protected abstract EntityType updateEntity(EntityType entity);

	@Test
	void createSurface_savedSuccessfully() {
		EntityType entity = createEntity();

		entityDao.save(entity);
		Optional<EntityType> surfaceOpt = entityDao.findById(entity.getUid());

		assertThat(surfaceOpt).isPresent();

		EntityType createdSurface = surfaceOpt.get();
		checkEntity(createdSurface, entity);
	}

	@Test
	void createSurface_null_returnsNull() {
		EntityType saved = entityDao.save(null);

		assertThat(saved).isNull();
		Mockito.verify(entityManager, Mockito.never()).persist(Mockito.any());
	}

	@Test
	void existsById_notExistingEntity_returnsFalse() {
		boolean result = entityDao.existsById(UUID.randomUUID());

		assertThat(result).isFalse();
	}

	@Test
	void existsById_notExistingEntity_returnsTrue() {
		EntityType entity = createEntity();

		entityDao.save(entity);

		boolean result = entityDao.existsById(UUID.randomUUID());

		assertThat(result).isFalse();
	}

	@Test
	void existsById_countReturnsNull_returnsFalse() {
		mockSingleResultToReturnNull();

		EntityType entity = createEntity();
		entityDao.save(entity);

		boolean result = entityDao.existsById(entity.getUid());

		assertThat(result).isFalse();
	}

	@Test
	@SuppressWarnings("unchecked")
	void existsById_nullUuid_returnFalse() {
		boolean result = entityDao.existsById(null);

		assertThat(result).isFalse();
		Mockito.verify(entityManager, Mockito.never()).createQuery(Mockito.any(CriteriaQuery.class));
	}

	@SuppressWarnings("unchecked")
	private void mockSingleResultToReturnNull() {
		TypedQuery<Long> queryMock = Mockito.mock(TypedQuery.class);

		Mockito.doReturn(queryMock).when(entityManager).createQuery(Mockito.any(CriteriaQuery.class));

		Mockito.when(queryMock.getSingleResult()).thenReturn(null);
	}

	@Test
	void updateEntity_existingEntity_updatedSuccessfully() {
		EntityType entity = createEntity();

		entity = entityDao.save(entity);
		EntityType updatedEntity = updateEntity(entity);
		entityDao.update(entity);

		Optional<EntityType> entityOptional = entityDao.findById(entity.getUid());
		assertThat(entityOptional).isPresent();
		checkEntity(entityOptional.get(), updatedEntity);
	}

	@Test
	void updateEntity_notExistingEntity_created() {
		EntityType entity = createEntity();

		entityDao.update(entity);

		Optional<EntityType> actual = entityDao.findById(entity.getUid());
		assertThat(actual).isPresent();

		checkEntity(actual.get(), entity);
	}

	@Test
	void updateEntity_null_notUpdated() {
		EntityType updated = entityDao.update(null);

		assertThat(updated).isNull();
		Mockito.verify(entityManager, Mockito.never()).merge(Mockito.any());
	}

	@Test
	void saveAll_emptyList_nothingIsSaved() {
		entityDao.saveAll(List.of());

		Mockito.verify(entityManager, Mockito.never()).persist(Mockito.any());
	}

	@Test
	void saveAll_null_nothingExecuted() {
		entityDao.saveAll(null);

		Mockito.verify(entityManager, Mockito.never()).persist(Mockito.any());
	}

	@Test
	void saveAll_fewEntities_allAreSaved() {
		List<EntityType> entities = List.of(createEntity(), createEntity());
		entityDao.saveAll(entities);

		assertThat(entityDao.findById(entities.getFirst().getUid())).isPresent().get().isEqualTo(entities.getFirst());
		assertThat(entityDao.findById(entities.getLast().getUid())).isPresent().get().isEqualTo(entities.getLast());

		Mockito.verify(entityManager, Mockito.times(1)).persist(entities.getFirst());
		Mockito.verify(entityManager, Mockito.times(1)).persist(entities.getLast());
		Mockito.verify(entityManager, Mockito.never()).flush();
		Mockito.verify(entityManager, Mockito.never()).clear();
	}

	@Test
	void saveAll_moreThan50Entities_allAreSavedBatched() {
		List<EntityType> entities = generateEntities(51);
		entityDao.saveAll(entities);

		assertThat(entityDao.count()).isEqualTo(entities.size());

		Mockito.verify(entityManager, Mockito.times(entities.size())).persist(Mockito.any());
		Mockito.verify(entityManager, Mockito.times(1)).flush();
		Mockito.verify(entityManager, Mockito.times(1)).clear();
	}

	private List<EntityType> generateEntities(int count) {
		List<EntityType> entities = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			entities.add(createEntity());
		}
		return entities;
	}

	@Test
	void delete_deleteEntity_successfullyDeleted() {
		EntityType entity = createEntity();

		entityDao.save(entity);
		assertThat(entityDao.existsById(entity.getUid())).isTrue();

		boolean result = entityDao.delete(entity.getUid());

		assertThat(result).isTrue();
		assertThat(entityDao.existsById(entity.getUid())).isFalse();
		assertThat(entityDao.findById(entity.getUid())).isEmpty();
	}

	@Test
	void delete_null_nothingExecuted() {
		boolean result = entityDao.delete(null);

		assertThat(result).isFalse();
		Mockito.verify(entityManager, Mockito.never()).remove(Mockito.any());
	}

	@Test
	void count_noEntity_returnsZero() {
		assertThat(entityDao.count()).isEqualTo(0);
	}

	@Test
	void count_resultSetIsNull_returnsZero() {
		mockSingleResultToReturnNull();

		EntityType entity = createEntity();
		entityDao.save(entity);

		assertThat(entityDao.count()).isEqualTo(0);
	}

	@Test
	void count_moreEntities_returnsCount() {
		List<EntityType> entities = List.of(createEntity(), createEntity());
		entityDao.saveAll(entities);

		assertThat(entityDao.count()).isEqualTo(entities.size());
	}

	@Test
	void findAll_noEntities_emptyList() {
		List<EntityType> result = entityDao.findAll(0, 10, Sort.by("uid"));
		assertThat(result).isEmpty();
	}

	@Test
	void findAll_entitiesSortedUid_returnsPages() {
		List<EntityType> entities = generateEntities(50);

		// Sort ascending according to uid
		entities.sort((a, b) -> Objects.compare(a.getUid().toString(), b.getUid().toString(), String::compareTo));

		entityDao.saveAll(entities);

		List<EntityType> result = entityDao.findAll(0, 10, Sort.by("uid"));

		assertThat(result).hasSize(10);
		assertThat(result.getFirst()).isEqualTo(entities.getFirst());
		assertThat(result.getLast()).isEqualTo(entities.get(9));
	}

	@Test
	void findAll_entitiesSortedUidDesc_returnsPages() {
		List<EntityType> entities = generateEntities(50);

		// Sort descending according to uid
		entities.sort((a, b) -> Objects.compare(b.getUid().toString(), a.getUid().toString(), String::compareTo));

		entityDao.saveAll(entities);

		List<EntityType> result = entityDao.findAll(0, 10, Sort.by("uid").descending());

		assertThat(result).hasSize(10);
		assertThat(result.getFirst()).isEqualTo(entities.getFirst());
		assertThat(result.getLast()).isEqualTo(entities.get(9));
	}

	@Test
	void findAll_moreThanLastPage_doesntReturnWholePage() {
		List<EntityType> entities = generateEntities(50);

		// Sort descending according to uid
		entities.sort((a, b) -> Objects.compare(b.getUid().toString(), a.getUid().toString(), String::compareTo));

		entityDao.saveAll(entities);

		List<EntityType> result = entityDao.findAll(2, 20, Sort.by("uid").descending());

		assertThat(result).hasSize(10);
		assertThat(result.getFirst()).isEqualTo(entities.get(40));
		assertThat(result.getLast()).isEqualTo(entities.getLast());
	}

	@Test
	void findAll_moreThanAvailable_returnsEmpty() {
		List<EntityType> entities = generateEntities(50);

		// Sort descending according to uid
		entities.sort((a, b) -> Objects.compare(b.getUid().toString(), a.getUid().toString(), String::compareTo));

		entityDao.saveAll(entities);

		List<EntityType> result = entityDao.findAll(5, 10, Sort.by("uid").descending());

		assertThat(result).isEmpty();
	}

}
