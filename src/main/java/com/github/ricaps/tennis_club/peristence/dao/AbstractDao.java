package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.peristence.dao.definition.CrudDao;
import com.github.ricaps.tennis_club.peristence.entity.IdentifiedEntity;
import com.github.ricaps.tennis_club.peristence.utils.PredicateProvider;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
public abstract class AbstractDao<EntityType extends IdentifiedEntity> implements CrudDao<EntityType> {

	private static final int BATCH_SIZE = 50;

	private final EntityManager entityManager;

	public AbstractDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	private List<Order> getOrderList(Sort sort, CriteriaBuilder criteriaBuilder, Root<EntityType> root) {
		return sort.get()
			.filter(order -> hasProperty(root, order.getProperty()))
			.map(order -> switch (order.getDirection()) {

				case ASC -> criteriaBuilder.asc(root.get(order.getProperty()));
				case DESC -> criteriaBuilder.desc(root.get(order.getProperty()));
			})
			.toList();
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

		entityManager.flush();
		return deleted == 1;
	}

	@Override
	public Optional<EntityType> findById(UUID uuid) {
		if (uuid == null) {
			return Optional.empty();
		}

		// EntityManager.find() doesn't respect soft delete. So use criteria.
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<EntityType> criteriaQuery = criteriaBuilder.createQuery(getEntityClass());
		Root<EntityType> root = criteriaQuery.from(getEntityClass());

		criteriaQuery.where(criteriaBuilder.equal(root.get("uid"), uuid));

		List<EntityType> entityList = entityManager.createQuery(criteriaQuery).getResultList();

		if (entityList.size() == 1) {
			return Optional.of(entityList.getFirst());
		}
		return Optional.empty();
	}

	@Override
	public EntityType findReferenceById(UUID uuid) {
		if (uuid == null) {
			return null;
		}

		return entityManager.getReference(getEntityClass(), uuid);
	}

	@Override
	public List<EntityType> findAll(int pageNumber, int pageSize, Sort sort) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<EntityType> criteriaQuery = criteriaBuilder.createQuery(getEntityClass());
		Root<EntityType> root = criteriaQuery.from(getEntityClass());

		TypedQuery<EntityType> typedQuery = applyPagingToQuery(pageNumber, pageSize, sort, criteriaBuilder, root,
				criteriaQuery);

		return typedQuery.getResultList();
	}

	protected TypedQuery<EntityType> applyPagingToQuery(int pageNumber, int pageSize, Sort sort,
			CriteriaBuilder criteriaBuilder, Root<EntityType> root, CriteriaQuery<EntityType> criteriaQuery) {
		List<Order> orders = getOrderList(sort, criteriaBuilder, root);

		criteriaQuery.orderBy(orders);
		TypedQuery<EntityType> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setMaxResults(pageSize);
		typedQuery.setFirstResult(pageNumber * pageSize);
		return typedQuery;
	}

	private boolean hasProperty(Root<EntityType> root, String property) {
		for (var attr : root.getModel().getAttributes()) {
			if (property.equals(attr.getName())) {
				return true;
			}
		}

		return false;
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
		return countWithPredicate(null);
	}

	private long countWithPredicate(@Nullable PredicateProvider<EntityType> predicateProvider) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cr = criteriaBuilder.createQuery(Long.class);
		Root<EntityType> root = cr.from(getEntityClass());

		cr.select(criteriaBuilder.count(root));

		if (predicateProvider != null) {
			cr.where(predicateProvider.createPredicate(criteriaBuilder, root));
		}

		Long rowCount = entityManager.createQuery(cr).getSingleResult();

		if (rowCount == null) {
			return 0;
		}

		return rowCount;
	}

	@Override
	public long count(PredicateProvider<EntityType> predicateProvider) {
		return countWithPredicate(predicateProvider);
	}

	protected abstract Class<EntityType> getEntityClass();

}
