package com.github.ricaps.tennis_club.business.mapping;

import com.github.ricaps.tennis_club.api.reservation.ReservationCreateDto;
import com.github.ricaps.tennis_club.api.reservation.ReservationViewDto;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.Reservation;
import com.github.ricaps.tennis_club.peristence.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper extends CrudMapper<ReservationViewDto, ReservationCreateDto, Reservation> {

	@Mapping(target = "uid", ignore = true)
	Reservation fromCreateToEntity(Court court, User user, ReservationCreateDto reservationCreateDto);

}
