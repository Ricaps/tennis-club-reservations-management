package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.peristence.dao.definition.CrudDao;
import com.github.ricaps.tennis_club.peristence.entity.IdentifiedEntity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractDao<EntityType extends IdentifiedEntity> implements CrudDao<EntityType> {

	private static final int BATCH_SIZE = 50;

	private final EntityManager entityManager;

	public AbstractDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public EntityType save(@Nonnull EntityType entity) {
		entityManager.persist(entity);
		return entity;
	}

	@Override
	@Transactional
	public void saveAll(@Nonnull Collection<EntityType> entities) {
		int index = 0;
		for (EntityType entity : entities) {
			index++;
			entityManager.persist(entity);

			if (index % BATCH_SIZE == 0) {
				entityManager.flush();
				entityManager.clear();
			}
		}
	}

	@Override
	public EntityType update(@Nonnull EntityType entity) {
		return entityManager.merge(entity);
	}

	@Override
	public void delete(@Nonnull EntityType entity) {
		entityManager.remove(entity);
		entityManager.flush();
	}

	@Override
	public Optional<EntityType> findById(@Nonnull UUID uuid) {
		return Optional.ofNullable(entityManager.find(getEntityClass(), uuid));
	}

	@Override
	public boolean existsById(@Nonnull UUID uuid) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cr = criteriaBuilder.createQuery(Long.class);
		Root<EntityType> root = cr.from(getEntityClass());

		Predicate uidPredicate = criteriaBuilder.equal(root.get("uid"), uuid);
		cr.select(criteriaBuilder.count(root)).where(uidPredicate);

		Long rowCount = entityManager.createQuery(cr).getSingleResult();

		if (rowCount == null) {
			return false;
		}

		return rowCount > 0;
	}

	@Override
	public long count() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cr = criteriaBuilder.createQuery(Long.class);
		Root<EntityType> root = cr.from(getEntityClass());

		cr.select(criteriaBuilder.count(root));

		Long rowCount = entityManager.createQuery(cr).getSingleResult();

		if (rowCount == null) {
			return 0;
		}

		return rowCount;
	}

	protected abstract Class<EntityType> getEntityClass();

}
