package com.github.ricaps.tennis_club.business.service;

import com.github.ricaps.tennis_club.exception.EntityExistsException;
import com.github.ricaps.tennis_club.exception.EntityNotExistsException;
import com.github.ricaps.tennis_club.exception.ValueIsMissingException;
import com.github.ricaps.tennis_club.peristence.dao.definition.UserDao;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.test_utils.MockUtils;
import com.github.ricaps.tennis_club.test_utils.UserTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserDao userDao;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	void create_nullEntity_throwsException() {
		assertThatThrownBy(() -> userService.create(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void create_nullUuid_throwsException() {
		User user = UserTestData.entity();
		user.setUid(null);

		assertThatThrownBy(() -> userService.create(user)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void create_entityExists_throwsException() {
		User user = UserTestData.entity();
		Mockito.when(userDao.existsById(user.getUid())).thenReturn(true);

		assertThatThrownBy(() -> userService.create(user)).isInstanceOf(EntityExistsException.class);
		Mockito.verify(userDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void create_allConditionsCorrect_creationSuccessful() {
		User user = UserTestData.entity();
		Mockito.when(userDao.existsById(user.getUid())).thenReturn(false);
		Mockito.when(userDao.save(user)).thenReturn(user);

		User result = userService.create(user);

		assertThat(result).isEqualTo(user);
		Mockito.verify(userDao, Mockito.times(1)).save(user);
	}

	@Test
	void create_nullRoles_throwsException() {
		User user = UserTestData.entity();
		user.setRoles(null);

		assertThatThrownBy(() -> userService.create(user)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void create_emptyRoles_throwsException() {
		User user = UserTestData.entity();
		user.setRoles(new HashSet<>());

		assertThatThrownBy(() -> userService.create(user)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void create_notUniquePhoneNumber_throwsException() {
		User user = UserTestData.entity();

		Mockito.when(userDao.findByPhoneNumber(user.getPhoneNumber())).thenReturn(Optional.of(UserTestData.entity()));
		assertThatThrownBy(() -> userService.create(user)).isInstanceOf(EntityExistsException.class);

		Mockito.verify(userDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void get_nullUid_throwsException() {
		assertThatThrownBy(() -> userService.get(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userDao, Mockito.never()).findById(Mockito.any());
	}

	@Test
	void get_notFoundEntity_returnsEmptyOptional() {
		User user = UserTestData.entity();
		Mockito.when(userDao.findById(user.getUid())).thenReturn(Optional.empty());

		Optional<User> result = userService.get(user.getUid());

		assertThat(result).isEmpty();
		Mockito.verify(userDao, Mockito.times(1)).findById(user.getUid());
	}

	@Test
	void get_foundEntity_returnsPresentOptional() {
		User user = UserTestData.entity();
		Mockito.when(userDao.findById(user.getUid())).thenReturn(Optional.of(user));

		Optional<User> result = userService.get(user.getUid());

		assertThat(result).isPresent().get().isEqualTo(user);
		Mockito.verify(userDao, Mockito.times(1)).findById(user.getUid());
	}

	@Test
	void getAll_nullPageable_throwsException() {
		assertThatThrownBy(() -> userService.getAll(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userDao, Mockito.never()).findAll(Mockito.anyInt(), Mockito.anyInt(), Mockito.any());
	}

	@Test
	void getAll_correctInput_returnsMultipleEntities() {
		User user1 = UserTestData.entity();
		User user2 = UserTestData.entity();

		int pageNumber = 1;
		int pageSize = 10;
		Sort sortBy = Sort.by("uid");

		Pageable pageableMock = MockUtils.mockPageable(pageNumber, pageSize, sortBy);

		Mockito.when(userDao.findAll(pageNumber, pageSize, sortBy)).thenReturn(List.of(user1, user2));

		List<User> result = userService.getAll(pageableMock);

		assertThat(result).hasSize(2);
		Mockito.verify(userDao, Mockito.times(1)).findAll(pageNumber, pageSize, sortBy);
	}

	@Test
	void update_nullEntity_throwsException() {
		assertThatThrownBy(() -> userService.update(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userDao, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_nullUuid_throwsException() {
		User user = UserTestData.entity();
		user.setUid(null);

		assertThatThrownBy(() -> userService.update(user)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userDao, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_entityNotExists_throwsException() {
		User user = UserTestData.entity();
		Mockito.when(userDao.existsById(user.getUid())).thenReturn(false);

		assertThatThrownBy(() -> userService.update(user)).isInstanceOf(EntityNotExistsException.class);
		Mockito.verify(userDao, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_allConditionsCorrect_updateSuccessful() {
		User user = UserTestData.entity();
		Mockito.when(userDao.existsById(user.getUid())).thenReturn(true);
		Mockito.when(userDao.update(user)).thenReturn(user);

		User result = userService.update(user);

		assertThat(result).isEqualTo(user);
		Mockito.verify(userDao, Mockito.times(1)).update(user);
	}

	@Test
	void update_nullRoles_throwsException() {
		User user = UserTestData.entity();
		user.setRoles(null);

		assertThatThrownBy(() -> userService.update(user)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userDao, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_emptyRoles_throwsException() {
		User user = UserTestData.entity();
		user.setRoles(new HashSet<>());

		assertThatThrownBy(() -> userService.update(user)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userDao, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_notUniquePhoneNumber_throwsException() {
		User user = UserTestData.entity();

		Mockito.when(userDao.findByPhoneNumber(user.getPhoneNumber())).thenReturn(Optional.of(UserTestData.entity()));
		assertThatThrownBy(() -> userService.update(user)).isInstanceOf(EntityExistsException.class);

		Mockito.verify(userDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void delete_nullUuid_throwsException() {
		assertThatThrownBy(() -> userService.delete(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userDao, Mockito.never()).delete(Mockito.any());
	}

	@Test
	void delete_entityNotExists_throwsException() {
		User user = UserTestData.entity();
		Mockito.when(userDao.existsById(user.getUid())).thenReturn(false);

		assertThatThrownBy(() -> userService.delete(user.getUid())).isInstanceOf(EntityNotExistsException.class);
		Mockito.verify(userDao, Mockito.never()).delete(Mockito.any());
	}

	@Test
	void delete_allConditionsCorrect_deleteSuccessful() {
		User user = UserTestData.entity();
		Mockito.when(userDao.existsById(user.getUid())).thenReturn(true);

		userService.delete(user.getUid());

		Mockito.verify(userDao, Mockito.times(1)).delete(user.getUid());
	}

	@Test
	void count_returnsCount() {
		long countRef = 5L;
		Mockito.when(userDao.count()).thenReturn(countRef);

		long returned = userService.count();

		assertThat(returned).isEqualTo(countRef);
		Mockito.verify(userDao, Mockito.times(1)).count();
	}

}