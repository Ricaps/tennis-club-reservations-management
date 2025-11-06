package com.github.ricaps.tennis_club.business.service;

import com.github.ricaps.tennis_club.business.service.definition.GenericService;
import com.github.ricaps.tennis_club.business.service.definition.UserService;
import com.github.ricaps.tennis_club.exception.EntityExistsException;
import com.github.ricaps.tennis_club.exception.EntityNotExistsException;
import com.github.ricaps.tennis_club.exception.ValueIsMissingException;
import com.github.ricaps.tennis_club.peristence.dao.definition.UserDao;
import com.github.ricaps.tennis_club.peristence.entity.Role;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.utils.ValidationHelper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

	private final GenericService<User> genericService;

	private final UserDao userDao;

	public UserServiceImpl(UserDao userDao) {
		this.genericService = new GenericService<>(userDao, User.class);
		this.userDao = userDao;
	}

	private static void checkRolesNotEmpty(User entity) {
		Set<Role> roles = entity.getRoles();

		if (roles == null || roles.isEmpty()) {
			throw new ValueIsMissingException("User must include roles for creation!");
		}
	}

	@Override
	public User create(User entity) {
		ValidationHelper.requireNonNull(entity, "User cannot be null!");
		checkRolesNotEmpty(entity);
		checkPhoneNumberUnique(entity);

		return genericService.create(entity);
	}

	@Override
	public Optional<User> get(UUID uid) {
		return genericService.get(uid);
	}

	@Override
	public User getReference(UUID uuid) throws EntityNotExistsException {
		return genericService.getReference(uuid);
	}

	@Override
	public List<User> getAll(Pageable pageable) {
		return genericService.getAll(pageable);
	}

	@Override
	public User update(User entity) {
		ValidationHelper.requireNonNull(entity, "User cannot be null!");
		checkRolesNotEmpty(entity);
		checkPhoneNumberUnique(entity);

		return genericService.update(entity);
	}

	@Override
	public void delete(UUID uid) {
		genericService.delete(uid);
	}

	@Override
	public long count() {
		return genericService.count();
	}

	private void checkPhoneNumberUnique(User entity) {
		Optional<User> user = userDao.findByPhoneNumber(entity.getPhoneNumber());

		if (user.isPresent()) {
			throw new EntityExistsException("User with phone %s already exists!".formatted(entity.getPhoneNumber()));
		}
	}

	@Override
	public Optional<User> getByPhoneNumber(String phoneNumber) {
		ValidationHelper.requireNonNull(phoneNumber, "Phone number cannot be null!");

		return userDao.findByPhoneNumber(phoneNumber);
	}

}
