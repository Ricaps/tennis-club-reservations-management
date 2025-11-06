package com.github.ricaps.tennis_club.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ricaps.tennis_club.api.surface.SurfaceCreateDto;
import com.github.ricaps.tennis_club.business.service.definition.UserService;
import com.github.ricaps.tennis_club.peristence.dao.definition.UserDao;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.security.JwtUtils;
import com.github.ricaps.tennis_club.security.model.JwtAuthenticationToken;
import com.github.ricaps.tennis_club.test_utils.SecuritySupport;
import com.github.ricaps.tennis_club.test_utils.SurfaceTestData;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class JwtFilterTest {

	SecuritySupport.TestSecurityContext testSecurityContext;

	@Autowired
	SecuritySupport securitySupport;

	@Autowired
	UserDao userDao;

	@MockitoSpyBean
	UserService userService;

	@Autowired
	ObjectMapper objectMapper;

	@MockitoSpyBean
	JwtUtils jwtUtils;

	@BeforeEach
	void setup() {
		testSecurityContext = securitySupport.defineUserAndGetMvc(true);
	}

	@Test
	void newlyLoggedInUser_securityContextCorrectlyAssigned() throws Exception {

		SecurityContext contextSpy = Mockito.spy(SecurityContextHolder.createEmptyContext());
		SurfaceCreateDto createDto = SurfaceTestData.createSurfaceCreate();

		testSecurityContext.mockMvc()
			.perform(post("/v1/surface").contentType(MediaType.APPLICATION_JSON_VALUE)
				.with(securityContext(contextSpy))
				.content(objectMapper.writeValueAsBytes(createDto)))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Claims claims = jwtUtils.extractClaims(testSecurityContext.token()).orElseThrow();
		JwtAuthenticationToken token = getJwtAuthenticationToken(claims, testSecurityContext);
		token.setAuthenticated(true);

		Mockito.verify(userService, Mockito.times(1)).get(UUID.fromString(claims.getSubject()));
		Mockito.verify(contextSpy, Mockito.times(1)).setAuthentication(token);
		assertThat(contextSpy.getAuthentication()).isInstanceOf(JwtAuthenticationToken.class);
	}

	@Test
	void newlyLoggedInUser_authenticationReturnsNotAuthenticated_contextAssigned() throws Exception {

		Authentication authenticationMock = Mockito.mock(Authentication.class);
		Mockito.when(authenticationMock.isAuthenticated()).thenReturn(false);

		SecurityContext contextSpy = Mockito.spy(SecurityContextHolder.createEmptyContext());
		contextSpy.setAuthentication(authenticationMock);

		SurfaceCreateDto createDto = SurfaceTestData.createSurfaceCreate();

		testSecurityContext.mockMvc()
			.perform(post("/v1/surface").contentType(MediaType.APPLICATION_JSON_VALUE)
				.with(securityContext(contextSpy))
				.content(objectMapper.writeValueAsBytes(createDto)))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Claims claims = jwtUtils.extractClaims(testSecurityContext.token()).orElseThrow();
		JwtAuthenticationToken token = getJwtAuthenticationToken(claims, testSecurityContext);
		token.setAuthenticated(true);

		Mockito.verify(userService, Mockito.times(1)).get(UUID.fromString(claims.getSubject()));
		Mockito.verify(contextSpy, Mockito.times(1)).setAuthentication(token);
		assertThat(contextSpy.getAuthentication()).isInstanceOf(JwtAuthenticationToken.class);
	}

	private JwtAuthenticationToken getJwtAuthenticationToken(Claims claims,
			SecuritySupport.TestSecurityContext testSecurityContext) {
		User user = userDao.findById(UUID.fromString(claims.getSubject())).orElseThrow();
		Collection<SimpleGrantedAuthority> authorities = jwtUtils.extractAuthorities(claims);
		return new JwtAuthenticationToken(user, testSecurityContext.token(), authorities);
	}

	@Test
	void newlyLoggedInUser_authenticationReturnsAuthenticated_contextNotReassigned() throws Exception {

		Claims claims = jwtUtils.extractClaims(testSecurityContext.token()).orElseThrow();
		Authentication authentication = getJwtAuthenticationToken(claims, testSecurityContext);
		authentication.setAuthenticated(true);

		SecurityContext contextSpy = Mockito.spy(SecurityContextHolder.createEmptyContext());
		contextSpy.setAuthentication(authentication);
		Mockito.reset(contextSpy);

		SurfaceCreateDto createDto = SurfaceTestData.createSurfaceCreate();

		testSecurityContext.mockMvc()
			.perform(post("/v1/surface").contentType(MediaType.APPLICATION_JSON_VALUE)
				.with(securityContext(contextSpy))
				.content(objectMapper.writeValueAsBytes(createDto)))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Mockito.verify(userService, Mockito.never()).get(Mockito.any());
		Mockito.verify(contextSpy, Mockito.never()).setAuthentication(Mockito.any());
		assertThat(contextSpy.getAuthentication()).isEqualTo(authentication);
	}

	@Test
	void newlyLoggedInUser_nonExistingUserInToken_securityContextNotAssigned() throws Exception {

		SecuritySupport.TestSecurityContext testSecurityContext = securitySupport.defineUserAndGetMvc(false);

		SecurityContext contextSpy = Mockito.spy(SecurityContextHolder.createEmptyContext());
		SurfaceCreateDto createDto = SurfaceTestData.createSurfaceCreate();

		testSecurityContext.mockMvc()
			.perform(post("/v1/surface").contentType(MediaType.APPLICATION_JSON_VALUE)
				.with(securityContext(contextSpy))
				.content(objectMapper.writeValueAsBytes(createDto)))
			.andExpect(status().isUnauthorized())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Mockito.verify(userService, Mockito.times(1)).get(Mockito.any());
		Mockito.verify(contextSpy, Mockito.never()).setAuthentication(Mockito.any());
		assertThat(contextSpy.getAuthentication()).isNull();
	}

	@Test
	void newlyLoggedInUser_invalidToken_securityContextNotAssigned() throws Exception {

		SecurityContext contextSpy = Mockito.spy(SecurityContextHolder.createEmptyContext());
		SurfaceCreateDto createDto = SurfaceTestData.createSurfaceCreate();
		Mockito.when(jwtUtils.extractClaims(testSecurityContext.token())).thenReturn(Optional.empty());

		testSecurityContext.mockMvc()
			.perform(post("/v1/surface").contentType(MediaType.APPLICATION_JSON_VALUE)
				.with(securityContext(contextSpy))
				.content(objectMapper.writeValueAsBytes(createDto)))
			.andExpect(status().isUnauthorized())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Mockito.verify(userService, Mockito.never()).get(Mockito.any());
		Mockito.verify(contextSpy, Mockito.never()).setAuthentication(Mockito.any());
		assertThat(contextSpy.getAuthentication()).isNull();
	}

}