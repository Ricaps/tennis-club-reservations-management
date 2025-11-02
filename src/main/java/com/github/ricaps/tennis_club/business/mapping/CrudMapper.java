package com.github.ricaps.tennis_club.business.mapping;

import com.github.ricaps.tennis_club.peristence.entity.IdentifiedEntity;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CrudMapper<ViewTypeDto, CreateTypeDto, EntityType extends IdentifiedEntity> {

	EntityType fromCreateToEntity(CreateTypeDto dto);

	EntityType fromCreateToEntity(UUID uid, CreateTypeDto dto);

	ViewTypeDto fromEntityToView(EntityType entity);

	List<ViewTypeDto> fromEntityListToView(Collection<EntityType> entities);

}
