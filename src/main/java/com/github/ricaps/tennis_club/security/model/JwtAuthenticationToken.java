package com.github.ricaps.tennis_club.security.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Objects;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private final JwtUser principal;

	private final String jwt;

	public JwtAuthenticationToken(JwtUser principal, String jwt) {
		super(principal.authorities());
		this.principal = principal;
		this.jwt = jwt;
	}

	@Override
	public Object getCredentials() {
		return jwt;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof JwtAuthenticationToken that))
			return false;
		if (!super.equals(object))
			return false;
		return Objects.equals(getPrincipal(), that.getPrincipal()) && Objects.equals(jwt, that.jwt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getPrincipal(), jwt);
	}
}
