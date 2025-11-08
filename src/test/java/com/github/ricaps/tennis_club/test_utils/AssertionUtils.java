package com.github.ricaps.tennis_club.test_utils;

import com.github.ricaps.tennis_club.api.shared.ErrorDto;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertionUtils {

	private AssertionUtils() {
		super();
	}

	public static void assertError(ErrorDto errorDto, String message, HttpStatus statusCode,
			List<ErrorDto.FieldError> fieldErrors) {
		if (message != null) {
			assertThat(errorDto.message()).isEqualTo(message);
		}

		if (statusCode != null) {
			assertThat(errorDto.statusCode()).isEqualTo(statusCode.value());
		}

		if (fieldErrors != null) {
			assertThat(errorDto.fieldErrors()).isEqualTo(fieldErrors);
		}
	}

}
