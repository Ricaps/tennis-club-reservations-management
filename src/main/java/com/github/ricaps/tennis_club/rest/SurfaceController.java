package com.github.ricaps.tennis_club.rest;

import com.github.ricaps.tennis_club.api.surface.SurfaceCreateDto;
import com.github.ricaps.tennis_club.api.surface.SurfaceViewDto;
import com.github.ricaps.tennis_club.business.facade.SurfaceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1/surface")
@Tag(name = "Surface CRUD API", description = "Operations related to Surface management")
@Validated
public class SurfaceController {

	private final SurfaceFacade surfaceFacade;

	public SurfaceController(SurfaceFacade surfaceFacade) {
		this.surfaceFacade = surfaceFacade;
	}

	@Operation(description = "Creates a surface")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Surface created successfully",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "400", description = "Validation of input request failed") })
	@PostMapping
	public ResponseEntity<SurfaceViewDto> create(@RequestBody @Valid SurfaceCreateDto surfaceCreateDto) {
		SurfaceViewDto surfaceView = surfaceFacade.create(surfaceCreateDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(surfaceView);
	}

	@Operation(description = "Gets a surface by its uid")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Surface found and returned successfully",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "400", description = "Validation of input request failed"),
			@ApiResponse(responseCode = "404", description = "Surface not found") })
	@GetMapping("/{uid}")
	public ResponseEntity<SurfaceViewDto> get(@PathVariable UUID uid) {
		Optional<SurfaceViewDto> surfaceView = surfaceFacade.get(uid);

		return ResponseEntity.of(surfaceView);
	}

	@Operation(description = "Get all surfaces paged")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Surface found and returned successfully",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "400", description = "Validation of input request failed"), })
	@GetMapping
	@PageableAsQueryParam
	public ResponseEntity<PagedModel<SurfaceViewDto>> get(
			@ParameterObject @PageableDefault(sort = { "name" }) Pageable pageable) {
		PagedModel<SurfaceViewDto> surfaceView = surfaceFacade.getAll(pageable);

		return ResponseEntity.ok(surfaceView);
	}

	@Operation(description = "Updates a surface by its uid")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Surface updated successfully",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "400", description = "Validation of input request failed") })
	@PutMapping("/{uid}")
	public ResponseEntity<SurfaceViewDto> update(@PathVariable UUID uid,
			@RequestBody @Valid SurfaceCreateDto surfaceCreateDto) {
		SurfaceViewDto surfaceView = surfaceFacade.update(uid, surfaceCreateDto);

		return ResponseEntity.status(HttpStatus.OK).body(surfaceView);
	}

	@Operation(description = "Deletes a surface")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Surface deleted successfully"),
			@ApiResponse(responseCode = "400", description = "Validation of input request failed") })
	@DeleteMapping("/{uid}")
	public ResponseEntity<SurfaceViewDto> delete(@PathVariable UUID uid) {
		surfaceFacade.delete(uid);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
