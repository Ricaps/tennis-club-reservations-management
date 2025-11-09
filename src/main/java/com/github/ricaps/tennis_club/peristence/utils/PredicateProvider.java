package com.github.ricaps.tennis_club.peristence.utils;

import com.github.ricaps.tennis_club.peristence.entity.IdentifiedEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@FunctionalInterface
public interface PredicateProvider<EntityType extends IdentifiedEntity> {

	/**
	 * Construct predicate based on the given criteria builder and entity path root
	 * @param criteriaBuilder criteria builder
	 * @param root entity path root
	 * @return predicate that can be used in a criteria query
	 */
	Predicate createPredicate(CriteriaBuilder criteriaBuilder, Root<EntityType> root);

}
