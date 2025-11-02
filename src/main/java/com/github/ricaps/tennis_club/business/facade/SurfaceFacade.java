package com.github.ricaps.tennis_club.business.facade;

import com.github.ricaps.tennis_club.api.surface.SurfaceCreateDto;
import com.github.ricaps.tennis_club.api.surface.SurfaceViewDto;
import com.github.ricaps.tennis_club.business.facade.definition.CrudFacade;
import com.github.ricaps.tennis_club.business.mapping.SurfaceMapper;
import com.github.ricaps.tennis_club.business.service.definition.SurfaceService;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import com.github.ricaps.tennis_club.utils.UUIDUtils;
import com.github.ricaps.tennis_club.utils.ValidationHelper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SurfaceFacade implements CrudFacade<SurfaceViewDto, SurfaceCreateDto> {

	private final SurfaceMapper surfaceMapper;

	private final SurfaceService surfaceService;

	public SurfaceFacade(SurfaceMapper surfaceMapper, SurfaceService surfaceService) {
		this.surfaceMapper = surfaceMapper;
		this.surfaceService = surfaceService;
	}

	@Override
	public SurfaceViewDto create(SurfaceCreateDto surfaceCreateDto) {
		ValidationHelper.requireNonNull(surfaceCreateDto, "Surface create DTO cannot be null!");

		final Surface entity = surfaceMapper.fromCreateToEntity(surfaceCreateDto);
		entity.setUid(UUIDUtils.generate());

		final Surface createdEntity = surfaceService.create(entity);

		return surfaceMapper.fromEntityToView(createdEntity);
	}

	@Override
	public Optional<SurfaceViewDto> get(UUID uid) {
		ValidationHelper.requireNonNull(uid, "Entity uid must not be null!");

		return surfaceService.get(uid).map(surfaceMapper::fromEntityToView);
	}

	@Override
	@Transactional
	public PagedModel<SurfaceViewDto> getAll(Pageable pageable) {
		ValidationHelper.requireNonNull(pageable, "Pageable object cannot be null!");

		List<Surface> entities = surfaceService.getAll(pageable);
		List<SurfaceViewDto> surfaceViewDtoList = surfaceMapper.fromEntityListToView(entities);

		PageImpl<SurfaceViewDto> surfaceViewPage = new PageImpl<>(surfaceViewDtoList, pageable, surfaceService.count());
		return new PagedModel<>(surfaceViewPage);
	}

	@Override
	public SurfaceViewDto update(UUID uid, SurfaceCreateDto surfaceCreateDto) {
		ValidationHelper.requireNonNull(uid, "Surface UUID cannot be null!");
		ValidationHelper.requireNonNull(surfaceCreateDto, "Surface create DTO cannot be null!");

		final Surface entity = surfaceMapper.fromCreateToEntity(uid, surfaceCreateDto);
		final Surface updatedEntity = surfaceService.update(entity);

		return surfaceMapper.fromEntityToView(updatedEntity);
	}

	@Override
	public void delete(UUID uid) {
		ValidationHelper.requireNonNull(uid, "Surface UUID cannot be null!");

		surfaceService.delete(uid);
	}

}
