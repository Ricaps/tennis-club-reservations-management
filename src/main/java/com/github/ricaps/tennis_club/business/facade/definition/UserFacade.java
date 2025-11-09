package com.github.ricaps.tennis_club.business.facade.definition;

import com.github.ricaps.tennis_club.api.user.UserCreateDto;
import com.github.ricaps.tennis_club.api.user.UserDetailedView;

public interface UserFacade extends CrudFacade<UserDetailedView, UserCreateDto> {

}
