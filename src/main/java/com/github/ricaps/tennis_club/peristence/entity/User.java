package com.github.ricaps.tennis_club.peristence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_entity")
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@RequiredArgsConstructor
@AllArgsConstructor
public class User extends IdentifiedEntity {

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String familyName;

	@Column(nullable = false, length = 63)
	private String phoneNumber;

	@Column(nullable = false)
	private String password;

}
