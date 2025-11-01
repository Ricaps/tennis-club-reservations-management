package com.github.ricaps.tennis_club.peristence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Currency;

@Entity
@Table
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Surface extends IdentifiedEntity {

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@Column(nullable = false, length = 3)
	private Currency currency;

}
