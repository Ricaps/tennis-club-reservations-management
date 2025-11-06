package com.github.ricaps.tennis_club.business.facade;

import com.github.ricaps.tennis_club.api.user.UserCreateDto;
import com.github.ricaps.tennis_club.api.user.UserViewDto;
import com.github.ricaps.tennis_club.business.facade.definition.CrudFacade;
import com.github.ricaps.tennis_club.business.facade.definition.GenericFacade;
import com.github.ricaps.tennis_club.business.mapping.UserMapper;
import com.github.ricaps.tennis_club.business.service.definition.UserService;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.utils.UUIDUtils;
import com.github.ricaps.tennis_club.utils.ValidationHelper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserFacade implements CrudFacade<UserViewDto, UserCreateDto> {

	private final UserMapper userMapper;

	private final GenericFacade<UserViewDto, UserCreateDto, User> genericFacade;

	private final UserService userService;

	private final PasswordEncoder passwordEncoder;

	public UserFacade(UserService userService, UserMapper userMapper, PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
		this.genericFacade = new GenericFacade<>(userService, userMapper, User.class);
		this.userMapper = userMapper;
		this.userService = userService;
	}

	@Override
	public UserViewDto create(UserCreateDto userCreateDto) {
		ValidationHelper.requireNonNull(userCreateDto, "User register DTO cannot be null!");

		final User entity = userMapper.fromCreateToEntity(userCreateDto);
		entity.setUid(UUIDUtils.generate());
		entity.setPassword(getEncodedPassword(entity));

		final User createdEntity = userService.create(entity);

		return userMapper.fromEntityToView(createdEntity);
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

	private String getEncodedPassword(User entity) {
		return passwordEncoder.encode(entity.getPassword());
	}

}
