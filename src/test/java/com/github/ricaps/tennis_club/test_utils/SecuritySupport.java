package com.github.ricaps.tennis_club.test_utils;

import com.github.ricaps.tennis_club.peristence.dao.definition.UserDao;
import com.github.ricaps.tennis_club.peristence.entity.Role;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.security.JwtUtils;
import com.github.ricaps.tennis_club.security.model.JwtAuthenticationToken;
import com.github.ricaps.tennis_club.security.model.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Set;

import static com.github.ricaps.tennis_club.security.JwtUtils.ROLE_PREFIX;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Component
public class SecuritySupport {

	@Autowired
	UserDao userDao;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	WebApplicationContext context;

	public TestSecurityContext defineUserAndGetMvc(boolean saveUser) {
		User user = UserTestData.entity();
		if (saveUser) {
			userDao.save(user);
		}

		String token = jwtUtils.generateAccessToken(user);
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context)
			.defaultRequest(get("/").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
			.apply(SecurityMockMvcConfigurers.springSecurity())
			.build();

		return new TestSecurityContext(mvc, token);
	}

	public MockMvc createFakeAuthMvc(Set<Role> roles) {
		User user = UserTestData.entity();
		user.setRoles(roles);

		String token = jwtUtils.generateAccessToken(user);
		List<SimpleGrantedAuthority> authorities = roles.stream()
			.map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
			.toList();
		JwtAuthenticationToken auth = new JwtAuthenticationToken(new JwtUser(user, authorities), token);
		auth.setAuthenticated(true);
		return MockMvcBuilders.webAppContextSetup(context)
			.defaultRequest(get("/").header(HttpHeaders.AUTHORIZATION, "Bearer " + token).with(authentication(auth)))
			.apply(SecurityMockMvcConfigurers.springSecurity())
			.build();
	}

	public static record TestSecurityContext(MockMvc mockMvc, String token) {
	}

}
