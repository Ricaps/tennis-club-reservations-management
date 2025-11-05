package com.github.ricaps.tennis_club.api.court;

import com.github.ricaps.tennis_club.api.surface.SurfaceViewDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "DTO for view of Court entity")
public record CourtViewDto(
		@Schema(description = "Court identifier", example = "a25b42b8-ee95-4222-853b-45a70934fc55 ") UUID uid,
		@Schema(description = "Court name", example = "Philippe-Chatrie") String name,
		@Schema(description = "Reference to the surface entity",
				exampleClasses = SurfaceViewDto.class) SurfaceViewDto surface

) {

}
