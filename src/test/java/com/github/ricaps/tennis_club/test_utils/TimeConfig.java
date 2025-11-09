package com.github.ricaps.tennis_club.test_utils;

import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

@Configuration
public class TimeConfig {

	public static Clock getFixedClock() {
		return Clock.fixed(LocalDate.of(2025, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
				ZoneId.systemDefault());
	}

	@Bean
	public Clock clock() {
		return getFixedClock();
	}

	@Bean
	public LocalValidatorFactoryBean defaultValidator(Clock clock) {
		LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean() {
			@Override
			protected void postProcessConfiguration(jakarta.validation.Configuration<?> configuration) {
				configuration.clockProvider(() -> clock);
			}
		};
		MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory();
		factory.setMessageInterpolator(interpolatorFactory.getObject());
		return factory;
	}

}
