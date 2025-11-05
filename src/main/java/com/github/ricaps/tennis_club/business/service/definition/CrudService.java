package com.github.ricaps.tennis_club.business.service.definition;

import com.github.ricaps.tennis_club.exception.EntityNotExistsException;
import com.github.ricaps.tennis_club.peristence.entity.IdentifiedEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CrudService<EntityType extends IdentifiedEntity> {

	/**
	 * Creates given entity. Entity must not exist for correct creation.
	 * @param entity entity for creation
	 * @return created entity
	 */
	EntityType create(EntityType entity);

	/**
	 * Gets optionally the desired entity by its uid
	 * @param uid uid of the entity
	 * @return entity wrapper in {@link Optional}
	 */
	Optional<EntityType> get(UUID uid);

	/**
	 * Gets lazy reference of an Entity. <br>
	 * If entity doesn't exist, throws {@link EntityNotExistsException}
	 * @param uuid identifier of an entity
	 * @return lazy reference to an entity
	 */
	EntityType getReference(UUID uuid) throws EntityNotExistsException;

	/**
	 * Get all entities paged.
	 * @param pageable pageable object containing information about pages, sorting, ...
	 * @return list of entities
	 */
	List<EntityType> getAll(Pageable pageable);

	/**
	 * Updated given entity. Entity must exist for correct update.
	 * @param entity entity for update
	 * @return updated entity
	 */
	EntityType update(EntityType entity);

	/**
	 * Deletes given entity. Entity must exist for correct deletion.
	 * @param uid uid of the entity
	 */
	void delete(UUID uid);

	/**
	 * Get count of entities
	 * @return count as long
	 */
	long count();

}
