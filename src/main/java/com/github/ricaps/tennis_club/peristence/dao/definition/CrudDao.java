package com.github.ricaps.tennis_club.peristence.dao.definition;

import com.github.ricaps.tennis_club.peristence.entity.IdentifiedEntity;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CrudDao<EntityType extends IdentifiedEntity> {

	/**
	 * Saves new entity based on the provided data
	 * @param entity entity to be saved
	 * @return saved entity
	 */
	EntityType save(EntityType entity);

	void saveAll(Collection<EntityType> entities);

	/**
	 * Updates given entity
	 * @param entity entity to be updated
	 * @return updated entity
	 */
	EntityType update(EntityType entity);

	/**
	 * Deletes given entity
	 * @param entity entity for deletion
	 * @return true if the deletion was successful
	 */
	boolean delete(UUID entity);

	/**
	 * Returns entity based on the UUID
	 * @param uuid uid of the entity
	 * @return found entity
	 */
	Optional<EntityType> findById(UUID uuid);

	/**
	 * Gets lazy proxy of an entity
	 * @param uuid uid of an entity
	 * @return entity reference
	 */
	EntityType findReferenceById(UUID uuid);

	/**
	 * Returns all entities pageable
	 * @param pageNumber page number
	 * @param pageSize size of requested page
	 * @param sort sort object defining sort constraints
	 * @return list of entities
	 */
	List<EntityType> findAll(int pageNumber, int pageSize, Sort sort);

	/**
	 * Checks database if entity with given ID exists
	 * @param uuid id of the entity
	 * @return true if entity exists, otherwise false
	 */
	boolean existsById(UUID uuid);

	/**
	 * Returns count of all entities
	 * @return number of entities as Long
	 */
	long count();

}
