package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.peristence.dao.definition.UserDao;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.test_utils.UserTestData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(UserDaoImpl.class)
class UserDaoImplTest extends AbstractDaoTest<User> {

	private final UserDao userDao;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	protected UserDaoImplTest(UserDao userDao) {
		super(userDao);
		this.userDao = userDao;
	}

	@Override
	protected User createEntity() {
		return UserTestData.entity();
	}

	@Override
	protected void checkEntity(User actualEntity, User referenceEntity) {
		assertThat(actualEntity.getUid()).isEqualTo(referenceEntity.getUid());
		assertThat(actualEntity.getFirstName()).isEqualTo(referenceEntity.getFirstName());
		assertThat(actualEntity.getFamilyName()).isEqualTo(referenceEntity.getFamilyName());
		assertThat(actualEntity.getPassword()).isEqualTo(referenceEntity.getPassword());
		assertThat(actualEntity.getPhoneNumber()).isEqualTo(referenceEntity.getPhoneNumber());
		assertThat(actualEntity.getRoles()).isEqualTo(referenceEntity.getRoles());
	}

	@Override
	protected User updateEntity(User entity) {
		entity.setFirstName("George");

		return entity;
	}

	@Test
	@SuppressWarnings("unchecked")
	void findByPhone_nullPhoneNumber_returnsEmpty() {
		Optional<User> result = userDao.findByPhoneNumber(null);

		assertThat(result).isEmpty();
		Mockito.verify(entityManager, Mockito.never()).createQuery(Mockito.any(CriteriaQuery.class));
	}

	@Test
	void findByPhone_nonExistingPhone_returnEmpty() {
		Optional<User> result = userDao.findByPhoneNumber("123456");

		assertThat(result).isEmpty();
	}

	@Test
	void findByPhone_existingUser_returnsUser() {
		User user = UserTestData.entity();
		entityManager.persist(user);

		Optional<User> result = userDao.findByPhoneNumber(user.getPhoneNumber());

		assertThat(result).isPresent().get().isEqualTo(user);
	}

	@Test
	void saveUser_duplicatePhone_throwsException() {
		User user = UserTestData.entity();
		User user2 = UserTestData.entity();
		user2.setPhoneNumber(user.getPhoneNumber());

		entityManager.setFlushMode(FlushModeType.COMMIT);
		userDao.save(user);
		userDao.save(user2);

		assertThatThrownBy(() -> entityManager.flush()).isInstanceOf(ConstraintViolationException.class);

		List<User> users = userDao.findAll(0, 10, Sort.by("uid"));
		assertThat(users).hasSize(1);
	}

}