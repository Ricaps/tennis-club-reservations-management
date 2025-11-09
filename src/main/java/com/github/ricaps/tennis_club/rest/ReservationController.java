package com.github.ricaps.tennis_club.rest;

import com.github.ricaps.tennis_club.api.reservation.ReservationCreateDto;
import com.github.ricaps.tennis_club.api.reservation.ReservationPhoneDateQueryDto;
import com.github.ricaps.tennis_club.api.reservation.ReservationViewDto;
import com.github.ricaps.tennis_club.business.facade.definition.ReservationFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1/reservation")
@Tag(name = "Reservation CRUD API", description = "Operations related to Reservation management")
@Validated
public class ReservationController {

	private final ReservationFacade reservationFacade;

	public ReservationController(ReservationFacade reservationFacade) {
		this.reservationFacade = reservationFacade;
	}

	@Operation(description = "Creates a reservation")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Reservation created successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) })
	@PostMapping
	public ResponseEntity<ReservationViewDto> create(@RequestBody @Valid ReservationCreateDto reservationCreateDto) {
		ReservationViewDto reservationView = reservationFacade.create(reservationCreateDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(reservationView);
	}

	@Operation(description = "Gets a reservation by its uid")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Reservation found and returned successfully",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "404", description = "Reservation not found") })
	@GetMapping("/{uid}")
	public ResponseEntity<ReservationViewDto> get(@PathVariable UUID uid) {
		Optional<ReservationViewDto> reservationView = reservationFacade.get(uid);

		return ResponseEntity.of(reservationView);
	}

	@Operation(description = "Get all reservations paged")
	@ApiResponses(
			value = { @ApiResponse(responseCode = "200", description = "Reservation found and returned successfully",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) })
	@GetMapping
	@PageableAsQueryParam
	public ResponseEntity<PagedModel<ReservationViewDto>> get(
			@ParameterObject @PageableDefault(sort = { "name" }) Pageable pageable) {
		PagedModel<ReservationViewDto> reservationView = reservationFacade.getAll(pageable);

		return ResponseEntity.ok(reservationView);
	}

	@Operation(description = "Updates a reservation by its uid")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Reservation updated successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) })
	@PutMapping("/{uid}")
	public ResponseEntity<ReservationViewDto> update(@PathVariable UUID uid,
			@RequestBody @Valid ReservationCreateDto reservationCreateDto) {
		ReservationViewDto reservationView = reservationFacade.update(uid, reservationCreateDto);

		return ResponseEntity.status(HttpStatus.OK).body(reservationView);
	}

	@Operation(description = "Deletes a reservation")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Reservation deleted successfully") })
	@DeleteMapping("/{uid}")
	public ResponseEntity<ReservationViewDto> delete(@PathVariable UUID uid) {
		reservationFacade.delete(uid);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(description = "Get all reservations paged filtered by specific court UID")
	@ApiResponses(
			value = { @ApiResponse(responseCode = "200", description = "Reservation found and returned successfully",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) })
	@GetMapping("/court/{courtUID}")
	@PageableAsQueryParam
	public ResponseEntity<PagedModel<ReservationViewDto>> getByCourt(
			@Schema(example = "UID of the court") @PathVariable UUID courtUID,
			@Schema(example = "Pageable object. Default paging is createdAt|asc") @ParameterObject @PageableDefault(
					sort = { "createdAt" }) Pageable pageable) {
		PagedModel<ReservationViewDto> reservationView = reservationFacade.getAllByCourt(courtUID, pageable);

		return ResponseEntity.ok(reservationView);
	}

	@Operation(description = "Get all reservations paged filtered by user (phoneNumber) and time")
	@ApiResponses(
			value = { @ApiResponse(responseCode = "200", description = "Reservation found and returned successfully",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) })
	@GetMapping("/user/{phoneNumber}")
	@PageableAsQueryParam
	public ResponseEntity<PagedModel<ReservationViewDto>> getByPhoneNumber(
			@PathVariable @NotNull @Schema(description = "Phone number bound to reservation's user") String phoneNumber,
			@RequestParam @NotNull @Schema(
					description = "Ability to filter reservations by date and time. Shows reservations with datetime greater than defined.",
					example = "2025-11-07T14:30:00+01:00") OffsetDateTime fromTime,
			@ParameterObject @PageableDefault(sort = "createdAt") Pageable pageable) {
		PagedModel<ReservationViewDto> reservationView = reservationFacade
			.getAllByPhoneNumber(new ReservationPhoneDateQueryDto(phoneNumber, fromTime, pageable));

		return ResponseEntity.ok(reservationView);
	}

}
