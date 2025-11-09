package com.github.ricaps.tennis_club.business.facade;

import com.github.ricaps.tennis_club.api.surface.SurfaceCreateDto;
import com.github.ricaps.tennis_club.api.surface.SurfaceViewDto;
import com.github.ricaps.tennis_club.business.facade.definition.GenericFacade;
import com.github.ricaps.tennis_club.business.facade.definition.SurfaceFacade;
import com.github.ricaps.tennis_club.business.mapping.SurfaceMapper;
import com.github.ricaps.tennis_club.business.service.definition.SurfaceService;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class SurfaceFacadeImpl implements SurfaceFacade {

	private final GenericFacade<SurfaceViewDto, SurfaceCreateDto, Surface> genericFacade;

	public SurfaceFacadeImpl(SurfaceMapper surfaceMapper, SurfaceService surfaceService) {
		this.genericFacade = new GenericFacade<>(surfaceService, surfaceMapper, Surface.class);
	}

	@Override
	public SurfaceViewDto create(SurfaceCreateDto surfaceCreateDto) {
		return genericFacade.create(surfaceCreateDto);
	}

	@Override
	public Optional<SurfaceViewDto> get(UUID uid) {
		return genericFacade.get(uid);
	}

	@Override
	public PagedModel<SurfaceViewDto> getAll(Pageable pageable) {
		return genericFacade.getAll(pageable);
	}

	@Override
	public SurfaceViewDto update(UUID uid, SurfaceCreateDto surfaceCreateDto) {
		return genericFacade.update(uid, surfaceCreateDto);
	}

	@Override
	public void delete(UUID uid) {
		genericFacade.delete(uid);
	}

}
