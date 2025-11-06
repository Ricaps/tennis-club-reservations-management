package com.github.ricaps.tennis_club.api.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Schema(description = "Schema for creating user. Only for admins user management!")
public record UserCreateDto(
		@NotNull @Size(min = 1, max = 255) @Schema(description = "First name of the user",
				example = "John") String firstName,
		@NotNull @Size(min = 1, max = 255) @Schema(description = "Family name of the user",
				example = "Doe") String familyName,
		@NotNull @Size(min = 1, max = 63) @Schema(description = "Phone number",
				example = "+420 111 111 111") String phoneNumber,
		@NotNull @Size(min = 1, max = 255) @Schema(description = "Password", example = "password") String password,
		@NotNull @Length(min = 1) @Schema(description = "Users role", example = "[\"USER\"]") Set<RoleDto> roles) {
}
