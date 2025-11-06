package com.github.ricaps.tennis_club.business.facade;

import com.github.ricaps.tennis_club.api.user.UserCreateDto;
import com.github.ricaps.tennis_club.api.user.UserViewDto;
import com.github.ricaps.tennis_club.business.mapping.UserMapper;
import com.github.ricaps.tennis_club.business.service.definition.UserService;
import com.github.ricaps.tennis_club.exception.ValueIsMissingException;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.test_utils.UserTestData;
import com.github.ricaps.tennis_club.utils.UUIDUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class UserFacadeTest {

	private final UserMapper userMapper = Mockito.spy(Mappers.getMapperClass(UserMapper.class));

	@Mock
	private UserService userService;

	@InjectMocks
	private UserFacade userFacade;

	@Test
	void create_nullEntity_throwsException() {
		assertThatThrownBy(() -> userFacade.create(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userService, Mockito.never()).create(Mockito.any());
	}

	@Test
	void create_allPropertiesGood_creationSuccessful() {
		UserCreateDto createDto = UserTestData.createUser(true);
		User entity = Mockito.spy(userMapper.fromCreateToEntity(createDto));
		Mockito.reset(userMapper);

		Mockito.when(userMapper.fromCreateToEntity(createDto)).thenReturn(entity);
		Mockito.when(userService.create(Mockito.any())).thenReturn(entity);

		UserViewDto view = userFacade.create(createDto);

		UserTestData.compareViewAndCreate(view, createDto);
		Mockito.verify(entity, Mockito.times(1)).setUid(Mockito.any());
		Mockito.verify(userService, Mockito.times(1)).create(entity);
	}

	@Test
	void get_uidNull_throwsException() {
		assertThatThrownBy(() -> userFacade.get(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userService, Mockito.never()).get(Mockito.any());
	}

	@Test
	void get_allGood_returnsOptionalValue() {
		UserViewDto view = UserTestData.viewUser(UUID.randomUUID());
		User entity = Mockito.mock(User.class);
		Mockito.when(userService.get(view.uid())).thenReturn(Optional.of(entity));
		Mockito.when(userMapper.fromEntityToView(entity)).thenReturn(view);

		Optional<UserViewDto> returnedView = userFacade.get(view.uid());

		assertThat(returnedView).isPresent().get().isEqualTo(view);
		Mockito.verify(userService, Mockito.times(1)).get(view.uid());
	}

	@Test
	void get_notFound_returnsEmptyValue() {
		UserViewDto view = UserTestData.viewUser(UUID.randomUUID());
		Mockito.when(userService.get(view.uid())).thenReturn(Optional.empty());

		Optional<UserViewDto> returnedView = userFacade.get(view.uid());

		assertThat(returnedView).isEmpty();
		Mockito.verify(userMapper, Mockito.never()).fromEntityToView(Mockito.any());
		Mockito.verify(userService, Mockito.times(1)).get(view.uid());
	}

	@Test
	void getAll_nullPageable_throwsException() {
		assertThatThrownBy(() -> userFacade.getAll(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userService, Mockito.never()).getAll(Mockito.any());
	}

	@Test
	void getAll_allGood_returnsPage() {
		UserViewDto view1 = UserTestData.viewUser(UUID.randomUUID());
		UserViewDto view2 = UserTestData.viewUser(UUID.randomUUID());
		List<UserViewDto> viewList = List.of(view1, view2);

		Pageable pageableMock = Mockito.mock(Pageable.class);

		User entity1 = Mockito.mock(User.class);
		User entity2 = Mockito.mock(User.class);
		List<User> entitiesList = List.of(entity1, entity2);
		Mockito.when(userService.getAll(pageableMock)).thenReturn(entitiesList);
		Mockito.when(userMapper.fromEntityListToView(entitiesList)).thenReturn(viewList);

		PagedModel<UserViewDto> returnedView = userFacade.getAll(pageableMock);

		assertThat(returnedView.getContent()).hasSize(2);
		assertThat(returnedView.getContent()).containsAll(viewList);
		Mockito.verify(userService, Mockito.times(1)).getAll(pageableMock);
	}

	@Test
	void update_nullEntity_throwsException() {
		assertThatThrownBy(() -> userFacade.update(UUID.randomUUID(), null))
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userService, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_nullUuid_throwsException() {
		assertThatThrownBy(() -> userFacade.update(null, UserTestData.createUser(true)))
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userService, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_allPropertiesGood_updateSuccessful() {
		UserCreateDto createDto = UserTestData.createUser(true);
		UUID uuid = UUIDUtils.generate();
		User entity = Mockito.spy(userMapper.fromCreateToEntity(uuid, createDto));

		User updatedEntity = Mockito.spy(userMapper.fromCreateToEntity(uuid, createDto));
		updatedEntity.setFirstName("New name");
		Mockito.reset(userMapper);

		Mockito.when(userMapper.fromCreateToEntity(uuid, createDto)).thenReturn(entity);
		Mockito.when(userService.update(entity)).thenReturn(updatedEntity);

		UserViewDto view = userFacade.update(uuid, createDto);

		assertThat(view.uid()).isEqualTo(uuid);
		assertThat(view.firstName()).isEqualTo(updatedEntity.getFirstName());
		Mockito.verify(userService, Mockito.times(1)).update(entity);
	}

	@Test
	void delete_nullUuid_throwsException() {
		assertThatThrownBy(() -> userFacade.delete(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userService, Mockito.never()).delete(Mockito.any());
	}

	@Test
	void delete_correctValue_deleted() {
		UUID uuid = UUIDUtils.generate();
		userFacade.delete(uuid);

		Mockito.verify(userService, Mockito.times(1)).delete(uuid);
	}

}