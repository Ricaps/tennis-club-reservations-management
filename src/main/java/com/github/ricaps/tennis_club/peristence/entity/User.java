package com.github.ricaps.tennis_club.peristence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

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

	@ElementCollection
	@CollectionTable(name = "user_roles",
			joinColumns = @JoinColumn(name = "user_uid", foreignKey = @ForeignKey(name = "fk_user_roles_on_user")))
	@Column(name = "role", nullable = false)
	@Enumerated(EnumType.STRING)
	private Set<Role> roles = new HashSet<>();

}
