package com.github.ricaps.tennis_club.security;

import com.github.ricaps.tennis_club.security.model.JwtUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public class SecurityUtils {

	private SecurityUtils() {
		super();
	}

	public static Optional<UUID> getCurrentUserUid() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return Optional.empty();
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof JwtUser user) {
			return Optional.of(user.user().getUid());
		}

		return Optional.empty();

	}

}
