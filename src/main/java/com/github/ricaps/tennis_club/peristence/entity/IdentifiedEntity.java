package com.github.ricaps.tennis_club.peristence.entity;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
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

	@Override
	public final boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy
				? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass)
			return false;
		IdentifiedEntity that = (IdentifiedEntity) o;
		return getUid() != null && Objects.equals(getUid(), that.getUid());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}
}
