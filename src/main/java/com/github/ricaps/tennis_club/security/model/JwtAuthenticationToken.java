package com.github.ricaps.tennis_club.security.model;

import com.github.ricaps.tennis_club.peristence.entity.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private final User principal;

	private final String jwt;

	public JwtAuthenticationToken(User principal, String jwt, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.jwt = jwt;
	}

	@Override
	public Object getCredentials() {
		return principal;
	}

	@Override
	public Object getPrincipal() {
		return jwt;
	}

}
