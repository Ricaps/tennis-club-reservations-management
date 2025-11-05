package com.github.ricaps.tennis_club.business.mapping;

import com.github.ricaps.tennis_club.api.court.CourtCreateDto;
import com.github.ricaps.tennis_club.api.court.CourtViewDto;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CourtMapper extends CrudMapper<CourtViewDto, CourtCreateDto, Court> {

	@Mapping(target = "uid", ignore = true)
	@Mapping(source = "courtCreateDto.name", target = "name")
	@Mapping(source = "surface", target = "surface")
	Court fromCreateToEntity(CourtCreateDto courtCreateDto, Surface surface);

	@Mapping(source = "courtCreateDto.name", target = "name")
	@Mapping(source = "uid", target = "uid")
	@Mapping(source = "surface", target = "surface")
	Court fromCreateToEntity(UUID uid, CourtCreateDto courtCreateDto, Surface surface);

}
