package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.peristence.dao.definition.UserDao;
import com.github.ricaps.tennis_club.peristence.entity.Role;
import com.github.ricaps.tennis_club.peristence.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(UserDaoImpl.class)
class UserDaoImplTest extends AbstractDaoTest<User> {

	@Autowired
	protected UserDaoImplTest(UserDao userDao) {
		super(userDao);
	}

	@Override
	protected User createEntity() {
		return User.builder()
			.firstName("John")
			.familyName("Doe")
			.password("12345")
			.phoneNumber("777777777")
			.uid(UUID.randomUUID())
			.roles(new HashSet<>(List.of(Role.USER)))
			.build();
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

}