package com.github.ricaps.tennis_club.api.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Available roles in the system")
public enum RoleDto {

	ADMIN, USER

}
