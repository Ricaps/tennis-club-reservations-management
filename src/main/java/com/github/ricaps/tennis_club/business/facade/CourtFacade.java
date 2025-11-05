package com.github.ricaps.tennis_club.business.facade;

import com.github.ricaps.tennis_club.api.court.CourtCreateDto;
import com.github.ricaps.tennis_club.api.court.CourtViewDto;
import com.github.ricaps.tennis_club.business.facade.definition.CrudFacade;
import com.github.ricaps.tennis_club.business.facade.definition.GenericFacade;
import com.github.ricaps.tennis_club.business.mapping.CourtMapper;
import com.github.ricaps.tennis_club.business.service.definition.CourtService;
import com.github.ricaps.tennis_club.business.service.definition.SurfaceService;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import com.github.ricaps.tennis_club.utils.UUIDUtils;
import com.github.ricaps.tennis_club.utils.ValidationHelper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CourtFacade implements CrudFacade<CourtViewDto, CourtCreateDto> {

	private final CourtService courtService;

	private final SurfaceService surfaceService;

	private final CourtMapper courtMapper;

	private final GenericFacade<CourtViewDto, CourtCreateDto, Court> genericFacade;

	public CourtFacade(CourtService courtService, SurfaceService surfaceService, CourtMapper courtMapper) {
		this.courtService = courtService;
		this.surfaceService = surfaceService;
		this.courtMapper = courtMapper;
		this.genericFacade = new GenericFacade<>(courtService, courtMapper, Court.class);
	}

	@Override
	public CourtViewDto create(CourtCreateDto courtCreateDto) {
		ValidationHelper.requireNonNull(courtCreateDto, "Court create DTO cannot be null!");

		final Surface surfaceReference = surfaceService.getReference(courtCreateDto.surfaceUid());
		final Court entity = courtMapper.fromCreateToEntity(courtCreateDto, surfaceReference);

		entity.setUid(UUIDUtils.generate());

		final Court createdEntity = courtService.create(entity);

		return courtMapper.fromEntityToView(createdEntity);
	}

	@Override
	public Optional<CourtViewDto> get(UUID uid) {
		return genericFacade.get(uid);
	}

	@Override
	public PagedModel<CourtViewDto> getAll(Pageable pageable) {
		return genericFacade.getAll(pageable);
	}

	@Override
	public CourtViewDto update(UUID uid, CourtCreateDto courtCreateDto) {
		ValidationHelper.requireNonNull(uid, "Court UUID cannot be null!");
		ValidationHelper.requireNonNull(courtCreateDto, "Court create DTO cannot be null!");

		final Surface surfaceReference = surfaceService.getReference(courtCreateDto.surfaceUid());

		final Court entity = courtMapper.fromCreateToEntity(uid, courtCreateDto, surfaceReference);
		final Court updatedEntity = courtService.update(entity);

		return courtMapper.fromEntityToView(updatedEntity);
	}

	@Override
	public void delete(UUID uid) {
		this.genericFacade.delete(uid);
	}

}
