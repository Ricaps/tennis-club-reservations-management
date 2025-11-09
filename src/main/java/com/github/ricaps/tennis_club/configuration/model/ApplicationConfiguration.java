package com.github.ricaps.tennis_club.configuration.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "application")
@Getter
@Setter
@Validated
@SuppressWarnings("unused")
public class ApplicationConfiguration {

	/**
	 * Decides whether database seed is applied or not
	 */
	private boolean databaseSeed;

}
