package com.github.ricaps.tennis_club.test_utils;

import com.github.ricaps.tennis_club.api.surface.SurfaceCreateDto;
import com.github.ricaps.tennis_club.api.surface.SurfaceViewDto;
import com.github.ricaps.tennis_club.peristence.entity.Surface;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SurfaceTestData {

	private SurfaceTestData() {
		super();
	}

	public static Surface createSurface() {
		return Surface.builder()
			.name("test")
			.price(new BigDecimal("12.50"))
			.uid(UUID.randomUUID())
			.currency(Currency.getInstance("CZK"))
			.build();
	}

	public static SurfaceCreateDto createSurfaceCreate() {
		return new SurfaceCreateDto("Name", new BigDecimal("15.50"), Currency.getInstance("CZK"));
	}

	public static SurfaceCreateDto createInvalidSurface() {
		return new SurfaceCreateDto("", new BigDecimal("15.50"), Currency.getInstance("CZK"));
	}

	public static SurfaceViewDto createSurfaceView(UUID uuid) {
		return new SurfaceViewDto(uuid, "Name", new BigDecimal("15.50"), Currency.getInstance("CZK"));
	}

	public static void compareViewAndCreate(SurfaceViewDto surfaceViewDto, SurfaceCreateDto createDto) {
		assertThat(surfaceViewDto.uid()).isNotNull();
		assertThat(surfaceViewDto.name()).isEqualTo(createDto.name());
		assertThat(surfaceViewDto.currency()).isEqualTo(createDto.currency());
		assertThat(surfaceViewDto.price()).isEqualTo(createDto.price());
	}

	public static void compareViewAndEntity(SurfaceViewDto surfaceViewDto, Surface entity) {
		assertThat(surfaceViewDto.uid()).isEqualTo(entity.getUid());
		assertThat(surfaceViewDto.name()).isEqualTo(entity.getName());
		assertThat(surfaceViewDto.currency()).isEqualTo(entity.getCurrency());
		assertThat(surfaceViewDto.price()).isEqualTo(entity.getPrice());
	}

}
