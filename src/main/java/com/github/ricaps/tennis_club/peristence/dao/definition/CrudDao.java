package com.github.ricaps.tennis_club.peristence.dao.definition;

import com.github.ricaps.tennis_club.peristence.entity.IdentifiedEntity;
import jakarta.annotation.Nonnull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface CrudDao<EntityType extends IdentifiedEntity> {

	/**
	 * Saves new entity based on the provided data
	 * @param entity entity to be saved
	 * @return saved entity
	 */
	EntityType save(@Nonnull EntityType entity);

	void saveAll(@Nonnull Collection<EntityType> entities);

	/**
	 * Updates given entity
	 * @param entity entity to be updated
	 * @return updated entity
	 */
	EntityType update(@Nonnull EntityType entity);

	/**
	 * Deletes given entity
	 * @param entity entity for deletion
	 */
	void delete(@Nonnull EntityType entity);

	/**
	 * Returns entity based on the UUID
	 * @param uuid uid of the entity
	 * @return found entity
	 */
	Optional<EntityType> findById(@Nonnull UUID uuid);

	/**
	 * Checks database if entity with given ID exists
	 * @param uuid id of the entity
	 * @return true if entity exists, otherwise false
	 */
	boolean existsById(@Nonnull UUID uuid);

	/**
	 * Returns count of all entities
	 * @return number of entities as Long
	 */
	long count();

}
