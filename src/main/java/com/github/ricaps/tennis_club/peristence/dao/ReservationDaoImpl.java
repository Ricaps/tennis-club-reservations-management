package com.github.ricaps.tennis_club.peristence.dao;

import com.github.ricaps.tennis_club.peristence.dao.definition.ReservationDao;
import com.github.ricaps.tennis_club.peristence.entity.Reservation;
import com.github.ricaps.tennis_club.peristence.utils.PageableResult;
import com.github.ricaps.tennis_club.utils.ValidationHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class ReservationDaoImpl extends AbstractDao<Reservation> implements ReservationDao {

	private final EntityManager entityManager;

	public ReservationDaoImpl(EntityManager entityManager) {
		super(entityManager);
		this.entityManager = entityManager;
	}

	private static Predicate buildCourtUidPredicate(UUID courtUid, CriteriaBuilder criteriaBuilder,
			Root<Reservation> root) {
		return criteriaBuilder.equal(root.get("court").get("uid"), courtUid);
	}

	private static Predicate buildPhoneNumberPredicate(String phoneNumber, OffsetDateTime fromTime,
			CriteriaBuilder criteriaBuilder, Root<Reservation> root) {
		Predicate phoneNumberPredicate = criteriaBuilder.equal(root.get("user").get("phoneNumber"), phoneNumber);
		Predicate onlyFuturePredicate = criteriaBuilder.greaterThan(root.get("fromTime"), fromTime);

		return criteriaBuilder.and(phoneNumberPredicate, onlyFuturePredicate);
	}

	@Override
	protected Class<Reservation> getEntityClass() {
		return Reservation.class;
	}

	@Override
	public List<Reservation> getReservationsAtTimeFrame(OffsetDateTime from, OffsetDateTime to, UUID courtID) {
		ValidationHelper.requireNonNull(from, "From cannot be null!");
		ValidationHelper.requireNonNull(to, "To cannot be null!");
		ValidationHelper.requireNonNull(courtID, "Court ID cannot be null!");

		TypedQuery<Reservation> query = entityManager
			.createQuery("FROM Reservation r WHERE ((:from >= fromTime AND :from <= toTime) OR "
					+ "(:to >= fromTime AND :to <= toTime) OR " + "(:from <= fromTime AND :to >= toTime)) "
					+ "AND court.id = :courtUid", Reservation.class);

		query.setParameter("from", from);
		query.setParameter("to", to);
		query.setParameter("courtUid", courtID);

		return query.getResultList();
	}

	@Override
	@Transactional
	public PageableResult<Reservation> getReservationsAtCourt(UUID courtUid, int pageNumber, int pageSize, Sort sort) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Reservation> criteriaQuery = criteriaBuilder.createQuery(getEntityClass());
		Root<Reservation> root = criteriaQuery.from(getEntityClass());

		Predicate courtUIDPredicate = buildCourtUidPredicate(courtUid, criteriaBuilder, root);
		criteriaQuery.where(courtUIDPredicate);

		TypedQuery<Reservation> typedQuery = applyPagingToQuery(pageNumber, pageSize, sort, criteriaBuilder, root,
				criteriaQuery);

		return new PageableResult<>(typedQuery.getResultList(),
				count((cb, builderRoot) -> buildCourtUidPredicate(courtUid, cb, builderRoot)));
	}

	@Override
	@Transactional
	public PageableResult<Reservation> getReservationsByPhoneNumber(String phoneNumber, OffsetDateTime fromTime,
			int pageNumber, int pageSize, Sort sort) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Reservation> criteriaQuery = criteriaBuilder.createQuery(getEntityClass());
		Root<Reservation> root = criteriaQuery.from(getEntityClass());

		Predicate courtUIDPredicate = buildPhoneNumberPredicate(phoneNumber, fromTime, criteriaBuilder, root);
		criteriaQuery.where(courtUIDPredicate);

		TypedQuery<Reservation> typedQuery = applyPagingToQuery(pageNumber, pageSize, sort, criteriaBuilder, root,
				criteriaQuery);

		return new PageableResult<>(typedQuery.getResultList(),
				count((cb, builderRoot) -> buildPhoneNumberPredicate(phoneNumber, fromTime, cb, builderRoot)));
	}

}
