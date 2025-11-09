package com.github.ricaps.tennis_club.rest;

import com.github.ricaps.tennis_club.api.court.CourtCreateDto;
import com.github.ricaps.tennis_club.api.court.CourtViewDto;
import com.github.ricaps.tennis_club.business.facade.definition.CourtFacade;
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
@RequestMapping("/v1/court")
@Tag(name = "Court CRUD API", description = "Operations related to Court management")
@Validated
public class CourtController {

	private final CourtFacade courtFacade;

	public CourtController(CourtFacade courtFacade) {
		this.courtFacade = courtFacade;
	}

	@Operation(description = "Creates a court")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Court created successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) })
	@PostMapping
	public ResponseEntity<CourtViewDto> create(@RequestBody @Valid CourtCreateDto courtCreateDto) {
		CourtViewDto courtView = courtFacade.create(courtCreateDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(courtView);
	}

	@Operation(description = "Gets a court by its uid")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Court found and returned successfully",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "404", description = "Court not found") })
	@GetMapping("/{uid}")
	public ResponseEntity<CourtViewDto> get(@PathVariable UUID uid) {
		Optional<CourtViewDto> courtView = courtFacade.get(uid);

		return ResponseEntity.of(courtView);
	}

	@Operation(description = "Get all courts paged")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Court found and returned successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) })
	@GetMapping
	@PageableAsQueryParam
	public ResponseEntity<PagedModel<CourtViewDto>> get(
			@ParameterObject @PageableDefault(sort = { "name" }) Pageable pageable) {
		PagedModel<CourtViewDto> courtView = courtFacade.getAll(pageable);

		return ResponseEntity.ok(courtView);
	}

	@Operation(description = "Updates a court by its uid")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Court updated successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) })
	@PutMapping("/{uid}")
	public ResponseEntity<CourtViewDto> update(@PathVariable UUID uid,
			@RequestBody @Valid CourtCreateDto courtCreateDto) {
		CourtViewDto courtView = courtFacade.update(uid, courtCreateDto);

		return ResponseEntity.status(HttpStatus.OK).body(courtView);
	}

	@Operation(description = "Deletes a court")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Court deleted successfully") })
	@DeleteMapping("/{uid}")
	public ResponseEntity<CourtViewDto> delete(@PathVariable UUID uid) {
		courtFacade.delete(uid);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
