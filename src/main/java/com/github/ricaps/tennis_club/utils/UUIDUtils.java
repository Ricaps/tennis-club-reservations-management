package com.github.ricaps.tennis_club.utils;

import java.util.UUID;

public class UUIDUtils {

	private UUIDUtils() {
		super();
	}

	public static UUID generate() {
		return UUID.randomUUID();
	}

}
