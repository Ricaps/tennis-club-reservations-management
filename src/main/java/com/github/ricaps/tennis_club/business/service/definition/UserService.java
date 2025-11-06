package com.github.ricaps.tennis_club.business.service.definition;

import com.github.ricaps.tennis_club.peristence.entity.User;

import java.util.Optional;

public interface UserService extends CrudService<User> {

	Optional<User> getByPhoneNumber(String phoneNumber);
}
