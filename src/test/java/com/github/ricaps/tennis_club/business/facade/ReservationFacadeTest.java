// package com.github.ricaps.tennis_club.business.facade;
//
// import com.github.ricaps.tennis_club.api.reservation.ReservationCreateDto;
// import com.github.ricaps.tennis_club.api.reservation.ReservationViewDto;
// import com.github.ricaps.tennis_club.business.mapping.ReservationMapper;
// import com.github.ricaps.tennis_club.business.service.definition.CourtService;
// import com.github.ricaps.tennis_club.business.service.definition.ReservationService;
// import com.github.ricaps.tennis_club.business.service.definition.SurfaceService;
// import com.github.ricaps.tennis_club.business.service.definition.UserService;
// import com.github.ricaps.tennis_club.exception.ValueIsMissingException;
// import com.github.ricaps.tennis_club.peristence.entity.Court;
// import com.github.ricaps.tennis_club.peristence.entity.Surface;
// import com.github.ricaps.tennis_club.test_utils.ReservationTestData;
// import com.github.ricaps.tennis_club.test_utils.SurfaceTestData;
// import com.github.ricaps.tennis_club.utils.UUIDUtils;
// import org.junit.jupiter.api.Test;
// import org.mapstruct.factory.Mappers;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.web.PagedModel;
//
// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;
//
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
// class ReservationFacadeTest {
// private final ReservationMapper reservationMapper =
// Mockito.spy(Mappers.getMapperClass(ReservationMapper.class));
//
// @Mock
// private ReservationService reservationService;
//
// @Mock
// private CourtService courtService;
//
// @Mock
// private UserService userService;
//
// @InjectMocks
// private ReservationFacade reservationFacade;
//
// @Test
// void create_nullEntity_throwsException() {
// assertThatThrownBy(() ->
// reservationFacade.create(null)).isInstanceOf(ValueIsMissingException.class);
//
// Mockito.verify(reservationService, Mockito.never()).create(Mockito.any());
// }
//
// @Test
// void create_allPropertiesGood_creationSuccessful() {
// ReservationCreateDto createDto = ReservationTestData.createCourt(UUID.randomUUID());
// Surface surface = SurfaceTestData.createSurface(createDto.surfaceUid());
// Court entity = Mockito.spy(reservationMapper.fromCreateToEntity(createDto));
// entity.setSurface(surface);
//
// Mockito.reset(reservationMapper);
//
// Mockito.when(courtService.getReference(surface.getUid())).thenReturn(surface);
// Mockito.when(reservationMapper.fromCreateToEntity(createDto,
// surface)).thenReturn(entity);
// Mockito.when(reservationService.create(Mockito.any())).thenReturn(entity);
//
// ReservationViewDto view = reservationFacade.create(createDto);
//
// ReservationTestData.compareViewAndCreate(view, createDto, surface);
// Mockito.verify(entity, Mockito.times(1)).setUid(Mockito.any());
// Mockito.verify(reservationMapper, Mockito.times(1)).fromCreateToEntity(createDto,
// surface);
// Mockito.verify(reservationService, Mockito.times(1)).create(entity);
// }
//
// @Test
// void get_uidNull_throwsException() {
// assertThatThrownBy(() ->
// reservationFacade.get(null)).isInstanceOf(ValueIsMissingException.class);
//
// Mockito.verify(reservationService, Mockito.never()).get(Mockito.any());
// }
//
// @Test
// void get_allGood_returnsOptionalValue() {
// ReservationViewDto view = ReservationTestData.viewCourt(UUID.randomUUID());
// Court entity = Mockito.mock(Court.class);
// Mockito.when(reservationService.get(view.uid())).thenReturn(Optional.of(entity));
// Mockito.when(reservationMapper.fromEntityToView(entity)).thenReturn(view);
//
// Optional<ReservationViewDto> returnedView = reservationFacade.get(view.uid());
//
// assertThat(returnedView).isPresent().get().isEqualTo(view);
// Mockito.verify(reservationService, Mockito.times(1)).get(view.uid());
// }
//
// @Test
// void get_notFound_returnsEmptyValue() {
// ReservationViewDto view = ReservationTestData.viewCourt(UUID.randomUUID());
// Mockito.when(reservationService.get(view.uid())).thenReturn(Optional.empty());
//
// Optional<ReservationViewDto> returnedView = reservationFacade.get(view.uid());
//
// assertThat(returnedView).isEmpty();
// Mockito.verify(reservationMapper, Mockito.never()).fromEntityToView(Mockito.any());
// Mockito.verify(reservationService, Mockito.times(1)).get(view.uid());
// }
//
// @Test
// void getAll_nullPageable_throwsException() {
// assertThatThrownBy(() ->
// reservationFacade.getAll(null)).isInstanceOf(ValueIsMissingException.class);
//
// Mockito.verify(reservationService, Mockito.never()).getAll(Mockito.any());
// }
//
// @Test
// void getAll_allGood_returnsPage() {
// ReservationViewDto view1 = ReservationTestData.viewCourt(UUID.randomUUID());
// ReservationViewDto view2 = ReservationTestData.viewCourt(UUID.randomUUID());
// List<ReservationViewDto> viewList = List.of(view1, view2);
//
// Pageable pageableMock = Mockito.mock(Pageable.class);
//
// Court entity1 = Mockito.mock(Court.class);
// Court entity2 = Mockito.mock(Court.class);
// List<Court> entitiesList = List.of(entity1, entity2);
// Mockito.when(reservationService.getAll(pageableMock)).thenReturn(entitiesList);
// Mockito.when(reservationMapper.fromEntityListToView(entitiesList)).thenReturn(viewList);
//
// PagedModel<ReservationViewDto> returnedView = reservationFacade.getAll(pageableMock);
//
// assertThat(returnedView.getContent()).hasSize(2);
// assertThat(returnedView.getContent()).containsAll(viewList);
// Mockito.verify(reservationService, Mockito.times(1)).getAll(pageableMock);
// }
//
// @Test
// void update_nullEntity_throwsException() {
// assertThatThrownBy(() -> reservationFacade.update(UUID.randomUUID(), null))
// .isInstanceOf(ValueIsMissingException.class);
//
// Mockito.verify(reservationService, Mockito.never()).update(Mockito.any());
// }
//
// @Test
// void update_nullUuid_throwsException() {
// assertThatThrownBy(() -> reservationFacade.update(null,
// ReservationTestData.createCourt(UUIDUtils.generate())))
// .isInstanceOf(ValueIsMissingException.class);
//
// Mockito.verify(reservationService, Mockito.never()).update(Mockito.any());
// }
//
// @Test
// void update_allPropertiesGood_updateSuccessful() {
// // Prepare
// ReservationCreateDto createDto = ReservationTestData.createCourt(UUIDUtils.generate());
// UUID uuid = UUIDUtils.generate();
// Surface surfaceReference = SurfaceTestData.createSurface(createDto.surfaceUid());
//
// // Mock
// Court entity = Mockito.spy(reservationMapper.fromCreateToEntity(uuid, createDto,
// surfaceReference));
// Court updatedEntity = Mockito.spy(reservationMapper.fromCreateToEntity(uuid, createDto,
// surfaceReference));
// updatedEntity.setName("New name");
// Mockito.reset(reservationMapper);
//
// Mockito.when(courtService.getReference(createDto.surfaceUid())).thenReturn(surfaceReference);
// Mockito.when(reservationMapper.fromCreateToEntity(uuid, createDto,
// surfaceReference)).thenReturn(entity);
// Mockito.when(reservationService.update(entity)).thenReturn(updatedEntity);
//
// // Run
// ReservationViewDto view = reservationFacade.update(uuid, createDto);
//
// // Asert
// assertThat(view.uid()).isEqualTo(uuid);
// assertThat(view.name()).isEqualTo(updatedEntity.getName());
// Mockito.verify(reservationMapper, Mockito.times(1)).fromCreateToEntity(uuid, createDto,
// surfaceReference);
// Mockito.verify(reservationService, Mockito.times(1)).update(entity);
// }
//
// @Test
// void delete_nullUuid_throwsException() {
// assertThatThrownBy(() ->
// reservationFacade.delete(null)).isInstanceOf(ValueIsMissingException.class);
//
// Mockito.verify(reservationService, Mockito.never()).delete(Mockito.any());
// }
//
// @Test
// void delete_correctValue_deleted() {
// UUID uuid = UUIDUtils.generate();
// reservationFacade.delete(uuid);
//
// Mockito.verify(reservationService, Mockito.times(1)).delete(uuid);
// }
// }