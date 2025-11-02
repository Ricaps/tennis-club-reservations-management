package com.github.ricaps.tennis_club.business.facade;

import com.github.ricaps.tennis_club.api.surface.SurfaceCreateDto;
import com.github.ricaps.tennis_club.api.surface.SurfaceViewDto;
import com.github.ricaps.tennis_club.business.mapping.SurfaceMapper;
import com.github.ricaps.tennis_club.business.service.definition.SurfaceService;
import com.github.ricaps.tennis_club.exception.ValueIsMissingException;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import com.github.ricaps.tennis_club.test_utils.TestData;
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
class SurfaceFacadeTest {

	private final SurfaceMapper surfaceMapper = Mockito.spy(Mappers.getMapperClass(SurfaceMapper.class));

	@Mock
	private SurfaceService surfaceService;

	@InjectMocks
	private SurfaceFacade surfaceFacade;

	private static void compareViewAndCreate(SurfaceViewDto surfaceViewDto, SurfaceCreateDto createDto) {
		assertThat(surfaceViewDto.uid()).isNotNull();
		assertThat(surfaceViewDto.name()).isEqualTo(createDto.name());
		assertThat(surfaceViewDto.currency()).isEqualTo(createDto.currency());
		assertThat(surfaceViewDto.price()).isEqualTo(createDto.price());
	}

	@Test
	void create_nullEntity_throwsException() {
		assertThatThrownBy(() -> surfaceFacade.create(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(surfaceService, Mockito.never()).create(Mockito.any());
	}

	@Test
	void create_allPropertiesGood_creationSuccessful() {
		SurfaceCreateDto createDto = TestData.createSurfaceCreate();
		Surface entity = Mockito.spy(surfaceMapper.fromCreateToEntity(createDto));
		Mockito.reset(surfaceMapper);

		Mockito.when(surfaceMapper.fromCreateToEntity(createDto)).thenReturn(entity);
		Mockito.when(surfaceService.create(Mockito.any())).thenReturn(entity);

		SurfaceViewDto view = surfaceFacade.create(createDto);

		compareViewAndCreate(view, createDto);
		Mockito.verify(entity, Mockito.times(1)).setUid(Mockito.any());
		Mockito.verify(surfaceMapper, Mockito.times(1)).fromCreateToEntity(createDto);
		Mockito.verify(surfaceService, Mockito.times(1)).create(entity);
	}

	@Test
	void get_uidNull_throwsException() {
		assertThatThrownBy(() -> surfaceFacade.get(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(surfaceService, Mockito.never()).get(Mockito.any());
	}

	@Test
	void get_allGood_returnsOptionalValue() {
		SurfaceViewDto view = TestData.createSurfaceView(UUID.randomUUID());
		Surface entity = Mockito.mock(Surface.class);
		Mockito.when(surfaceService.get(view.uid())).thenReturn(Optional.of(entity));
		Mockito.when(surfaceMapper.fromEntityToView(entity)).thenReturn(view);

		Optional<SurfaceViewDto> returnedView = surfaceFacade.get(view.uid());

		assertThat(returnedView).isPresent().get().isEqualTo(view);
		Mockito.verify(surfaceService, Mockito.times(1)).get(view.uid());
	}

	@Test
	void get_notFound_returnsEmptyValue() {
		SurfaceViewDto view = TestData.createSurfaceView(UUID.randomUUID());
		Mockito.when(surfaceService.get(view.uid())).thenReturn(Optional.empty());

		Optional<SurfaceViewDto> returnedView = surfaceFacade.get(view.uid());

		assertThat(returnedView).isEmpty();
		Mockito.verify(surfaceMapper, Mockito.never()).fromEntityToView(Mockito.any());
		Mockito.verify(surfaceService, Mockito.times(1)).get(view.uid());
	}

	@Test
	void getAll_nullPageable_throwsException() {
		assertThatThrownBy(() -> surfaceFacade.getAll(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(surfaceService, Mockito.never()).getAll(Mockito.any());
	}

	@Test
	void getAll_allGood_returnsPage() {
		SurfaceViewDto view1 = TestData.createSurfaceView(UUID.randomUUID());
		SurfaceViewDto view2 = TestData.createSurfaceView(UUID.randomUUID());
		List<SurfaceViewDto> viewList = List.of(view1, view2);

		Pageable pageableMock = Mockito.mock(Pageable.class);

		Surface entity1 = Mockito.mock(Surface.class);
		Surface entity2 = Mockito.mock(Surface.class);
		List<Surface> entitiesList = List.of(entity1, entity2);
		Mockito.when(surfaceService.getAll(pageableMock)).thenReturn(entitiesList);
		Mockito.when(surfaceMapper.fromEntityListToView(entitiesList)).thenReturn(viewList);

		PagedModel<SurfaceViewDto> returnedView = surfaceFacade.getAll(pageableMock);

		assertThat(returnedView.getContent()).hasSize(2);
		assertThat(returnedView.getContent()).containsAll(viewList);
		Mockito.verify(surfaceService, Mockito.times(1)).getAll(pageableMock);
	}

	@Test
	void update_nullEntity_throwsException() {
		assertThatThrownBy(() -> surfaceFacade.update(UUID.randomUUID(), null))
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(surfaceService, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_nullUuid_throwsException() {
		assertThatThrownBy(() -> surfaceFacade.update(null, TestData.createSurfaceCreate()))
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(surfaceService, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_allPropertiesGood_updateSuccessful() {
		SurfaceCreateDto createDto = TestData.createSurfaceCreate();
		UUID uuid = UUIDUtils.generate();
		Surface entity = Mockito.spy(surfaceMapper.fromCreateToEntity(uuid, createDto));

		Surface updatedEntity = Mockito.spy(surfaceMapper.fromCreateToEntity(uuid, createDto));
		updatedEntity.setName("New name");
		Mockito.reset(surfaceMapper);

		Mockito.when(surfaceMapper.fromCreateToEntity(uuid, createDto)).thenReturn(entity);
		Mockito.when(surfaceService.update(entity)).thenReturn(updatedEntity);

		SurfaceViewDto view = surfaceFacade.update(uuid, createDto);

		assertThat(view.uid()).isEqualTo(uuid);
		assertThat(view.name()).isEqualTo(updatedEntity.getName());
		Mockito.verify(surfaceMapper, Mockito.times(1)).fromCreateToEntity(uuid, createDto);
		Mockito.verify(surfaceService, Mockito.times(1)).update(entity);
	}

	@Test
	void delete_nullUuid_throwsException() {
		assertThatThrownBy(() -> surfaceFacade.delete(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(surfaceService, Mockito.never()).delete(Mockito.any());
	}

	@Test
	void delete_correctValue_deleted() {
		UUID uuid = UUIDUtils.generate();
		surfaceFacade.delete(uuid);

		Mockito.verify(surfaceService, Mockito.times(1)).delete(uuid);
	}

}