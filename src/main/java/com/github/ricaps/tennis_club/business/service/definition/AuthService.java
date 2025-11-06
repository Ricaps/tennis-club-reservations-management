package com.github.ricaps.tennis_club.business.service.definition;

import com.github.ricaps.tennis_club.api.user.UserRegisterDto;

public interface AuthService {

	/**
	 * Registers users and creates and JWT token
	 * @param registerDto DTO for user registration
	 * @return generated JWT token
	 */
	String register(UserRegisterDto registerDto);

	/**
	 * Logins user and generate JWT token if the logic was successful
	 * @param phoneNumber user's phone number
	 * @param password user's password
	 * @return generated JWT token
	 */
	String login(String phoneNumber, String password);

	/**
	 * Logins user and generate JWT token if the logic was successful
	 * @param base64AuthorizationHeader Basic Authorization header value
	 * @return generated JWT token
	 */
	String login(String base64AuthorizationHeader);

}
