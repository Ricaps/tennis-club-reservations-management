package com.github.ricaps.tennis_club.business.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyUtils {

	public static final int PRECISION = 10;

	public static final int SCALE = 2;

	public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

	private MoneyUtils() {
		super();
	}

	public static BigDecimal multiply(BigDecimal base, BigDecimal multiplier) {
		return base.multiply(multiplier).setScale(SCALE, ROUNDING_MODE);
	}

}
