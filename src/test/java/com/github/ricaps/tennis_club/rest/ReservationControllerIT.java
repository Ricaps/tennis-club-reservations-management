package com.github.ricaps.tennis_club.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ricaps.tennis_club.api.reservation.ReservationCreateDto;
import com.github.ricaps.tennis_club.api.reservation.ReservationViewDto;
import com.github.ricaps.tennis_club.api.shared.ErrorDto;
import com.github.ricaps.tennis_club.business.facade.definition.ReservationFacade;
import com.github.ricaps.tennis_club.peristence.dao.definition.ReservationDao;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.Reservation;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.test_utils.CourtTestData;
import com.github.ricaps.tennis_club.test_utils.ReservationTestData;
import com.github.ricaps.tennis_club.test_utils.SecuritySupport;
import com.github.ricaps.tennis_club.test_utils.SurfaceTestData;
import com.github.ricaps.tennis_club.test_utils.TimeConfig;
import com.github.ricaps.tennis_club.test_utils.UserTestData;
import com.github.ricaps.tennis_club.utils.UUIDUtils;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.github.ricaps.tennis_club.test_utils.AssertionUtils.assertError;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@Import(TimeConfig.class)
class ReservationControllerIT {

	MockMvc mockMvc;

	SecuritySupport.TestSecurityContext testSecurityContext;

	@Autowired
	EntityManager entityManager;

	@Autowired
	ReservationDao reservationDao;

	@MockitoSpyBean
	ReservationFacade reservationFacade;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	SecuritySupport securitySupport;

	@Autowired
	ReservationTestData reservationTestData;

	@Autowired
	private Clock clock;

	private static Stream<Arguments> getByCourtMethodData() {
		return Stream.of(Arguments.of("createdAt,DESC", "$.content[1]", "$.content[0]"),
				Arguments.of("createdAt,ASC", "$.content[0]", "$.content[1]"));
	}

	@BeforeEach
	void setup() {
		testSecurityContext = securitySupport.defineUserAndGetMvc(true);
		mockMvc = testSecurityContext.mockMvc();
	}

	private Court saveCourt() {
		Surface surface = SurfaceTestData.createSurface();
		entityManager.persist(surface);

		Court court = CourtTestData.entity(surface);
		entityManager.persist(court);

		return court;
	}

