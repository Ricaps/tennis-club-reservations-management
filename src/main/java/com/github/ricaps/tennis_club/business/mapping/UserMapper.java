package com.github.ricaps.tennis_club.business.mapping;

import com.github.ricaps.tennis_club.api.user.UserCreateDto;
import com.github.ricaps.tennis_club.api.user.UserDetailedView;
import com.github.ricaps.tennis_club.api.user.UserRegisterDto;
import com.github.ricaps.tennis_club.peristence.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper extends CrudMapper<UserDetailedView, UserCreateDto, User> {

	@Mapping(target = "uid", ignore = true)
	@Mapping(target = "roles", ignore = true)
	User fromRegisterToEntity(UserRegisterDto userRegisterDto);

}
