package com.github.ricaps.tennis_club.business.service;

import com.github.ricaps.tennis_club.business.service.definition.CourtService;
import com.github.ricaps.tennis_club.business.service.definition.GenericService;
import com.github.ricaps.tennis_club.exception.EntityNotExistsException;
import com.github.ricaps.tennis_club.peristence.dao.definition.CourtDao;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourtServiceImpl implements CourtService {

	private final GenericService<Court> genericService;

	public CourtServiceImpl(CourtDao courtDao) {
		this.genericService = new GenericService<>(courtDao, Court.class);
	}

	@Override
	public Court create(Court entity) {
		return genericService.create(entity);
	}

	@Override
	public Optional<Court> get(UUID uid) {
		return genericService.get(uid);
	}

	@Override
	public Court getReference(UUID uuid) throws EntityNotExistsException {
		return genericService.getReference(uuid);
	}

	@Override
	public List<Court> getAll(Pageable pageable) {
		return genericService.getAll(pageable);
	}

	@Override
	public Court update(Court entity) {
		return genericService.update(entity);
	}

	@Override
	public void delete(UUID uid) {
		genericService.delete(uid);
	}

	@Override
	public long count() {
		return genericService.count();
	}

}
