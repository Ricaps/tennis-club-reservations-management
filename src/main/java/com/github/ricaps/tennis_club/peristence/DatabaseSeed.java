package com.github.ricaps.tennis_club.peristence;

import com.github.ricaps.tennis_club.peristence.dao.SurfaceDaoImpl;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Component
@ConditionalOnProperty(value = "database.seed", havingValue = "true")
@Slf4j
public class DatabaseSeed implements InitializingBean {

	private final SurfaceDaoImpl surfaceDao;

	public DatabaseSeed(SurfaceDaoImpl surfaceDao) {
		this.surfaceDao = surfaceDao;
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

	@Override
	public void afterPropertiesSet() {
		List<Surface> surfaces = getSurfaces();
		surfaceDao.saveAll(surfaces);
		log.info("Saved {} instances of Surface entity", surfaces.size());
	}

}
