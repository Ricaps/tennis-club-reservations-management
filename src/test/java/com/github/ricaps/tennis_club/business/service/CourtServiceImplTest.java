package com.github.ricaps.tennis_club.business.service;

import com.github.ricaps.tennis_club.exception.EntityExistsException;
import com.github.ricaps.tennis_club.exception.EntityNotExistsException;
import com.github.ricaps.tennis_club.exception.ValueIsMissingException;
import com.github.ricaps.tennis_club.peristence.dao.definition.CourtDao;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.test_utils.CourtTestData;
import com.github.ricaps.tennis_club.test_utils.MockUtils;
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
class CourtServiceImplTest {

	@Mock
	private CourtDao courtDao;

	@InjectMocks
	private CourtServiceImpl courtService;

	@Test
	void create_nullEntity_throwsException() {
		assertThatThrownBy(() -> courtService.create(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(courtDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void create_nullUuid_throwsException() {
		Court court = CourtTestData.entity();
		court.setUid(null);

		assertThatThrownBy(() -> courtService.create(court)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(courtDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void create_entityExists_throwsException() {
		Court court = CourtTestData.entity();
		Mockito.when(courtDao.existsById(court.getUid())).thenReturn(true);

		assertThatThrownBy(() -> courtService.create(court)).isInstanceOf(EntityExistsException.class);
		Mockito.verify(courtDao, Mockito.never()).save(Mockito.any());
	}

	@Test
	void create_allConditionsCorrect_creationSuccessful() {
		Court court = CourtTestData.entity();
		Mockito.when(courtDao.existsById(court.getUid())).thenReturn(false);
		Mockito.when(courtDao.save(court)).thenReturn(court);

		Court result = courtService.create(court);

		assertThat(result).isEqualTo(court);
		Mockito.verify(courtDao, Mockito.times(1)).save(court);
	}

	@Test
	void get_nullUid_throwsException() {
		assertThatThrownBy(() -> courtService.get(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(courtDao, Mockito.never()).findById(Mockito.any());
	}

	@Test
	void get_notFoundEntity_returnsEmptyOptional() {
		Court court = CourtTestData.entity();
		Mockito.when(courtDao.findById(court.getUid())).thenReturn(Optional.empty());

		Optional<Court> result = courtService.get(court.getUid());

		assertThat(result).isEmpty();
		Mockito.verify(courtDao, Mockito.times(1)).findById(court.getUid());
	}

	@Test
	void get_foundEntity_returnsPresentOptional() {
		Court court = CourtTestData.entity();
		Mockito.when(courtDao.findById(court.getUid())).thenReturn(Optional.of(court));

		Optional<Court> result = courtService.get(court.getUid());

		assertThat(result).isPresent().get().isEqualTo(court);
		Mockito.verify(courtDao, Mockito.times(1)).findById(court.getUid());
	}

	@Test
	void getAll_nullPageable_throwsException() {
		assertThatThrownBy(() -> courtService.getAll(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(courtDao, Mockito.never()).findAll(Mockito.anyInt(), Mockito.anyInt(), Mockito.any());
	}

	@Test
	void getAll_correctInput_returnsMultipleEntities() {
		Court court1 = CourtTestData.entity();
		Court court2 = CourtTestData.entity();

		int pageNumber = 1;
		int pageSize = 10;
		Sort sortBy = Sort.by("uid");

		Pageable pageableMock = MockUtils.mockPageable(pageNumber, pageSize, sortBy);

		Mockito.when(courtDao.findAll(pageNumber, pageSize, sortBy)).thenReturn(List.of(court1, court2));

		List<Court> result = courtService.getAll(pageableMock);

		assertThat(result).hasSize(2);
		Mockito.verify(courtDao, Mockito.times(1)).findAll(pageNumber, pageSize, sortBy);
	}

	@Test
	void update_nullEntity_throwsException() {
		assertThatThrownBy(() -> courtService.update(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(courtDao, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_nullUuid_throwsException() {
		Court court = CourtTestData.entity();
		court.setUid(null);

		assertThatThrownBy(() -> courtService.update(court)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(courtDao, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_entityNotExists_throwsException() {
		Court court = CourtTestData.entity();
		Mockito.when(courtDao.existsById(court.getUid())).thenReturn(false);

		assertThatThrownBy(() -> courtService.update(court)).isInstanceOf(EntityNotExistsException.class);
		Mockito.verify(courtDao, Mockito.never()).update(Mockito.any());
	}

	@Test
	void update_allConditionsCorrect_updateSuccessful() {
		Court court = CourtTestData.entity();
		Mockito.when(courtDao.existsById(court.getUid())).thenReturn(true);
		Mockito.when(courtDao.update(court)).thenReturn(court);

		Court result = courtService.update(court);

		assertThat(result).isEqualTo(court);
		Mockito.verify(courtDao, Mockito.times(1)).update(court);
	}

	@Test
	void delete_nullUuid_throwsException() {
		assertThatThrownBy(() -> courtService.delete(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(courtDao, Mockito.never()).delete(Mockito.any());
	}

	@Test
	void delete_entityNotExists_throwsException() {
		Court court = CourtTestData.entity();
		Mockito.when(courtDao.existsById(court.getUid())).thenReturn(false);

		assertThatThrownBy(() -> courtService.delete(court.getUid())).isInstanceOf(EntityNotExistsException.class);
		Mockito.verify(courtDao, Mockito.never()).delete(Mockito.any());
	}

	@Test
	void delete_allConditionsCorrect_deleteSuccessful() {
		Court court = CourtTestData.entity();
		Mockito.when(courtDao.existsById(court.getUid())).thenReturn(true);

		courtService.delete(court.getUid());

		Mockito.verify(courtDao, Mockito.times(1)).delete(court.getUid());
	}

	@Test
	void count_returnsCount() {
		long countRef = 5L;
		Mockito.when(courtDao.count()).thenReturn(countRef);

		long returned = courtService.count();

		assertThat(returned).isEqualTo(countRef);
		Mockito.verify(courtDao, Mockito.times(1)).count();
	}

}