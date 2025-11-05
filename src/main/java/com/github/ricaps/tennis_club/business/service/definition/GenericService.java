package com.github.ricaps.tennis_club.business.service.definition;

import com.github.ricaps.tennis_club.exception.EntityExistsException;
import com.github.ricaps.tennis_club.exception.EntityNotExistsException;
import com.github.ricaps.tennis_club.peristence.dao.definition.CrudDao;
import com.github.ricaps.tennis_club.peristence.entity.IdentifiedEntity;
import com.github.ricaps.tennis_club.utils.ValidationHelper;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GenericService<EntityType extends IdentifiedEntity> implements CrudService<EntityType> {

	private final CrudDao<EntityType> crudDao;

	private final Class<EntityType> entityClass;

	public GenericService(CrudDao<EntityType> crudDao, Class<EntityType> entityClass) {
		this.crudDao = crudDao;
		this.entityClass = entityClass;
	}

	@Override
	public EntityType create(EntityType entity) {
		ValidationHelper.requireNonNull(entity, "Please provide non null %s entity!".formatted(getEntityName()));
		ValidationHelper.requireUuidExists(entity);
		if (crudDao.existsById(entity.getUid())) {
			throw new EntityExistsException(
					"Entity %s with ID %s already exists!".formatted(getEntityName(), entity.getUid()));
		}

		return crudDao.save(entity);
	}

	@Override
	public Optional<EntityType> get(UUID uid) {
		ValidationHelper.requireNonNull(uid, "Entity uid must not be null!");

		return crudDao.findById(uid);
	}

	@Override
	public EntityType getReference(UUID uuid) throws EntityNotExistsException {
		if (!crudDao.existsById(uuid)) {
			throw new EntityNotExistsException("Entity with ID %s doesn't exist!".formatted(uuid));
		}

		return crudDao.findReferenceById(uuid);
	}

	@Override
	public List<EntityType> getAll(Pageable pageable) {
		ValidationHelper.requireNonNull(pageable, "Pageable object must not be null!");

		return crudDao.findAll(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
	}

	@Override
	public EntityType update(EntityType entity) {
		ValidationHelper.requireNonNull(entity, "Please provide non null Surface entity!");
		ValidationHelper.requireUuidExists(entity);

		if (!crudDao.existsById(entity.getUid())) {
			throw new EntityNotExistsException(
					"%s with UID %s doesn't exist!".formatted(getEntityName(), entity.getUid()));
		}

		return crudDao.update(entity);
	}

	@Override
	public void delete(UUID uid) {
		ValidationHelper.requireNonNull(uid, "Please provide non null UUID for entity deletion!");

		if (!crudDao.existsById(uid)) {
			throw new EntityNotExistsException("%s with UID %s doesn't exist!".formatted(getEntityName(), uid));
		}

		crudDao.delete(uid);
	}

	@Override
	public long count() {
		return crudDao.count();
	}

	private String getEntityName() {
		return entityClass.getSimpleName();
	}

}
