package com.github.ricaps.tennis_club.business.facade;

import com.github.ricaps.tennis_club.api.court.CourtCreateDto;
import com.github.ricaps.tennis_club.api.court.CourtViewDto;
import com.github.ricaps.tennis_club.business.mapping.CourtMapper;
import com.github.ricaps.tennis_club.business.service.definition.CourtService;
import com.github.ricaps.tennis_club.business.service.definition.SurfaceService;
import com.github.ricaps.tennis_club.exception.ValueIsMissingException;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import com.github.ricaps.tennis_club.test_utils.CourtTestData;
import com.github.ricaps.tennis_club.test_utils.SurfaceTestData;
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
class CourtFacadeImplTest {

	private final CourtMapper courtMapper = Mockito.spy(Mappers.getMapperClass(CourtMapper.class));

	@Mock
	private CourtService courtService;

	@Mock
	private SurfaceService surfaceService;

	@InjectMocks
	private CourtFacadeImpl courtFacade;

	@Test
	void create_nullEntity_throwsException() {
		assertThatThrownBy(() -> courtFacade.create(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(courtService, Mockito.never()).create(Mockito.any());
	}

	@Test
	void create_allPropertiesGood_creationSuccessful() {
		CourtCreateDto createDto = CourtTestData.createCourt(UUID.randomUUID());
		Surface surface = SurfaceTestData.createSurface(createDto.surfaceUid());
		Court entity = Mockito.spy(courtMapper.fromCreateToEntity(createDto));
		entity.setSurface(surface);

		Mockito.reset(courtMapper);

		Mockito.when(surfaceService.getReference(surface.getUid())).thenReturn(surface);
		Mockito.when(courtMapper.fromCreateToEntity(createDto, surface)).thenReturn(entity);
		Mockito.when(courtService.create(Mockito.any())).thenReturn(entity);

		CourtViewDto view = courtFacade.create(createDto);

		CourtTestData.compareViewAndCreate(view, createDto, surface);
		Mockito.verify(entity, Mockito.times(1)).setUid(Mockito.any());
		Mockito.verify(courtMapper, Mockito.times(1)).fromCreateToEntity(createDto, surface);
		Mockito.verify(courtService, Mockito.times(1)).create(entity);
	}

	@Test
	void get_uidNull_throwsException() {
		assertThatThrownBy(() -> courtFacade.get(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(courtService, Mockito.never()).get(Mockito.any());
	}

	@Test
	void get_allGood_returnsOptionalValue() {
		CourtViewDto view = CourtTestData.viewCourt(UUID.randomUUID());
		Court entity = Mockito.mock(Court.class);
		Mockito.when(courtService.get(view.uid())).thenReturn(Optional.of(entity));
		Mockito.when(courtMapper.fromEntityToView(entity)).thenReturn(view);

		Optional<CourtViewDto> returnedView = courtFacade.get(view.uid());

		assertThat(returnedView).isPresent().get().isEqualTo(view);
		Mockito.verify(courtService, Mockito.times(1)).get(view.uid());
	}

	@Test
	void get_notFound_returnsEmptyValue() {
		CourtViewDto view = CourtTestData.viewCourt(UUID.randomUUID());
		Mockito.when(courtService.get(view.uid())).thenReturn(Optional.empty());

		Optional<CourtViewDto> returnedView = courtFacade.get(view.uid());

		assertThat(returnedView).isEmpty();
		Mockito.verify(courtMapper, Mockito.never()).fromEntityToView(Mockito.any());
		Mockito.verify(courtService, Mockito.times(1)).get(view.uid());
	}

	@Test
	void getAll_nullPageable_throwsException() {
		assertThatThrownBy(() -> courtFacade.getAll(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(courtService, Mockito.never()).getAll(Mockito.any());
	}

	@Test
	void getAll_allGood_returnsPage() {
		CourtViewDto view1 = CourtTestData.viewCourt(UUID.randomUUID());
		CourtViewDto view2 = CourtTestData.viewCourt(UUID.randomUUID());
		List<CourtViewDto> viewList = List.of(view1, view2);

		Pageable pageableMock = Mockito.mock(Pageable.class);

		Court entity1 = Mockito.mock(Court.class);
		Court entity2 = Mockito.mock(Court.class);
		List<Court> entitiesList = List.of(entity1, entity2);
		Mockito.when(courtService.getAll(pageableMock)).thenReturn(entitiesList);
		Mockito.when(courtMapper.fromEntityListToView(entitiesList)).thenReturn(viewList);

		PagedModel<CourtViewDto> returnedView = courtFacade.getAll(pageableMock);

		assertThat(returnedView.getContent()).hasSize(2);
		assertThat(returnedView.getContent()).containsAll(viewList);
		Mockito.verify(courtService, Mockito.times(1)).getAll(pageableMock);
	}

	@Test
	void update_nullEntity_throwsException() {
		assertThatThrownBy(() -> courtFacade.update(UUID.randomUUID(), null))
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(courtService, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_nullUuid_throwsException() {
		assertThatThrownBy(() -> courtFacade.update(null, CourtTestData.createCourt(UUIDUtils.generate())))
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(courtService, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_allPropertiesGood_updateSuccessful() {
		// Prepare
		CourtCreateDto createDto = CourtTestData.createCourt(UUIDUtils.generate());
		UUID uuid = UUIDUtils.generate();
		Surface surfaceReference = SurfaceTestData.createSurface(createDto.surfaceUid());

		// Mock
		Court entity = Mockito.spy(courtMapper.fromCreateToEntity(uuid, createDto, surfaceReference));
		Court updatedEntity = Mockito.spy(courtMapper.fromCreateToEntity(uuid, createDto, surfaceReference));
		updatedEntity.setName("New name");
		Mockito.reset(courtMapper);

		Mockito.when(surfaceService.getReference(createDto.surfaceUid())).thenReturn(surfaceReference);
		Mockito.when(courtMapper.fromCreateToEntity(uuid, createDto, surfaceReference)).thenReturn(entity);
		Mockito.when(courtService.update(entity)).thenReturn(updatedEntity);

		// Run
		CourtViewDto view = courtFacade.update(uuid, createDto);

		// Asert
		assertThat(view.uid()).isEqualTo(uuid);
		assertThat(view.name()).isEqualTo(updatedEntity.getName());
		Mockito.verify(courtMapper, Mockito.times(1)).fromCreateToEntity(uuid, createDto, surfaceReference);
		Mockito.verify(courtService, Mockito.times(1)).update(entity);
	}

	@Test
	void delete_nullUuid_throwsException() {
		assertThatThrownBy(() -> courtFacade.delete(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(courtService, Mockito.never()).delete(Mockito.any());
	}

	@Test
	void delete_correctValue_deleted() {
		UUID uuid = UUIDUtils.generate();
		courtFacade.delete(uuid);

		Mockito.verify(courtService, Mockito.times(1)).delete(uuid);
	}

}