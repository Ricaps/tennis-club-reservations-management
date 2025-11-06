package com.github.ricaps.tennis_club.security;

import com.github.ricaps.tennis_club.configuration.model.JwtConfiguration;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.utils.UUIDUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtils {

	public static final String ROLES_CLAIM = "roles";

	public static final String ROLE_PREFIX = "ROLE_";

	private final JwtConfiguration jwtConfiguration;

	public JwtUtils(JwtConfiguration jwtConfiguration) {
		this.jwtConfiguration = jwtConfiguration;
	}

	public String generateAccessToken(User user) {
		final Map<String, Object> claims = new HashMap<>();
		claims.put(ROLES_CLAIM, getRoles(user));

		return Jwts.builder()
			.id(UUIDUtils.generate().toString())
			.signWith(Keys.hmacShaKeyFor(jwtConfiguration.getSecret().getBytes()))
			.claims(claims)
			.subject(user.getUid().toString())
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + jwtConfiguration.getExpiration()))
			.compact();
	}

	public Optional<Claims> extractClaims(String token) {
		try {
			Claims claims = Jwts.parser()
				.verifyWith(Keys.hmacShaKeyFor(jwtConfiguration.getSecret().getBytes()))
				.build()
				.parseSignedClaims(token)
				.getPayload();

			return Optional.of(claims);
		}
		catch (SignatureException e) {
			log.warn("The signature for token {} is invalid!", token, e);
		}
		catch (ExpiredJwtException e) {
			log.warn("Token {} is already invalid!", token, e);
		}

		return Optional.empty();

	}

	private Set<String> getRoles(User user) {
		return user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
	}

	@SuppressWarnings("unchecked")
	public Collection<SimpleGrantedAuthority> extractAuthorities(Claims claims) {
		Object claimsObject = claims.get(ROLES_CLAIM);
		List<String> roles = (List<String>) claimsObject;

		return roles.stream().map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role)).toList();
	}

}
