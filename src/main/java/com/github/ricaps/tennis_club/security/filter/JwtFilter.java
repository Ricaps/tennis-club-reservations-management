package com.github.ricaps.tennis_club.security.filter;

import com.github.ricaps.tennis_club.business.service.definition.UserService;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.security.JwtUtils;
import com.github.ricaps.tennis_club.security.model.JwtAuthenticationToken;
import com.github.ricaps.tennis_club.security.model.JwtUser;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtFilter extends OncePerRequestFilter {

	public static final String BEARER_PREFIX = "Bearer ";

	private final JwtUtils jwtUtils;

	private final UserService userService;

	public JwtFilter(JwtUtils jwtUtils, UserService userService) {
		this.jwtUtils = jwtUtils;
		this.userService = userService;
	}

	@Override
	protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
			@Nonnull FilterChain filterChain) throws ServletException, IOException {

		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
			doFilter(request, response, filterChain);
			return;
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String jwtToken = authorizationHeader.substring(BEARER_PREFIX.length());
		Optional<Claims> claimsOptional = jwtUtils.extractClaims(jwtToken);

		if (claimsOptional.isEmpty()) {
			doFilter(request, response, filterChain);
			return;
		}

		Claims claims = claimsOptional.get();

		if (authentication == null || !authentication.isAuthenticated()) {
			Optional<User> userOptional = userService.get(UUID.fromString(claims.getSubject()));

			if (userOptional.isEmpty()) {
				doFilter(request, response, filterChain);
				return;
			}

			User user = userOptional.get();
			Collection<SimpleGrantedAuthority> authorities = jwtUtils.extractAuthorities(claims);
			JwtUser jwtUser = new JwtUser(user, authorities);
			JwtAuthenticationToken token = new JwtAuthenticationToken(jwtUser, jwtToken);
			token.setAuthenticated(true);

			SecurityContextHolder.getContext().setAuthentication(token);

		}
		doFilter(request, response, filterChain);
	}

}
