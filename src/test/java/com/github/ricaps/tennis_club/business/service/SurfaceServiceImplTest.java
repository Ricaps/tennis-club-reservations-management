package com.github.ricaps.tennis_club.business.service;

import com.github.ricaps.tennis_club.exception.EntityExistsException;
import com.github.ricaps.tennis_club.exception.EntityNotExistsException;
import com.github.ricaps.tennis_club.exception.ValueIsMissingException;
import com.github.ricaps.tennis_club.peristence.dao.definition.SurfaceDao;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import com.github.ricaps.tennis_club.test_utils.MockUtils;
import com.github.ricaps.tennis_club.test_utils.SurfaceTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SurfaceServiceImplTest {

	@Mock
	private SurfaceDao surfaceDao;

	@InjectMocks
	private SurfaceServiceImpl surfaceService;

	@Test
	void create_nullEntity_throwsException() {
		assertThatThrownBy(() -> surfaceService.create(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(surfaceDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void create_nullUuid_throwsException() {
		Surface surface = SurfaceTestData.createSurface();
		surface.setUid(null);

		assertThatThrownBy(() -> surfaceService.create(surface)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(surfaceDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void create_entityExists_throwsException() {
		Surface surface = SurfaceTestData.createSurface();
		Mockito.when(surfaceDao.existsById(surface.getUid())).thenReturn(true);

		assertThatThrownBy(() -> surfaceService.create(surface)).isInstanceOf(EntityExistsException.class);
		Mockito.verify(surfaceDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void create_allConditionsCorrect_creationSuccessful() {
		Surface surface = SurfaceTestData.createSurface();
		Mockito.when(surfaceDao.existsById(surface.getUid())).thenReturn(false);
		Mockito.when(surfaceDao.save(surface)).thenReturn(surface);

		Surface result = surfaceService.create(surface);

		assertThat(result).isEqualTo(surface);
		Mockito.verify(surfaceDao, Mockito.times(1)).save(surface);
	}

	@Test
	void get_nullUid_throwsException() {
		assertThatThrownBy(() -> surfaceService.get(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(surfaceDao, Mockito.never()).findById(Mockito.any());
	}

	@Test
	void get_notFoundEntity_returnsEmptyOptional() {
		Surface surface = SurfaceTestData.createSurface();
		Mockito.when(surfaceDao.findById(surface.getUid())).thenReturn(Optional.empty());

		Optional<Surface> result = surfaceService.get(surface.getUid());

		assertThat(result).isEmpty();
		Mockito.verify(surfaceDao, Mockito.times(1)).findById(surface.getUid());
	}

	@Test
	void get_foundEntity_returnsPresentOptional() {
		Surface surface = SurfaceTestData.createSurface();
		Mockito.when(surfaceDao.findById(surface.getUid())).thenReturn(Optional.of(surface));

		Optional<Surface> result = surfaceService.get(surface.getUid());

		assertThat(result).isPresent().get().isEqualTo(surface);
		Mockito.verify(surfaceDao, Mockito.times(1)).findById(surface.getUid());
	}

	@Test
	void getAll_nullPageable_throwsException() {
		assertThatThrownBy(() -> surfaceService.getAll(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(surfaceDao, Mockito.never()).findAll(Mockito.anyInt(), Mockito.anyInt(), Mockito.any());
	}

	@Test
	void getAll_correctInput_returnsMultipleEntities() {
		Surface surface1 = SurfaceTestData.createSurface();
		Surface surface2 = SurfaceTestData.createSurface();

		int pageNumber = 1;
		int pageSize = 10;
		Sort sortBy = Sort.by("uid");

		Pageable pageableMock = MockUtils.mockPageable(pageNumber, pageSize, sortBy);

		Mockito.when(surfaceDao.findAll(pageNumber, pageSize, sortBy)).thenReturn(List.of(surface1, surface2));

		List<Surface> result = surfaceService.getAll(pageableMock);

		assertThat(result).hasSize(2);
		Mockito.verify(surfaceDao, Mockito.times(1)).findAll(pageNumber, pageSize, sortBy);
	}

	@Test
	void update_nullEntity_throwsException() {
		assertThatThrownBy(() -> surfaceService.update(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(surfaceDao, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_nullUuid_throwsException() {
		Surface surface = SurfaceTestData.createSurface();
		surface.setUid(null);

		assertThatThrownBy(() -> surfaceService.update(surface)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(surfaceDao, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_entityNotExists_throwsException() {
		Surface surface = SurfaceTestData.createSurface();
		Mockito.when(surfaceDao.existsById(surface.getUid())).thenReturn(false);

		assertThatThrownBy(() -> surfaceService.update(surface)).isInstanceOf(EntityNotExistsException.class);
		Mockito.verify(surfaceDao, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_allConditionsCorrect_updateSuccessful() {
		Surface surface = SurfaceTestData.createSurface();
		Mockito.when(surfaceDao.existsById(surface.getUid())).thenReturn(true);
		Mockito.when(surfaceDao.update(surface)).thenReturn(surface);

		Surface result = surfaceService.update(surface);

		assertThat(result).isEqualTo(surface);
		Mockito.verify(surfaceDao, Mockito.times(1)).update(surface);
	}

	@Test
	void delete_nullUuid_throwsException() {
		assertThatThrownBy(() -> surfaceService.delete(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(surfaceDao, Mockito.never()).delete(Mockito.any());
	}

	@Test
	void delete_entityNotExists_throwsException() {
		Surface surface = SurfaceTestData.createSurface();
		Mockito.when(surfaceDao.existsById(surface.getUid())).thenReturn(false);

		assertThatThrownBy(() -> surfaceService.delete(surface.getUid())).isInstanceOf(EntityNotExistsException.class);
		Mockito.verify(surfaceDao, Mockito.never()).delete(Mockito.any());
	}

	@Test
	void delete_allConditionsCorrect_deleteSuccessful() {
		Surface surface = SurfaceTestData.createSurface();
		Mockito.when(surfaceDao.existsById(surface.getUid())).thenReturn(true);

		surfaceService.delete(surface.getUid());

		Mockito.verify(surfaceDao, Mockito.times(1)).delete(surface.getUid());
	}

	@Test
	void count_returnsCount() {
		long countRef = 5L;
		Mockito.when(surfaceDao.count()).thenReturn(countRef);

		long returned = surfaceService.count();

		assertThat(returned).isEqualTo(countRef);
		Mockito.verify(surfaceDao, Mockito.times(1)).count();
	}

}