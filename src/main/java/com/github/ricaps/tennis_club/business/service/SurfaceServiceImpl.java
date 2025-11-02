package com.github.ricaps.tennis_club.business.service;

import com.github.ricaps.tennis_club.business.service.definition.SurfaceService;
import com.github.ricaps.tennis_club.exception.EntityExistsException;
import com.github.ricaps.tennis_club.exception.EntityNotExistsException;
import com.github.ricaps.tennis_club.peristence.dao.definition.SurfaceDao;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import com.github.ricaps.tennis_club.utils.ValidationHelper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class SurfaceServiceImpl implements SurfaceService {

	private final SurfaceDao surfaceDao;

	public SurfaceServiceImpl(SurfaceDao surfaceDao) {
		this.surfaceDao = surfaceDao;
	}

	@Override
	public Surface create(Surface entity) {
		ValidationHelper.requireNonNull(entity, "Please provide non null Surface entity!");
		ValidationHelper.requireUuidExists(entity);
		if (surfaceDao.existsById(entity.getUid())) {
			throw new EntityExistsException("Entity Surface with ID %s already exists!".formatted(entity.getUid()));
		}

		return surfaceDao.save(entity);
	}

	@Override
	public Optional<Surface> get(UUID uid) {
		ValidationHelper.requireNonNull(uid, "Entity uid must not be null!");

		return surfaceDao.findById(uid);
	}

	@Override
	public List<Surface> getAll(Pageable pageable) {
		ValidationHelper.requireNonNull(pageable, "Pageable object must not be null!");

		return surfaceDao.findAll(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
	}

	@Override
	public Surface update(Surface entity) {
		ValidationHelper.requireNonNull(entity, "Please provide non null Surface entity!");
		ValidationHelper.requireUuidExists(entity);

		if (!surfaceDao.existsById(entity.getUid())) {
			throw new EntityNotExistsException("Surface with UID %s doesn't exist!".formatted(entity.getUid()));
		}

		return surfaceDao.update(entity);
	}

	@Override
	public void delete(UUID uid) {
		ValidationHelper.requireNonNull(uid, "Please provide non null UUID for entity deletion!");

		if (!surfaceDao.existsById(uid)) {
			throw new EntityNotExistsException("Surface with UID %s doesn't exist!".formatted(uid));
		}

		surfaceDao.delete(uid);
	}

	@Override
	public long count() {
		return surfaceDao.count();
	}

}
