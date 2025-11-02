package com.github.ricaps.tennis_club.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ricaps.tennis_club.api.surface.SurfaceCreateDto;
import com.github.ricaps.tennis_club.api.surface.SurfaceViewDto;
import com.github.ricaps.tennis_club.business.facade.SurfaceFacade;
import com.github.ricaps.tennis_club.peristence.dao.definition.SurfaceDao;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
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

@SpringBootTest(properties = { "database.seed=false" })
@AutoConfigureMockMvc
@Transactional
class SurfaceControllerIT {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	SurfaceDao surfaceDao;

	@MockitoSpyBean
	SurfaceFacade surfaceFacade;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	void create_correctCreation_returnsData() throws Exception {
		SurfaceCreateDto createDto = SurfaceTestData.createSurfaceCreate();

		String response = mockMvc
			.perform(post("/v1/surface").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsBytes(createDto)))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		SurfaceViewDto returnedView = objectMapper.readValue(response, SurfaceViewDto.class);
		SurfaceTestData.compareViewAndCreate(returnedView, createDto);
	}

	@Test
	void create_invalidInput_returns400() throws Exception {
		SurfaceCreateDto createDto = SurfaceTestData.createInvalidSurface();

		String response = mockMvc
			.perform(post("/v1/surface").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsBytes(createDto)))
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();

		assertThat(response).isEmpty();
	}

	@Test
	void get_notExist_returns404() throws Exception {
		UUID uuid = UUIDUtils.generate();
		String response = mockMvc.perform(get("/v1/surface/{uuid}", uuid))
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Mockito.verify(surfaceFacade, Mockito.times(1)).get(uuid);
		assertThat(response).isEmpty();

	}

	@Test
	void get_existing_returnsData() throws Exception {
		Surface entity = SurfaceTestData.createSurface();
		surfaceDao.save(entity);

		String response = mockMvc.perform(get("/v1/surface/{uuid}", entity.getUid()))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		SurfaceViewDto returnedView = objectMapper.readValue(response, SurfaceViewDto.class);
		SurfaceTestData.compareViewAndEntity(returnedView, entity);
	}

	@Test
	void getAll_noData_returnsEmpty() throws Exception {
		mockMvc.perform(get("/v1/surface").queryParam("page", "0").queryParam("size", "20"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isEmpty());
	}

	@Test
	void getAll_data_returnedAll() throws Exception {
		Surface entity1 = SurfaceTestData.createSurface();
		Surface entity2 = SurfaceTestData.createSurface();

		List<Surface> entities = List.of(entity1, entity2);
		surfaceDao.saveAll(entities);

		mockMvc.perform(get("/v1/surface").queryParam("page", "0").queryParam("size", "20"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.page.size").value(20))
			.andExpect(jsonPath("$.page.totalElements").value(entities.size()))
			.andExpect(jsonPath("$.page.totalPages").value(1));
	}

	@Test
	void update_notExisting_returns404() throws Exception {
		UUID uuid = UUIDUtils.generate();
		SurfaceCreateDto createDto = SurfaceTestData.createSurfaceCreate();
		String response = mockMvc
			.perform(put("/v1/surface/{uuid}", uuid).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Mockito.verify(surfaceFacade, Mockito.times(1)).update(uuid, createDto);
		assertThat(response).isEmpty();
	}

	@Test
	void update_existing_updated() throws Exception {
		Surface entity = SurfaceTestData.createSurface();
		surfaceDao.save(entity);
		SurfaceCreateDto createDto = SurfaceTestData.createSurfaceCreate();

		String response = mockMvc
			.perform(put("/v1/surface/{uuid}", entity.getUid()).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		SurfaceViewDto updatedView = objectMapper.readValue(response, SurfaceViewDto.class);
		SurfaceTestData.compareViewAndCreate(updatedView, createDto);
	}

	@Test
	void delete_notExisting_returns404() throws Exception {
		UUID uuid = UUIDUtils.generate();
		String response = mockMvc.perform(delete("/v1/surface/{uuid}", uuid))
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Mockito.verify(surfaceFacade, Mockito.times(1)).delete(uuid);
		assertThat(response).isEmpty();
	}

	@Test
	void delete_existing_successfullyDeleted() throws Exception {
		Surface entity = SurfaceTestData.createSurface();
		surfaceDao.save(entity);

		mockMvc.perform(delete("/v1/surface/{uuid}", entity.getUid())).andExpect(status().isNoContent());

		assertThat(surfaceDao.findById(entity.getUid())).isEmpty();
	}

}