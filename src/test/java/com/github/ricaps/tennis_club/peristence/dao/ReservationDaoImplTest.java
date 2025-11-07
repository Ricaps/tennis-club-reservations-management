package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.peristence.dao.definition.CrudDao;
import com.github.ricaps.tennis_club.peristence.entity.Court;
import com.github.ricaps.tennis_club.peristence.entity.MoneyAmount;
import com.github.ricaps.tennis_club.peristence.entity.Reservation;
import com.github.ricaps.tennis_club.peristence.entity.Surface;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.test_utils.CourtTestData;
import com.github.ricaps.tennis_club.test_utils.ReservationTestData;
import com.github.ricaps.tennis_club.test_utils.SurfaceTestData;
import com.github.ricaps.tennis_club.test_utils.UserTestData;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;

@Import(ReservationDaoImpl.class)
class ReservationDaoImplTest extends AbstractDaoTest<Reservation> {

	@Autowired
	EntityManager entityManager;

	private Court court;

	private User user;

	@Autowired
	public ReservationDaoImplTest(CrudDao<Reservation> entityDao) {
		super(entityDao);
	}

	@BeforeEach
	void setup() {
		Surface surface = SurfaceTestData.createSurface();
		court = CourtTestData.entity(surface);
		user = UserTestData.entity(true);

		entityManager.persist(surface);
		entityManager.persist(court);
		entityManager.persist(user);

		Mockito.reset(entityManager);
	}

	@Override
	protected Reservation createEntity() {
		return ReservationTestData.entity(court, user);
	}

	@Override
	protected void checkEntity(Reservation actualEntity, Reservation referenceEntity) {
		assertThat(actualEntity.getUid()).isEqualTo(referenceEntity.getUid());
		assertThat(actualEntity.getCourt()).isEqualTo(referenceEntity.getCourt());
		assertThat(actualEntity.getUser()).isEqualTo(referenceEntity.getUser());
		assertThat(actualEntity.getFromTime()).isEqualTo(referenceEntity.getFromTime());
		assertThat(actualEntity.getToTime()).isEqualTo(referenceEntity.getToTime());
		assertThat(actualEntity.getTotalPrice()).isEqualTo(referenceEntity.getTotalPrice());
	}

	@Override
	protected Reservation updateEntity(Reservation entity) {
		entity.setTotalPrice(new MoneyAmount(new BigDecimal("25.5"), Currency.getInstance("CZK")));
		return entity;
	}

}