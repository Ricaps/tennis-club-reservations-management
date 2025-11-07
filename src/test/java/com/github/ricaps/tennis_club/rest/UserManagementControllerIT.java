package com.github.ricaps.tennis_club.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ricaps.tennis_club.api.user.UserCreateDto;
import com.github.ricaps.tennis_club.api.user.UserDetailedView;
import com.github.ricaps.tennis_club.business.facade.UserFacade;
import com.github.ricaps.tennis_club.peristence.dao.definition.UserDao;
import com.github.ricaps.tennis_club.peristence.entity.Role;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.test_utils.SecuritySupport;
import com.github.ricaps.tennis_club.test_utils.UserTestData;
import com.github.ricaps.tennis_club.utils.UUIDUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class UserManagementControllerIT {

	MockMvc mockMvc;

	@Autowired
	UserDao userDao;

	@MockitoSpyBean
	UserFacade userFacade;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	SecuritySupport securitySupport;

	@BeforeEach
	void setup() {
		mockMvc = securitySupport.createFakeAuthMvc(Set.of(Role.ADMIN, Role.USER));
	}

	@Test
	void create_correctCreation_returnsData() throws Exception {
		UserCreateDto createDto = UserTestData.createUser(true);

		String response = mockMvc
			.perform(post("/v1/user").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsBytes(createDto)))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		UserDetailedView returnedView = objectMapper.readValue(response, UserDetailedView.class);
		UserTestData.compareViewAndCreate(returnedView, createDto);
	}

	@Test
	void create_invalidInput_returns400() throws Exception {
		UserCreateDto createDto = UserTestData.createInvalid(true);

		String response = mockMvc
			.perform(post("/v1/user").contentType(MediaType.APPLICATION_JSON_VALUE)
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
		String response = mockMvc.perform(get("/v1/user/{uuid}", uuid))
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Mockito.verify(userFacade, Mockito.times(1)).get(uuid);
		assertThat(response).isEmpty();

	}

	@Test
	void get_existing_returnsData() throws Exception {
		User entity = UserTestData.entity();
		userDao.save(entity);

		String response = mockMvc.perform(get("/v1/user/{uuid}", entity.getUid()))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		UserDetailedView returnedView = objectMapper.readValue(response, UserDetailedView.class);
		UserTestData.compareViewAndEntity(returnedView, entity);
	}

	@Test
	void getAll_noData_returnsEmpty() throws Exception {
		mockMvc.perform(get("/v1/user").queryParam("page", "0").queryParam("size", "20"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isEmpty());
	}

	@Test
	void getAll_data_returnedAll() throws Exception {
		User entity1 = UserTestData.entity();
		User entity2 = UserTestData.entity();

		List<User> entities = List.of(entity1, entity2);
		userDao.saveAll(entities);

		mockMvc.perform(get("/v1/user").queryParam("page", "0").queryParam("size", "20"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.page.size").value(20))
			.andExpect(jsonPath("$.page.totalElements").value(entities.size()))
			.andExpect(jsonPath("$.page.totalPages").value(1));
	}

	@Test
	void update_notExisting_returns404() throws Exception {
		UUID uuid = UUIDUtils.generate();
		UserCreateDto createDto = UserTestData.createUser(true);
		String response = mockMvc
			.perform(put("/v1/user/{uuid}", uuid).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Mockito.verify(userFacade, Mockito.times(1)).update(uuid, createDto);
		assertThat(response).isEmpty();
	}

	@Test
	void update_existing_updated() throws Exception {
		User entity = UserTestData.entity();
		userDao.save(entity);
		UserCreateDto createDto = UserTestData.createUser(true);

		String response = mockMvc
			.perform(put("/v1/user/{uuid}", entity.getUid()).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		UserDetailedView updatedView = objectMapper.readValue(response, UserDetailedView.class);
		UserTestData.compareViewAndCreate(updatedView, createDto);
	}

	@Test
	void delete_notExisting_returns404() throws Exception {
		UUID uuid = UUIDUtils.generate();
		String response = mockMvc.perform(delete("/v1/user/{uuid}", uuid))
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Mockito.verify(userFacade, Mockito.times(1)).delete(uuid);
		assertThat(response).isEmpty();
	}

	@Test
	void delete_existing_successfullyDeleted() throws Exception {
		User entity = UserTestData.entity();
		userDao.save(entity);

		mockMvc.perform(delete("/v1/user/{uuid}", entity.getUid())).andExpect(status().isNoContent());

		assertThat(userDao.findById(entity.getUid())).isEmpty();
	}

}