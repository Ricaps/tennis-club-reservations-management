package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.peristence.dao.definition.CrudDao;
import com.github.ricaps.tennis_club.peristence.entity.IdentifiedEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
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
	public EntityType save(EntityType entity) {
		if (entity == null) {
			return null;
		}

		entityManager.persist(entity);
		return entity;
	}

	@Override
	@Transactional
	public void saveAll(Collection<EntityType> entities) {
		if (entities == null) {
			return;
		}

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
	public EntityType update(EntityType entity) {
		if (entity == null) {
			return null;
		}

		return entityManager.merge(entity);
	}

	@Override
	public boolean delete(UUID uuid) {
		if (uuid == null) {
			return false;
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaDelete<EntityType> deleteCriteria = criteriaBuilder.createCriteriaDelete(getEntityClass());
		Root<EntityType> root = deleteCriteria.from(getEntityClass());

		deleteCriteria.where(criteriaBuilder.equal(root.get("uid"), uuid));

		int deleted = entityManager.createQuery(deleteCriteria).executeUpdate();

		return deleted == 1;
	}

	@Override
	public Optional<EntityType> findById(UUID uuid) {
		if (uuid == null) {
			return Optional.empty();
		}

		return Optional.ofNullable(entityManager.find(getEntityClass(), uuid));
	}

	@Override
	public boolean existsById(UUID uuid) {
		if (uuid == null) {
			return false;
		}

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
