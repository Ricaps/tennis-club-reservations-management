package com.github.ricaps.tennis_club.utils;

import com.github.ricaps.tennis_club.exception.ValueIsMissingException;
import com.github.ricaps.tennis_club.peristence.entity.IdentifiedEntity;

public class ValidationHelper {

	private ValidationHelper() {
		super();
	}

	/**
	 * Checks if the passed object is null. If so, the method throws
	 * {@link ValueIsMissingException}
	 * @param object object to be checked for null
	 * @param message message to be passed into the exception
	 * @throws ValueIsMissingException exception that invokes 400 Bad Request status code
	 */
	public static void requireNonNull(Object object, String message) throws ValueIsMissingException {
		if (object == null) {
			throw new ValueIsMissingException(message);
		}
	}

	/**
	 * Validates that UUID is present in the identified entity
	 * @param entity entity to be checked
	 */
	public static void requireUuidExists(IdentifiedEntity entity) {
		if (entity.getUid() == null) {
			throw new ValueIsMissingException("Entity is missing UUID!");
		}
	}

}
