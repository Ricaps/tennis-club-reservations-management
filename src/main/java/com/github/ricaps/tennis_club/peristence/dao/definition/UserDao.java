package com.github.ricaps.tennis_club.peristence.dao.definition;

import com.github.ricaps.tennis_club.peristence.entity.User;

import java.util.Optional;

public interface UserDao extends CrudDao<User> {

	Optional<User> findByPhoneNumber(String phoneNumber);
}
