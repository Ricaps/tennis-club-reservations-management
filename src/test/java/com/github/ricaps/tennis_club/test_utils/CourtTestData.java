package com.github.ricaps.tennis_club.test_utils;

import com.github.ricaps.tennis_club.api.court.CourtCreateDto;
import com.github.ricaps.tennis_club.api.court.CourtViewDto;
import com.github.ricaps.tennis_club.api.surface.SurfaceViewDto;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import com.github.ricaps.tennis_club.utils.UUIDUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CourtTestData {

	private CourtTestData() {
		super();
	}

	public static Court entity() {
		return entity(SurfaceTestData.createSurface());
	}

	public static Court entity(Surface surface) {
		return Court.builder().name("test").uid(UUID.randomUUID()).surface(surface).build();
	}

	public static CourtCreateDto createCourt(UUID surfaceUID) {
		return new CourtCreateDto("Name", surfaceUID);
	}

	public static CourtCreateDto createInvalid(UUID surfaceUID) {
		return new CourtCreateDto("", surfaceUID);
	}

	public static CourtViewDto viewCourt(UUID uuid) {
		SurfaceViewDto surface = SurfaceTestData.createSurfaceView(UUIDUtils.generate());
		return new CourtViewDto(uuid, "Name", surface);
	}

	public static void compareViewAndCreate(CourtViewDto courtViewDto, CourtCreateDto createDto, Surface surface) {
		assertThat(courtViewDto.uid()).isNotNull();
		assertThat(courtViewDto.name()).isEqualTo(createDto.name());
		assertThat(courtViewDto.surface().uid()).isEqualTo(createDto.surfaceUid());
		SurfaceTestData.compareViewAndEntity(courtViewDto.surface(), surface);
	}

	public static void compareViewAndEntity(CourtViewDto courtViewDto, Court entity, Surface surfaceEntity) {
		assertThat(courtViewDto.uid()).isEqualTo(entity.getUid());
		assertThat(courtViewDto.name()).isEqualTo(entity.getName());
		SurfaceTestData.compareViewAndEntity(courtViewDto.surface(), surfaceEntity);
	}

}
