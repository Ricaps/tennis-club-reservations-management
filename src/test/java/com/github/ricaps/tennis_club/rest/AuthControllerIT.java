package com.github.ricaps.tennis_club.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ricaps.tennis_club.api.user.UserRegisterDto;
import com.github.ricaps.tennis_club.peristence.dao.definition.UserDao;
import com.github.ricaps.tennis_club.peristence.entity.Role;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.security.JwtUtils;
import com.github.ricaps.tennis_club.test_utils.UserTestData;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static com.github.ricaps.tennis_club.business.service.AuthServiceImpl.BASIC_PREFIX;
import static com.github.ricaps.tennis_club.security.JwtUtils.ROLE_PREFIX;
import static com.github.ricaps.tennis_club.security.filter.JwtFilter.BEARER_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class AuthControllerIT {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	UserDao userDao;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	void register_allGood_returnsToken() throws Exception {
		UserRegisterDto registerDto = UserTestData.createRegister(true);

		String authorizationHeader = mockMvc
			.perform(post("/v1/auth/register").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(registerDto)))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getHeader(HttpHeaders.AUTHORIZATION);

		assertThat(authorizationHeader).isNotNull();
		Optional<Claims> claimsOpt = jwtUtils.extractClaims(authorizationHeader.substring(BEARER_PREFIX.length()));

		assertThat(claimsOpt).isPresent();
		Claims claims = claimsOpt.get();

		Optional<User> userOpt = userDao.findById(UUID.fromString(claims.getSubject()));
		assertThat(userOpt).isPresent();

		User user = userOpt.get();
		assertThat(passwordEncoder.matches(registerDto.password(), user.getPassword()));
		assertThat(user.getRoles()).hasSize(1).contains(Role.USER);
	}

	@Test
	void register_alreadyExistingPhone_returnsConflict() throws Exception {
		User user = UserTestData.entity();
		userDao.save(user);

		UserRegisterDto registerDto = new UserRegisterDto("John", "Doe", user.getPhoneNumber(), "12345");

		String authorizationHeader = mockMvc
			.perform(post("/v1/auth/register").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(registerDto)))
			.andExpect(status().isConflict())
			.andReturn()
			.getResponse()
			.getHeader(HttpHeaders.AUTHORIZATION);

		assertThat(authorizationHeader).isNull();
	}

	@Test
	void login_notExistingUser_returns401() throws Exception {
		String authorizationHeader = mockMvc
			.perform(post("/v1/auth/login").headers(getBasicAuthHeader("123456789", "pwd")))
			.andExpect(status().isUnauthorized())
			.andReturn()
			.getResponse()
			.getHeader(HttpHeaders.AUTHORIZATION);

		assertThat(authorizationHeader).isNull();
	}

	@Test
	void login_wrongPassword_returns401() throws Exception {
		String password = "passwooooord";
		User user = UserTestData.entity();
		user.setPassword(passwordEncoder.encode(password));

		userDao.save(user);

		String authorizationHeader = mockMvc
			.perform(post("/v1/auth/login").headers(getBasicAuthHeader(user.getPhoneNumber(), "pwd")))
			.andExpect(status().isUnauthorized())
			.andReturn()
			.getResponse()
			.getHeader(HttpHeaders.AUTHORIZATION);

		assertThat(authorizationHeader).isNull();
	}

	@Test
	void login_noHeader_returnsBadRequest() throws Exception {
		String authorizationHeader = mockMvc.perform(post("/v1/auth/login"))
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getHeader(HttpHeaders.AUTHORIZATION);

		assertThat(authorizationHeader).isNull();
	}

	@Test
	void login_headerWithoutBasicPrefix_returnsBadRequest() throws Exception {
		String authorizationHeader = mockMvc.perform(post("/v1/auth/login").header("Authorization", "someHeader"))
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getHeader(HttpHeaders.AUTHORIZATION);

		assertThat(authorizationHeader).isNull();
	}

	@Test
	void login_existingUser_returnsToken() throws Exception {
		String password = "passwooooord";
		User user = UserTestData.entity();
		user.setPassword(passwordEncoder.encode(password));

		userDao.save(user);

		String authorizationHeader = mockMvc
			.perform(post("/v1/auth/login").headers(getBasicAuthHeader(user.getPhoneNumber(), password)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getHeader(HttpHeaders.AUTHORIZATION);

		assertThat(authorizationHeader).isNotNull();
		Optional<Claims> claimsOpt = jwtUtils.extractClaims(authorizationHeader.substring(BEARER_PREFIX.length()));

		assertThat(claimsOpt).isPresent();
		Claims claims = claimsOpt.get();

		Collection<SimpleGrantedAuthority> authorityList = jwtUtils.extractAuthorities(claims);
		assertThat(authorityList).hasSize(user.getRoles().size());
		assertThat(authorityList)
			.containsAll(user.getRoles().stream().map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role)).toList());
	}

	private HttpHeaders getBasicAuthHeader(String phone, String password) {
		String authString = String.join(":", phone, password);
		String encoded = Base64.getEncoder().encodeToString(authString.getBytes());

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.AUTHORIZATION, BASIC_PREFIX + encoded);

		return httpHeaders;
	}

}