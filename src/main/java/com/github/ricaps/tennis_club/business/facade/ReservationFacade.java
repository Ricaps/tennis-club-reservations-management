package com.github.ricaps.tennis_club.business.facade;

import com.github.ricaps.tennis_club.api.reservation.ReservationCreateDto;
import com.github.ricaps.tennis_club.api.reservation.ReservationViewDto;
import com.github.ricaps.tennis_club.business.facade.definition.CrudFacade;
import com.github.ricaps.tennis_club.business.facade.definition.GenericFacade;
import com.github.ricaps.tennis_club.business.mapping.ReservationMapper;
import com.github.ricaps.tennis_club.business.service.definition.CourtService;
import com.github.ricaps.tennis_club.business.service.definition.ReservationService;
import com.github.ricaps.tennis_club.business.service.definition.UserService;
import com.github.ricaps.tennis_club.exception.NotAuthenticatedException;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.Reservation;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.security.SecurityUtils;
import com.github.ricaps.tennis_club.utils.UUIDUtils;
import com.github.ricaps.tennis_club.utils.ValidationHelper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ReservationFacade implements CrudFacade<ReservationViewDto, ReservationCreateDto> {

	private final ReservationService reservationService;

	private final ReservationMapper reservationMapper;

	private final CourtService courtService;

	private final UserService userService;

	private final GenericFacade<ReservationViewDto, ReservationCreateDto, Reservation> genericFacade;

	public ReservationFacade(ReservationService reservationService, ReservationMapper reservationMapper,
			CourtService courtService, UserService userService) {
		this.reservationService = reservationService;
		this.reservationMapper = reservationMapper;
		this.courtService = courtService;
		this.userService = userService;
		this.genericFacade = new GenericFacade<>(reservationService, reservationMapper, Reservation.class);
	}

	@Override
	public ReservationViewDto create(ReservationCreateDto reservationCreateDto) {
		final Reservation created = reservationService.create(getSavableEntity(reservationCreateDto));

		return reservationMapper.fromEntityToView(created);
	}

	private Reservation getSavableEntity(ReservationCreateDto reservationCreateDto) {
		ValidationHelper.requireNonNull(reservationCreateDto, "Reservation create DTO cannot be null!");

		final Court courtReference = courtService.getReference(reservationCreateDto.courtUid());
		final UUID userUUID = SecurityUtils.getCurrentUserUid()
			.orElseThrow(() -> new NotAuthenticatedException("User must be authenticated!"));
		final User userReference = userService.getReference(userUUID);

		final Reservation entity = reservationMapper.fromCreateToEntity(courtReference, userReference,
				reservationCreateDto);
		entity.setUid(UUIDUtils.generate());

		return entity;
	}

	@Override
	public Optional<ReservationViewDto> get(UUID uid) {
		return genericFacade.get(uid);
	}

	@Override
	public PagedModel<ReservationViewDto> getAll(Pageable pageable) {
		return genericFacade.getAll(pageable);
	}

	@Override
	public ReservationViewDto update(UUID uid, ReservationCreateDto reservationCreateDto) {
		final Reservation updated = reservationService.update(getSavableEntity(reservationCreateDto));

		return reservationMapper.fromEntityToView(updated);
	}

	@Override
	public void delete(UUID uid) {
		genericFacade.delete(uid);
	}

}
