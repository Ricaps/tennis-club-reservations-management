package com.github.ricaps.tennis_club.peristence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Entity
@Table
@SuperBuilder
@Getter
@Setter
@RequiredArgsConstructor
public class Reservation extends IdentifiedEntity {

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_RESERVATION_ON_COURT_UID"))
	private Court court;

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_RESERVATION_ON_USER_UID"))
	private User user;

	@Column(nullable = false)
	private OffsetDateTime fromTime;

	@Column(nullable = false)
	private OffsetDateTime toTime;

	@Column(nullable = false)
	@Builder.Default
	private OffsetDateTime createdAt = OffsetDateTime.now();

	@Column(nullable = false)
	private Boolean isQuadGame;

	@Embedded
	private MoneyAmount totalPrice;

}
