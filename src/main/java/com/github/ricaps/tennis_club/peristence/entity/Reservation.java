package com.github.ricaps.tennis_club.peristence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Reservation extends IdentifiedEntity {

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_RESERVATION_ON_COURT_UID"))
	private Court court;

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_RESERVATION_ON_USER_UID"))
	private User user;

	@Column(nullable = false)
	private LocalDateTime from;

	@Column(nullable = false)
	private LocalDateTime to;

	@Column(nullable = false)
	private Boolean is_quad_game;

	@Column(precision = 10, scale = 2)
	private BigDecimal total_price;

}
