package com.github.ricaps.tennis_club.business.facade;

import com.github.ricaps.tennis_club.api.user.UserCreateDto;
import com.github.ricaps.tennis_club.api.user.UserViewDto;
import com.github.ricaps.tennis_club.business.facade.definition.CrudFacade;
import com.github.ricaps.tennis_club.business.facade.definition.GenericFacade;
import com.github.ricaps.tennis_club.business.mapping.UserMapper;
import com.github.ricaps.tennis_club.business.service.definition.UserService;
import com.github.ricaps.tennis_club.peristence.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserFacade implements CrudFacade<UserViewDto, UserCreateDto> {

	private final GenericFacade<UserViewDto, UserCreateDto, User> genericFacade;

	public UserFacade(UserService userService, UserMapper userMapper) {
		this.genericFacade = new GenericFacade<>(userService, userMapper, User.class);
	}

	@Override
	public UserViewDto create(UserCreateDto userCreateDto) {
		return genericFacade.create(userCreateDto);
	}

	@Override
	public Optional<UserViewDto> get(UUID uid) {
		return genericFacade.get(uid);
	}

	@Override
	public PagedModel<UserViewDto> getAll(Pageable pageable) {
		return genericFacade.getAll(pageable);
	}

	@Override
	public UserViewDto update(UUID uid, UserCreateDto userCreateDto) {
		return genericFacade.update(uid, userCreateDto);
	}

	@Override
	public void delete(UUID uid) {
		genericFacade.delete(uid);
	}

}
