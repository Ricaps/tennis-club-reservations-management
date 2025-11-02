package com.github.ricaps.tennis_club.business.facade.definition;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import java.util.Optional;
import java.util.UUID;

public interface CrudFacade<ViewTypeDto, CreateTypeDto> {

	ViewTypeDto create(CreateTypeDto createTypeDto);

	Optional<ViewTypeDto> get(UUID uid);

	PagedModel<ViewTypeDto> getAll(Pageable pageable);

	ViewTypeDto update(UUID uid, CreateTypeDto createTypeDto);

	void delete(UUID uid);

}
