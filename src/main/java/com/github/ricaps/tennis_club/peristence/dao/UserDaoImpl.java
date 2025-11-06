package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.peristence.dao.definition.UserDao;
import com.github.ricaps.tennis_club.peristence.entity.User;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl extends AbstractDao<User> implements UserDao {

	public UserDaoImpl(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}

}
