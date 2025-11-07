package com.github.ricaps.tennis_club.security.model;

import com.github.ricaps.tennis_club.peristence.entity.User;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

@Getter
public class JwtUser {

	private final User user;

	private final Collection<? extends SimpleGrantedAuthority> authorities;

	public JwtUser(User user, Collection<? extends SimpleGrantedAuthority> authorities) {
		this.user = user;
		this.authorities = authorities;
	}

	@Override
	public String toString() {
		return "JwtUser{" + "userID=" + user.getUid() + ", authorities=" + authorities + '}';
	}

}
