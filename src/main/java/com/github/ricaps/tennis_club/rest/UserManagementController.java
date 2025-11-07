package com.github.ricaps.tennis_club.rest;

import com.github.ricaps.tennis_club.api.user.UserCreateDto;
import com.github.ricaps.tennis_club.api.user.UserDetailedView;
import com.github.ricaps.tennis_club.business.facade.UserFacade;
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
@RequestMapping("/v1/user")
@Tag(name = "User Management API",
		description = "Operations related to User management. Modifying operations are available only to administrators!")
@Validated
public class UserManagementController {

	private final UserFacade userFacade;

	public UserManagementController(UserFacade userFacade) {
		this.userFacade = userFacade;
	}

	@Operation(description = "Creates a user")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "User created successfully",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "400", description = "Validation of input request failed") })
	@PostMapping
	public ResponseEntity<UserDetailedView> create(@RequestBody @Valid UserCreateDto userCreateDto) {
		UserDetailedView userViewDto = userFacade.create(userCreateDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(userViewDto);
	}

	@Operation(description = "Gets a user by uid")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User found and returned successfully",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "400", description = "Validation of input request failed"),
			@ApiResponse(responseCode = "404", description = "User not found") })
	@GetMapping("/{uid}")
	public ResponseEntity<UserDetailedView> get(@PathVariable UUID uid) {
		Optional<UserDetailedView> userView = userFacade.get(uid);

		return ResponseEntity.of(userView);
	}

	@Operation(description = "Get all users paged")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Users found and returned successfully",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "400", description = "Validation of input request failed"), })
	@GetMapping
	@PageableAsQueryParam
	public ResponseEntity<PagedModel<UserDetailedView>> get(
			@ParameterObject @PageableDefault(sort = { "familyName" }) Pageable pageable) {
		PagedModel<UserDetailedView> usersPaged = userFacade.getAll(pageable);

		return ResponseEntity.ok(usersPaged);
	}

	@Operation(description = "Updates a user by its uid")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User updated successfully",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "400", description = "Validation of input request failed") })
	@PutMapping("/{uid}")
	public ResponseEntity<UserDetailedView> update(@PathVariable UUID uid,
			@RequestBody @Valid UserCreateDto userCreateDto) {
		UserDetailedView userView = userFacade.update(uid, userCreateDto);

		return ResponseEntity.status(HttpStatus.OK).body(userView);
	}

	@Operation(description = "Deletes a user")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "User deleted successfully"),
			@ApiResponse(responseCode = "400", description = "Validation of input request failed") })
	@DeleteMapping("/{uid}")
	public ResponseEntity<UserDetailedView> delete(@PathVariable UUID uid) {
		userFacade.delete(uid);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
