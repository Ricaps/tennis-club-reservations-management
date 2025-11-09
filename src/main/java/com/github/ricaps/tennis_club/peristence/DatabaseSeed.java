package com.github.ricaps.tennis_club.peristence;

import com.github.ricaps.tennis_club.peristence.dao.definition.CourtDao;
import com.github.ricaps.tennis_club.peristence.dao.definition.SurfaceDao;
import com.github.ricaps.tennis_club.peristence.dao.definition.UserDao;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.Role;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import com.github.ricaps.tennis_club.peristence.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@ConditionalOnProperty(value = "application.database-seed", havingValue = "true")
@Slf4j
public class DatabaseSeed implements InitializingBean {

	private final SurfaceDao surfaceDao;

	private final CourtDao courtDao;

	private final UserDao userDao;

	private final PasswordEncoder passwordEncoder;

	@Autowired
	public DatabaseSeed(SurfaceDao surfaceDao, CourtDao courtDao, UserDao userDao, PasswordEncoder passwordEncoder) {
		this.surfaceDao = surfaceDao;
		this.courtDao = courtDao;
		this.userDao = userDao;
		this.passwordEncoder = passwordEncoder;
	}

	private List<Surface> getSurfaces() {
		return List.of(
				Surface.builder()
					.price(new BigDecimal("12.5"))
					.currency(Currency.getInstance("CZK"))
					.uid(UUID.randomUUID())
					.name("Surface 1")
					.build(),
				Surface.builder()
					.price(new BigDecimal("15.5"))
					.currency(Currency.getInstance("CZK"))
					.uid(UUID.randomUUID())
					.name("Surface 2")
					.build());
	}

	private List<Court> getCourts(List<Surface> surfaces) {
		return List.of(Court.builder().uid(UUID.randomUUID()).name("Court 1").surface(surfaces.getFirst()).build(),
				Court.builder().uid(UUID.randomUUID()).name("Court 2").surface(surfaces.getLast()).build(),
				Court.builder().uid(UUID.randomUUID()).name("Court 3").surface(surfaces.getFirst()).build(),
				Court.builder().uid(UUID.randomUUID()).name("Court 4").surface(surfaces.getLast()).build());
	}

	private List<User> getUsers() {
		return List.of(User.builder()
			.uid(UUID.randomUUID())
			.phoneNumber("+420111111112")
			.firstName("John")
			.familyName("Doe")
			.password(passwordEncoder.encode("12345"))
			.roles(Set.of(Role.USER, Role.ADMIN))
			.build());
	}

	@Override
	public void afterPropertiesSet() {
		List<Surface> surfaces = getSurfaces();
		surfaceDao.saveAll(surfaces);
		log.info("Saved {} instances of Surface entity", surfaces.size());

		List<Court> courts = getCourts(surfaces);
		courtDao.saveAll(courts);
		log.info("Saved {} instances of Court entity", courts.size());

		List<User> users = getUsers();
		userDao.saveAll(users);
		log.info("Save {} instances of User entity", users.size());
	}

}
