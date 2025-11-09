package com.github.ricaps.tennis_club.business.facade;

import com.github.ricaps.tennis_club.api.reservation.ReservationCreateDto;
import com.github.ricaps.tennis_club.api.reservation.ReservationPhoneDateQueryDto;
import com.github.ricaps.tennis_club.api.reservation.ReservationViewDto;
import com.github.ricaps.tennis_club.business.facade.definition.GenericFacade;
import com.github.ricaps.tennis_club.business.facade.definition.ReservationFacade;
import com.github.ricaps.tennis_club.business.mapping.ReservationMapper;
import com.github.ricaps.tennis_club.business.service.definition.CourtService;
import com.github.ricaps.tennis_club.business.service.definition.ReservationService;
import com.github.ricaps.tennis_club.business.service.definition.UserService;
import com.github.ricaps.tennis_club.exception.NotAuthenticatedException;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.Reservation;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.peristence.utils.PageableResult;
import com.github.ricaps.tennis_club.security.SecurityUtils;
import com.github.ricaps.tennis_club.utils.UUIDUtils;
import com.github.ricaps.tennis_club.utils.ValidationHelper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReservationFacadeImpl implements ReservationFacade {

	private final ReservationService reservationService;

	private final ReservationMapper reservationMapper;

	private final CourtService courtService;

	private final UserService userService;

	private final GenericFacade<ReservationViewDto, ReservationCreateDto, Reservation> genericFacade;

	public ReservationFacadeImpl(ReservationService reservationService, ReservationMapper reservationMapper,
			CourtService courtService, UserService userService) {
		this.reservationService = reservationService;
		this.reservationMapper = reservationMapper;
		this.courtService = courtService;
		this.userService = userService;
		this.genericFacade = new GenericFacade<>(reservationService, reservationMapper, Reservation.class);
	}

	@Override
	public ReservationViewDto create(ReservationCreateDto reservationCreateDto) {
		Reservation savableEntity = getSavableEntity(reservationCreateDto);
		savableEntity.setUid(UUIDUtils.generate());
		final Reservation created = reservationService.create(savableEntity);

		return reservationMapper.fromEntityToView(created);
	}

	private Reservation getSavableEntity(ReservationCreateDto reservationCreateDto) {
		ValidationHelper.requireNonNull(reservationCreateDto, "Reservation create DTO cannot be null!");

		final Court courtReference = courtService.getReference(reservationCreateDto.courtUid());
		final UUID userUUID = SecurityUtils.getCurrentUserUid()
			.orElseThrow(() -> new NotAuthenticatedException("User must be authenticated!"));
		final User userReference = userService.getReference(userUUID);

		return reservationMapper.fromCreateToEntity(courtReference, userReference, reservationCreateDto);
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
		ValidationHelper.requireNonNull(uid, "UID of updated entity cannot be null!");

		Reservation savableEntity = getSavableEntity(reservationCreateDto);
		savableEntity.setUid(uid);
		final Reservation updated = reservationService.update(savableEntity);

		return reservationMapper.fromEntityToView(updated);
	}

	@Override
	public void delete(UUID uid) {
		genericFacade.delete(uid);
	}

	@Override
	public PagedModel<ReservationViewDto> getAllByCourt(UUID courtUID, Pageable pageable) {
		ValidationHelper.requireNonNull(courtUID, "UID cannot be null!");
		ValidationHelper.requireNonNull(pageable, "Pageable cannot be null!");

		PageableResult<Reservation> reservations = reservationService.getAllByCourt(courtUID, pageable);
		List<ReservationViewDto> views = reservationMapper.fromEntityListToView(reservations.data());

		return new PagedModel<>(new PageImpl<>(views, pageable, reservations.totalCount()));
	}

	@Override
	public PagedModel<ReservationViewDto> getAllByPhoneNumber(ReservationPhoneDateQueryDto queryDto) {
		ValidationHelper.requireNonNull(queryDto, "Reservation query cannot be null!");
		ValidationHelper.requireNonNull(queryDto.phoneNumber(), "Phone number cannot be null!");
		ValidationHelper.requireNonNull(queryDto.pageable(), "Pageable cannot be null!");

		PageableResult<Reservation> reservations = reservationService.getAllByPhoneNumber(queryDto.phoneNumber(),
				queryDto.fromTime(), queryDto.pageable());
		List<ReservationViewDto> views = reservationMapper.fromEntityListToView(reservations.data());

		return new PagedModel<>(new PageImpl<>(views, queryDto.pageable(), reservations.totalCount()));
	}

}
