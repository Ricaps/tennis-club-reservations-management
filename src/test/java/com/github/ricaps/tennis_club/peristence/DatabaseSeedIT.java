package com.github.ricaps.tennis_club.peristence;

import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = { "application.database-seed=true",
		"spring.datasource.url=jdbc:h2:mem:seed-test;DB_CLOSE_DELAY=-1" })
@Transactional
class DatabaseSeedIT {

	@Autowired
	EntityManager entityManager;

	@Test
	void testSurface_allInserted() {
		List<Surface> surfaceList = entityManager.createQuery("FROM Surface", Surface.class).getResultList();
		assertThat(surfaceList).hasSize(2);
	}

	@Test
	void testCourts_allInserted() {
		List<Court> surfaceList = entityManager.createQuery("FROM Court", Court.class).getResultList();
		assertThat(surfaceList).hasSize(4);
	}

}