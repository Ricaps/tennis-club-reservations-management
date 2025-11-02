package com.github.ricaps.tennis_club.test_utils;

import com.github.ricaps.tennis_club.api.surface.SurfaceCreateDto;
import com.github.ricaps.tennis_club.api.surface.SurfaceViewDto;
import com.github.ricaps.tennis_club.peristence.entity.Surface;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public class TestData {

	private TestData() {
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

	public static SurfaceViewDto createSurfaceView(UUID uuid) {
		return new SurfaceViewDto(uuid, "Name", new BigDecimal("15.50"), Currency.getInstance("CZK"));
	}

}
