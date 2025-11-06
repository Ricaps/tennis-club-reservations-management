package com.github.ricaps.tennis_club.configuration.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
@Validated
public final class JwtConfiguration {

	/**
	 * Secret used for signing the JWT tokens
	 */
	@NotNull
	@Size(min = 50, message = "Minimal length of secret is 10 characters")
	private String secret;

	/**
	 * Expiration time in [ms] of the JWT access token. <br>
	 * Recommended value is 15 minutes = 900 000
	 */
	private @NotNull Long expiration;

}
