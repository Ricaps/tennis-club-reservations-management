package com.github.ricaps.tennis_club.business.mapping;

import com.github.ricaps.tennis_club.api.surface.SurfaceCreateDto;
import com.github.ricaps.tennis_club.api.surface.SurfaceViewDto;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SurfaceMapper extends CrudMapper<SurfaceViewDto, SurfaceCreateDto, Surface> {

}
