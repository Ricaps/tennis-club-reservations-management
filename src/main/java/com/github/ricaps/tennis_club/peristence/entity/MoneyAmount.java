package com.github.ricaps.tennis_club.peristence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class MoneyAmount {

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal amount;

	@Column(nullable = false, length = 3)
	private Currency currency;

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof MoneyAmount that))
			return false;
		return Objects.equals(getAmount(), that.getAmount()) && Objects.equals(getCurrency(), that.getCurrency());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getAmount(), getCurrency());
	}

}
