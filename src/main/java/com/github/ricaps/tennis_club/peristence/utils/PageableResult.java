package com.github.ricaps.tennis_club.peristence.utils;

import com.github.ricaps.tennis_club.peristence.entity.IdentifiedEntity;

import java.util.List;

public record PageableResult<T extends IdentifiedEntity>(List<T> data, long totalCount) {
}
