package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.peristence.dao.definition.UserDao;
import com.github.ricaps.tennis_club.peristence.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl extends AbstractDao<User> implements UserDao {

	private final EntityManager entityManager;

	public UserDaoImpl(EntityManager entityManager) {
		super(entityManager);
		this.entityManager = entityManager;
	}

	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}

	@Override
	public Optional<User> findByPhoneNumber(String phoneNumber) {

		if (phoneNumber == null) {
			return Optional.empty();
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
		Root<User> root = query.from(User.class);

		query.where(criteriaBuilder.equal(root.get("phoneNumber"), phoneNumber));

		List<User> users = entityManager.createQuery(query).getResultList();

		if (users.isEmpty()) {
			return Optional.empty();
		}

		// users.size > 1 are enforced by the database

		return Optional.of(users.getFirst());
	}

}
