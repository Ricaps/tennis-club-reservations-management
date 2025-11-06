package com.github.ricaps.tennis_club.security;

import com.github.ricaps.tennis_club.peristence.entity.Role;
import com.github.ricaps.tennis_club.security.filter.JwtFilter;
import com.github.ricaps.tennis_club.security.handler.RestAccessDeniedHandler;
import com.github.ricaps.tennis_club.security.handler.RestEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtFilter jwtFilter;

	public SecurityConfig(JwtFilter jwtFilter) {
		this.jwtFilter = jwtFilter;
	}

	@Bean
	@Order(1)
	public SecurityFilterChain defaultFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			.authorizeHttpRequests(requests -> requests.requestMatchers("/v1/auth/**")
				.permitAll()
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml")
				.permitAll()
				.requestMatchers(HttpMethod.GET)
				.authenticated()
				.anyRequest()
				.hasRole(Role.ADMIN.name()))
			.formLogin(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.exceptionHandling(exceptionHandlers -> {
				exceptionHandlers.authenticationEntryPoint(new RestEntryPoint());
				exceptionHandlers.accessDeniedHandler(new RestAccessDeniedHandler());
			})
			.sessionManagement(
					sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return httpSecurity.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
