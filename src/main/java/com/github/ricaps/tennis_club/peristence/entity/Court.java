package com.github.ricaps.tennis_club.peristence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Court extends IdentifiedEntity {

	@Column(nullable = false)
	private String name;

	@ManyToOne(optional = false)
	private Surface surface;

}
