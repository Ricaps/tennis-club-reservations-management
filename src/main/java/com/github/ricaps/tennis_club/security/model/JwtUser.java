package com.github.ricaps.tennis_club.security.model;

import com.github.ricaps.tennis_club.peristence.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

public record JwtUser(User user, Collection<? extends SimpleGrantedAuthority> authorities) {

	@Override
	public String toString() {
		return "JwtUser{" + "userID=" + user.getUid() + ", authorities=" + authorities + '}';
	}

}
