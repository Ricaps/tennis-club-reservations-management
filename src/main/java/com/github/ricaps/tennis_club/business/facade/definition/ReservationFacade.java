package com.github.ricaps.tennis_club.business.facade.definition;

import com.github.ricaps.tennis_club.api.reservation.ReservationCreateDto;
import com.github.ricaps.tennis_club.api.reservation.ReservationPhoneDateQueryDto;
import com.github.ricaps.tennis_club.api.reservation.ReservationViewDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import java.util.UUID;

public interface ReservationFacade extends CrudFacade<ReservationViewDto, ReservationCreateDto> {

	PagedModel<ReservationViewDto> getAllByCourt(UUID courtUID, Pageable pageable);

	PagedModel<ReservationViewDto> getAllByPhoneNumber(ReservationPhoneDateQueryDto queryDto);

}
