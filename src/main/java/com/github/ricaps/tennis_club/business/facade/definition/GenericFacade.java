package com.github.ricaps.tennis_club.business.facade.definition;

import com.github.ricaps.tennis_club.business.mapping.CrudMapper;
import com.github.ricaps.tennis_club.business.service.definition.CrudService;
import com.github.ricaps.tennis_club.peristence.entity.IdentifiedEntity;
import com.github.ricaps.tennis_club.utils.UUIDUtils;
import com.github.ricaps.tennis_club.utils.ValidationHelper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GenericFacade<ViewTypeDto, CreateTypeDto, EntityType extends IdentifiedEntity>
		implements CrudFacade<ViewTypeDto, CreateTypeDto> {

	private final CrudService<EntityType> crudService;

	private final CrudMapper<ViewTypeDto, CreateTypeDto, EntityType> crudMapper;

	private final Class<EntityType> entityClass;

	public GenericFacade(CrudService<EntityType> crudService,
			CrudMapper<ViewTypeDto, CreateTypeDto, EntityType> crudMapper, Class<EntityType> entityClass) {
		this.crudService = crudService;
		this.crudMapper = crudMapper;
		this.entityClass = entityClass;
	}

	@Override
	public ViewTypeDto create(CreateTypeDto createDto) {
		ValidationHelper.requireNonNull(createDto, "%s create DTO cannot be null!".formatted(getEntityName()));

		final EntityType entity = crudMapper.fromCreateToEntity(createDto);
		entity.setUid(UUIDUtils.generate());

		final EntityType createdEntity = crudService.create(entity);

		return crudMapper.fromEntityToView(createdEntity);
	}

	@Override
	public Optional<ViewTypeDto> get(UUID uid) {
		ValidationHelper.requireNonNull(uid, "Entity uid must not be null!");

		return crudService.get(uid).map(crudMapper::fromEntityToView);
	}

	@Override
	@Transactional
	public PagedModel<ViewTypeDto> getAll(Pageable pageable) {
		ValidationHelper.requireNonNull(pageable, "Pageable object cannot be null!");

		List<EntityType> entities = crudService.getAll(pageable);
		List<ViewTypeDto> viewDtoList = crudMapper.fromEntityListToView(entities);

		PageImpl<ViewTypeDto> viewPage = new PageImpl<>(viewDtoList, pageable, crudService.count());
		return new PagedModel<>(viewPage);
	}

	@Override
	public ViewTypeDto update(UUID uid, CreateTypeDto createDto) {
		ValidationHelper.requireNonNull(uid, "%s UUID cannot be null!".formatted(getEntityName()));
		ValidationHelper.requireNonNull(createDto, "%s create DTO cannot be null!".formatted(getEntityName()));

		final EntityType entity = crudMapper.fromCreateToEntity(uid, createDto);
		final EntityType updatedEntity = crudService.update(entity);

		return crudMapper.fromEntityToView(updatedEntity);
	}

	@Override
	public void delete(UUID uid) {
		ValidationHelper.requireNonNull(uid, "%s UUID cannot be null!".formatted(getEntityName()));

		crudService.delete(uid);
	}

	private String getEntityName() {
		return entityClass.getSimpleName();
	}

}
