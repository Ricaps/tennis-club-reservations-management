package com.github.ricaps.tennis_club.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ricaps.tennis_club.api.court.CourtCreateDto;
import com.github.ricaps.tennis_club.api.court.CourtViewDto;
import com.github.ricaps.tennis_club.business.facade.CourtFacade;
import com.github.ricaps.tennis_club.peristence.dao.definition.CourtDao;
import com.github.ricaps.tennis_club.peristence.dao.definition.SurfaceDao;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import com.github.ricaps.tennis_club.test_utils.CourtTestData;
import com.github.ricaps.tennis_club.test_utils.SurfaceTestData;
import com.github.ricaps.tennis_club.utils.UUIDUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CourtControllerIT {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	CourtDao courtDao;

	@Autowired
	SurfaceDao surfaceDao;

	@MockitoSpyBean
	CourtFacade courtFacade;

	@Autowired
	ObjectMapper objectMapper;

	private Surface saveSurface() {
		Surface surface = SurfaceTestData.createSurface();
		surfaceDao.save(surface);

		return surface;
	}

	@Test
	void create_correctCreation_returnsData() throws Exception {
		Surface surface = saveSurface();
		CourtCreateDto createDto = CourtTestData.createCourt(surface.getUid());

		String response = mockMvc
			.perform(post("/v1/court").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsBytes(createDto)))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		CourtViewDto returnedView = objectMapper.readValue(response, CourtViewDto.class);
		CourtTestData.compareViewAndCreate(returnedView, createDto, surface);
	}

	@Test
	void create_invalidInput_returns400() throws Exception {
		Surface surface = saveSurface();
		CourtCreateDto createDto = CourtTestData.createInvalid(surface.getUid());

		String response = mockMvc
			.perform(post("/v1/court").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsBytes(createDto)))
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();

		assertThat(response).isEmpty();
	}

	@Test
	void create_nonExistingSurface_returns404() throws Exception {
		CourtCreateDto createDto = CourtTestData.createCourt(UUID.randomUUID());

		String response = mockMvc
			.perform(post("/v1/court").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsBytes(createDto)))
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		assertThat(response).isEmpty();
		assertThat(courtDao.count()).isEqualTo(0);
	}

	@Test
	void get_notExist_returns404() throws Exception {
		UUID uuid = UUIDUtils.generate();
		String response = mockMvc.perform(get("/v1/court/{uuid}", uuid))
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Mockito.verify(courtFacade, Mockito.times(1)).get(uuid);
		assertThat(response).isEmpty();

	}

	@Test
	void get_existing_returnsData() throws Exception {
		Surface surface = saveSurface();
		Court entity = CourtTestData.entity(surface);
		courtDao.save(entity);

		String response = mockMvc.perform(get("/v1/court/{uuid}", entity.getUid()))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		CourtViewDto returnedView = objectMapper.readValue(response, CourtViewDto.class);
		CourtTestData.compareViewAndEntity(returnedView, entity, surface);
	}

	@Test
	void getAll_noData_returnsEmpty() throws Exception {
		mockMvc.perform(get("/v1/court").queryParam("page", "0").queryParam("size", "20"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isEmpty());
	}

	@Test
	void getAll_data_returnedAll() throws Exception {
		Surface surface = saveSurface();

		Court entity1 = CourtTestData.entity(surface);
		Court entity2 = CourtTestData.entity(surface);

		List<Court> entities = List.of(entity1, entity2);
		courtDao.saveAll(entities);

		mockMvc.perform(get("/v1/court").queryParam("page", "0").queryParam("size", "20"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.page.size").value(20))
			.andExpect(jsonPath("$.page.totalElements").value(entities.size()))
			.andExpect(jsonPath("$.page.totalPages").value(1));
	}

	@Test
	void update_notExisting_returns404() throws Exception {
		UUID uuid = UUIDUtils.generate();
		Surface surface = saveSurface();
		CourtCreateDto createDto = CourtTestData.createCourt(surface.getUid());
		String response = mockMvc
			.perform(put("/v1/court/{uuid}", uuid).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Mockito.verify(courtFacade, Mockito.times(1)).update(uuid, createDto);
		assertThat(response).isEmpty();
	}

	@Test
	void update_existing_updated() throws Exception {
		Surface surface = saveSurface();
		Court entity = CourtTestData.entity(surface);
		courtDao.save(entity);
		CourtCreateDto createDto = CourtTestData.createCourt(surface.getUid());

		String response = mockMvc
			.perform(put("/v1/court/{uuid}", entity.getUid()).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		CourtViewDto updatedView = objectMapper.readValue(response, CourtViewDto.class);
		CourtTestData.compareViewAndCreate(updatedView, createDto, surface);
	}

	@Test
	void update_updateSurface_updated() throws Exception {
		Surface surface = saveSurface();
		Surface forUpdateSurface = saveSurface();
		Court entity = CourtTestData.entity(surface);
		courtDao.save(entity);

		CourtCreateDto forUpdateDto = CourtTestData.createCourt(forUpdateSurface.getUid());

		String response = mockMvc
			.perform(put("/v1/court/{uuid}", entity.getUid()).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(forUpdateDto)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		CourtViewDto updatedView = objectMapper.readValue(response, CourtViewDto.class);
		CourtTestData.compareViewAndCreate(updatedView, forUpdateDto, forUpdateSurface);
	}

	@Test
	void update_nonExistingSurface_returns404NoUpdate() throws Exception {
		Surface surface = saveSurface();
		Court entity = CourtTestData.entity(surface);
		courtDao.save(entity);

		UUID randomSurfaceUUID = UUIDUtils.generate();
		CourtCreateDto forUpdateDto = CourtTestData.createCourt(randomSurfaceUUID);

		String response = mockMvc
			.perform(put("/v1/court/{uuid}", entity.getUid()).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(forUpdateDto)))
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		assertThat(response).isEmpty();

		Court courtFromDB = courtDao.findById(entity.getUid()).orElseThrow();
		assertThat(courtFromDB).isEqualTo(entity);
	}

	@Test
	void delete_notExisting_returns404() throws Exception {
		UUID uuid = UUIDUtils.generate();
		String response = mockMvc.perform(delete("/v1/court/{uuid}", uuid))
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Mockito.verify(courtFacade, Mockito.times(1)).delete(uuid);
		assertThat(response).isEmpty();
	}

	@Test
	void delete_existing_successfullyDeleted() throws Exception {
		Surface surface = saveSurface();
		Court entity = CourtTestData.entity(surface);
		courtDao.save(entity);

		mockMvc.perform(delete("/v1/court/{uuid}", entity.getUid())).andExpect(status().isNoContent());

		assertThat(courtDao.findById(entity.getUid())).isEmpty();
	}

}