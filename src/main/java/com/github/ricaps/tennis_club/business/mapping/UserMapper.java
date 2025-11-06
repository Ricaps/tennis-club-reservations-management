package com.github.ricaps.tennis_club.business.mapping;

import com.github.ricaps.tennis_club.api.user.UserCreateDto;
import com.github.ricaps.tennis_club.api.user.UserViewDto;
import com.github.ricaps.tennis_club.peristence.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends CrudMapper<UserViewDto, UserCreateDto, User> {

}
