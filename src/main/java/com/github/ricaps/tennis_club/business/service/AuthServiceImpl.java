package com.github.ricaps.tennis_club.business.service;

import com.github.ricaps.tennis_club.api.user.UserRegisterDto;
import com.github.ricaps.tennis_club.business.mapping.UserMapper;
import com.github.ricaps.tennis_club.business.service.definition.AuthService;
import com.github.ricaps.tennis_club.business.service.definition.UserService;
import com.github.ricaps.tennis_club.exception.AuthenticationFailed;
import com.github.ricaps.tennis_club.peristence.entity.Role;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.security.JwtUtils;
import com.github.ricaps.tennis_club.utils.UUIDUtils;
import com.github.ricaps.tennis_club.utils.ValidationHelper;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

@Component
@Slf4j
public class AuthServiceImpl implements AuthService {

	public static final String BASIC_PREFIX = "Basic ";
	private static final Role DEFAULT_ROLE = Role.USER;
	private static final String WRONG_COMBINATION_ERROR = "This combination of user and password doesn't exist!";

	private final UserService userService;

	private final UserMapper userMapper;

	private final PasswordEncoder passwordEncoder;

	private final JwtUtils jwtUtils;

	public AuthServiceImpl(UserService userService, UserMapper userMapper, PasswordEncoder passwordEncoder,
			JwtUtils jwtUtils) {
		this.userService = userService;
		this.userMapper = userMapper;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtils = jwtUtils;
	}

	private static byte[] decodeBase64(String encodedCredentials) {
		try {
			return Base64.getDecoder().decode(encodedCredentials);
		}
		catch (IllegalArgumentException e) {
			throw new ValidationException("Basic auth value is not Base64 encoded", e);
		}
	}

	@Override
	public String register(UserRegisterDto registerDto) {
		ValidationHelper.requireNonNull(registerDto, "User register DTO cannot be null!");

		final User entity = userMapper.fromRegisterToEntity(registerDto);
		entity.setUid(UUIDUtils.generate());
		entity.setPassword(passwordEncoder.encode(entity.getPassword()));
		entity.setRoles(Set.of(DEFAULT_ROLE));

		final User createdEntity = userService.create(entity);
		String token = jwtUtils.generateAccessToken(createdEntity);

		log.info("User {} successfully registered!", entity.getPhoneNumber());
		return token;
	}

	@Override
	public String login(String phoneNumber, String password) {
		ValidationHelper.requireNonNull(phoneNumber, "Phone number cannot be null!");
		ValidationHelper.requireNonNull(password, "Password cannot be null!");

		User user = userService.getByPhoneNumber(phoneNumber)
			.orElseThrow(() -> new AuthenticationFailed(WRONG_COMBINATION_ERROR));

		boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
		if (!passwordMatches) {
			throw new AuthenticationFailed(WRONG_COMBINATION_ERROR);
		}

		String token = jwtUtils.generateAccessToken(user);

		log.info("User {} successfully logged-in!", user.getPhoneNumber());
		return token;
	}

	@Override
	public String login(String base64AuthorizationHeader) {
		if (!base64AuthorizationHeader.startsWith(BASIC_PREFIX)) {
			throw new ValidationException("Invalid Basic auth header");
		}

		String encodedCredentials = base64AuthorizationHeader.substring(BASIC_PREFIX.length());
		byte[] byteHeader = decodeBase64(encodedCredentials);
		String stringHeader = new String(byteHeader, StandardCharsets.UTF_8);

		String[] split = stringHeader.split(":");
		if (split.length != 2) {
			throw new AuthenticationFailed(WRONG_COMBINATION_ERROR);
		}

		return login(split[0], split[1]);
	}

}
