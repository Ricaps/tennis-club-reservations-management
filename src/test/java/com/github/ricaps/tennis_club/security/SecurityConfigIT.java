package com.github.ricaps.tennis_club.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ricaps.tennis_club.api.reservation.ReservationCreateDto;
import com.github.ricaps.tennis_club.api.surface.SurfaceCreateDto;
import com.github.ricaps.tennis_club.api.user.UserCreateDto;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.Role;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import com.github.ricaps.tennis_club.test_utils.CourtTestData;
import com.github.ricaps.tennis_club.test_utils.ReservationTestData;
import com.github.ricaps.tennis_club.test_utils.SecuritySupport;
import com.github.ricaps.tennis_club.test_utils.SurfaceTestData;
import com.github.ricaps.tennis_club.test_utils.UserTestData;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
		properties = { "database.seed=true", "spring.datasource.url=jdbc:h2:mem:security-test;DB_CLOSE_DELAY=-1" })
@AutoConfigureMockMvc
@Transactional
class SecurityConfigIT {

	SecuritySupport.TestSecurityContext mockMvcAdmin;

	SecuritySupport.TestSecurityContext mockMvcUser;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	SecuritySupport securitySupport;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	EntityManager entityManager;

	@Autowired
	ReservationTestData reservationTestData;

	@BeforeEach
	void setup() {
		mockMvcAdmin = securitySupport.defineUserAndGetMvc(true, Set.of(Role.ADMIN, Role.USER));
		mockMvcUser = securitySupport.defineUserAndGetMvc(true, Set.of(Role.USER));
	}

	@Test
	void roleUser_canPerformGetOperation() throws Exception {
		mockMvcUser.mockMvc()
			.perform(get("/v1/court"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.size()").value(greaterThan(0)));
	}

	@Test
	void anonymous_canRegister() throws Exception {
		UserCreateDto userCreateDto = UserTestData.createUser(true);

		mockMvc
			.perform(post("/v1/auth/register").with(anonymous())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userCreateDto)))
			.andExpect(status().isCreated());
	}

	@Test
	void roleUser_cannotDoPost() throws Exception {
		SurfaceCreateDto surfaceCreateDto = SurfaceTestData.createSurfaceCreate();

		mockMvcUser.mockMvc()
			.perform(post("/v1/surface").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(surfaceCreateDto)))
			.andExpect(status().isForbidden());
	}

	@Test
	void roleUser_canCreateReservation() throws Exception {
		Surface surface = SurfaceTestData.createSurface();
		Court court = CourtTestData.entity(surface);
		entityManager.persist(surface);
		entityManager.persist(court);

		ReservationCreateDto createDto = reservationTestData.createReservation(court.getUid());

		mockMvcUser.mockMvc()
			.perform(post("/v1/reservation").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isCreated());
	}

	@Test
	void roleAdmin_canDoPost() throws Exception {
		SurfaceCreateDto surfaceCreateDto = SurfaceTestData.createSurfaceCreate();

		mockMvcAdmin.mockMvc()
			.perform(post("/v1/surface").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(surfaceCreateDto)))
			.andExpect(status().isCreated());
	}

}