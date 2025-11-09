package com.github.ricaps.tennis_club.api.reservation;

import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;

public record ReservationPhoneDateQueryDto(String phoneNumber, OffsetDateTime fromTime, Pageable pageable) {
}
