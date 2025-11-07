package com.github.ricaps.tennis_club.security.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private final JwtUser principal;

	private final String jwt;

	public JwtAuthenticationToken(JwtUser principal, String jwt) {
		super(principal.getAuthorities());
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

}