	@Test
	void create_correctCreation_returnsData() throws Exception {
		Court court = saveCourt();
		ReservationCreateDto createDto = reservationTestData.createReservation(court.getUid());

		String response = mockMvc
			.perform(post("/v1/reservation").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsBytes(createDto)))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		ReservationViewDto returnedView = objectMapper.readValue(response, ReservationViewDto.class);
		reservationTestData.compareViewAndCreate(returnedView, createDto, court, testSecurityContext.user());
	}

	@Test
	void create_timeframeConflict_returns409() throws Exception {
		Court court = saveCourt();
		Reservation reservation = reservationTestData.entity(court, testSecurityContext.user());
		entityManager.persist(reservation);

		ReservationCreateDto createDto = new ReservationCreateDto(court.getUid(), reservation.getFromTime(),
				reservation.getToTime().minusMinutes(30), false);

		String response = mockMvc
			.perform(post("/v1/reservation").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsBytes(createDto)))
			.andExpect(status().isConflict())
			.andReturn()
			.getResponse()
			.getContentAsString();

		ErrorDto returnedView = objectMapper.readValue(response, ErrorDto.class);
		assertError(returnedView, "There is already existing reservation for the specified time frame!",
				HttpStatus.CONFLICT, List.of());
	}

	@Test
	void create_invalidInput_returns400() throws Exception {
		Court court = saveCourt();
		ReservationCreateDto createDto = reservationTestData.createInvalid(court.getUid());

		String response = mockMvc
			.perform(post("/v1/reservation").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsBytes(createDto)))
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();

		ErrorDto error = objectMapper.readValue(response, ErrorDto.class);
		assertError(error, "Invalid request content.", HttpStatus.BAD_REQUEST,
				List.of(new ErrorDto.FieldError("fromTime", "must be a future date")));
	}

	@Test
	void create_nonExistingCourt_returns404() throws Exception {
		ReservationCreateDto createDto = reservationTestData.createReservation(UUID.randomUUID());

		String response = mockMvc
			.perform(post("/v1/reservation").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsBytes(createDto)))
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		ErrorDto error = objectMapper.readValue(response, ErrorDto.class);
		assertError(error, "Entity with ID %s doesn't exist!".formatted(createDto.courtUid()), HttpStatus.NOT_FOUND,
				List.of());
		assertThat(reservationDao.count()).isEqualTo(0);
	}

	@Test
	void create_userNotLoggedIn_returns401() throws Exception {
		ReservationCreateDto createDto = reservationTestData.createReservation(UUID.randomUUID());

		mockMvc
			.perform(post("/v1/reservation").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsBytes(createDto))
				.with(anonymous()))
			.andExpect(status().isUnauthorized());

		assertThat(reservationDao.count()).isEqualTo(0);
	}

	@Test
	void get_notExist_returns404() throws Exception {
		UUID uuid = UUIDUtils.generate();
		String response = mockMvc.perform(get("/v1/reservation/{uuid}", uuid))
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Mockito.verify(reservationFacade, Mockito.times(1)).get(uuid);
		assertThat(response).isEmpty();
	}

	@Test
	void get_existing_returnsData() throws Exception {
		Court court = saveCourt();
		Reservation entity = reservationTestData.entity(court, testSecurityContext.user());
		reservationDao.save(entity);

		String response = mockMvc.perform(get("/v1/reservation/{uuid}", entity.getUid()))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		ReservationViewDto returnedView = objectMapper.readValue(response, ReservationViewDto.class);
		reservationTestData.compareViewAndEntity(returnedView, entity);
	}

	@Test
	void getAll_noData_returnsEmpty() throws Exception {
		mockMvc.perform(get("/v1/reservation").queryParam("page", "0").queryParam("size", "20"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isEmpty());
	}

	@Test
	void getAll_data_returnedAll() throws Exception {
		Court court = saveCourt();

		Reservation entity1 = reservationTestData.entity(court, testSecurityContext.user());
		Reservation entity2 = reservationTestData.entity(court, testSecurityContext.user());

		List<Reservation> entities = List.of(entity1, entity2);
		reservationDao.saveAll(entities);

		mockMvc.perform(get("/v1/reservation").queryParam("page", "0").queryParam("size", "20"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.page.size").value(20))
			.andExpect(jsonPath("$.page.totalElements").value(entities.size()))
			.andExpect(jsonPath("$.page.totalPages").value(1));
	}

	@Test
	void update_notExisting_returns404() throws Exception {
		UUID uuid = UUIDUtils.generate();
		Court court = saveCourt();
		ReservationCreateDto createDto = reservationTestData.createReservation(court.getUid());
		String response = mockMvc
			.perform(put("/v1/reservation/{uuid}", uuid).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Mockito.verify(reservationFacade, Mockito.times(1)).update(uuid, createDto);

		ErrorDto error = objectMapper.readValue(response, ErrorDto.class);
		assertError(error, "Reservation with UID %s doesn't exist!".formatted(uuid), HttpStatus.NOT_FOUND, List.of());
	}

	@Test
	void update_existing_updated() throws Exception {
		Court court = saveCourt();
		Reservation entity = reservationTestData.entity(court, testSecurityContext.user());
		reservationDao.save(entity);
		ReservationCreateDto createDto = reservationTestData.createReservation(court.getUid());

		String response = mockMvc
			.perform(put("/v1/reservation/{uuid}", entity.getUid()).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		ReservationViewDto updatedView = objectMapper.readValue(response, ReservationViewDto.class);
		reservationTestData.compareViewAndCreate(updatedView, createDto, court, testSecurityContext.user());
	}

	@Test
	void update_updateCourt_updated() throws Exception {
		Court court = saveCourt();
		Court forUpdateCourt = saveCourt();
		Reservation entity = reservationTestData.entity(court, testSecurityContext.user());
		reservationDao.save(entity);

		ReservationCreateDto forUpdateDto = reservationTestData.createReservation(forUpdateCourt.getUid());

		String response = mockMvc
			.perform(put("/v1/reservation/{uuid}", entity.getUid()).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(forUpdateDto)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		ReservationViewDto updatedView = objectMapper.readValue(response, ReservationViewDto.class);
		reservationTestData.compareViewAndCreate(updatedView, forUpdateDto, forUpdateCourt, testSecurityContext.user());
	}

	@Test
	void update_nonExistingCourt_returns404NoUpdate() throws Exception {
		Court court = saveCourt();
		Reservation entity = reservationTestData.entity(court, testSecurityContext.user());
		reservationDao.save(entity);

		UUID randomCourtUUID = UUIDUtils.generate();
		ReservationCreateDto forUpdateDto = reservationTestData.createReservation(randomCourtUUID);

		String response = mockMvc
			.perform(put("/v1/reservation/{uuid}", entity.getUid()).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(forUpdateDto)))
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		ErrorDto error = objectMapper.readValue(response, ErrorDto.class);
		assertError(error, "Entity with ID %s doesn't exist!".formatted(randomCourtUUID), HttpStatus.NOT_FOUND,
				List.of());

		Reservation courtFromDB = reservationDao.findById(entity.getUid()).orElseThrow();
		assertThat(courtFromDB).isEqualTo(entity);
	}

	@Test
	void delete_notExisting_returns404() throws Exception {
		UUID uuid = UUIDUtils.generate();
		String response = mockMvc.perform(delete("/v1/reservation/{uuid}", uuid))
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Mockito.verify(reservationFacade, Mockito.times(1)).delete(uuid);
		ErrorDto error = objectMapper.readValue(response, ErrorDto.class);
		assertError(error, "Reservation with UID %s doesn't exist!".formatted(uuid), HttpStatus.NOT_FOUND, List.of());
	}

	@Test
	void delete_existing_successfullyDeleted() throws Exception {
		Court court = saveCourt();
		Reservation entity = reservationTestData.entity(court, testSecurityContext.user());
		reservationDao.save(entity);

		mockMvc.perform(delete("/v1/reservation/{uuid}", entity.getUid())).andExpect(status().isNoContent());

		assertThat(reservationDao.findById(entity.getUid())).isEmpty();
	}

	@ParameterizedTest
	@MethodSource("getByCourtMethodData")
	void getByCourt_sortAndFilterByCourt_filteredAndCorrectlyOrdered(String sortString, String entity1JsonPath,
			String entity2JsonPath) throws Exception {
		Court court = saveCourt();
		Court court2 = saveCourt();

		Reservation entity1Court1 = reservationTestData.entity(court, testSecurityContext.user());
		Reservation entity2Court1 = reservationTestData.entity(court, testSecurityContext.user());
		Reservation entity2 = reservationTestData.entity(court2, testSecurityContext.user());

		List<Reservation> entities = List.of(entity1Court1, entity2Court1, entity2);
		reservationDao.saveAll(entities);

		mockMvc
			.perform(get("/v1/reservation/court/{courtUID}", court.getUid()).queryParam("page", "0")
				.queryParam("size", "20")
				.queryParam("sort", sortString))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.page.size").value(20))
			.andExpect(jsonPath("$.page.totalElements").value(2))
			.andExpect(jsonPath("$.page.totalPages").value(1))
			.andExpect(jsonPath(entity1JsonPath + ".uid").value(entity1Court1.getUid().toString()))
			.andExpect(jsonPath(entity2JsonPath + ".uid").value(entity2Court1.getUid().toString()));
	}

	@Test
	void getByPhone_returnedData_filteredAndCorrectlyOrdered() throws Exception {
		Court court = saveCourt();
		User user1 = UserTestData.entity(true);
		User user2 = UserTestData.entity(true);
		entityManager.persist(user1);
		entityManager.persist(user2);

		Reservation entity1User1 = reservationTestData.entity(court, user1);
		Reservation entity2User1 = reservationTestData.entity(court, user1);
		Reservation entity3InPastUser1 = reservationTestData.entity(court, user1);
		entity3InPastUser1.setFromTime(OffsetDateTime.now(clock).minusDays(1));
		entity3InPastUser1.setToTime(OffsetDateTime.now(clock).minusHours(11));

		Reservation entity2 = reservationTestData.entity(court, user2);

		List<Reservation> entities = List.of(entity1User1, entity2User1, entity3InPastUser1, entity2);
		reservationDao.saveAll(entities);

		mockMvc
			.perform(get("/v1/reservation/user/{phoneNumber}", user1.getPhoneNumber()).queryParam("page", "0")
				.queryParam("size", "20")
				.queryParam("fromTime", OffsetDateTime.now(clock).toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.page.size").value(20))
			.andExpect(jsonPath("$.page.totalElements").value(2))
			.andExpect(jsonPath("$.page.totalPages").value(1))
			.andExpect(jsonPath("$.content[0].uid").value(entity1User1.getUid().toString()))
			.andExpect(jsonPath("$.content[1].uid").value(entity2User1.getUid().toString()));
	}

}