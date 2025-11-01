package com.github.ricaps.tennis_club.peristence.entity;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;

import java.util.UUID;

@MappedSuperclass
@SoftDelete
@SuperBuilder
@NoArgsConstructor
@ToString
@Getter
@Setter
abstract public class IdentifiedEntity {

	@Id
	private UUID uid;

}
