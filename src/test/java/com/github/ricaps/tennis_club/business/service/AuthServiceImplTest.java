package com.github.ricaps.tennis_club.business.service;

import com.github.ricaps.tennis_club.api.user.UserRegisterDto;
import com.github.ricaps.tennis_club.business.mapping.UserMapper;
import com.github.ricaps.tennis_club.business.service.definition.UserService;
import com.github.ricaps.tennis_club.exception.AuthenticationFailed;
import com.github.ricaps.tennis_club.exception.ValueIsMissingException;
import com.github.ricaps.tennis_club.peristence.entity.User;
import com.github.ricaps.tennis_club.security.JwtUtils;
import com.github.ricaps.tennis_club.test_utils.UserTestData;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Base64;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

	@Mock
	UserMapper userMapper;

	@Mock
	UserService userService;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	JwtUtils jwtUtils;

	@InjectMocks
	AuthServiceImpl authService;

	@Test
	void register_nullDto_throwsException() {

		assertThatThrownBy(() -> authService.register(null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userService, Mockito.never()).create(Mockito.any());
		Mockito.verify(jwtUtils, Mockito.never()).generateAccessToken(Mockito.any());
	}

	@Test
	void register_allMatches_tokenGenerated() {
		String tokenValue = "token_value";
		String encodedPassword = "encoded_password";
		UserRegisterDto registerDto = UserTestData.createRegister(true);
		User entity = Mockito.spy(UserTestData.entity(true));

		Mockito.when(userService.create(entity)).thenReturn(entity);
		Mockito.when(jwtUtils.generateAccessToken(entity)).thenReturn(tokenValue);
		Mockito.when(passwordEncoder.encode(registerDto.password())).thenReturn(encodedPassword);
		Mockito.when(userMapper.fromRegisterToEntity(registerDto)).thenReturn(entity);
		Mockito.when(entity.getPassword()).thenReturn(registerDto.password());
		Mockito.doNothing().when(entity).setUid(Mockito.any());
		Mockito.doNothing().when(entity).setPassword(Mockito.any());
		Mockito.doNothing().when(entity).setRoles(Mockito.any());

		String token = authService.register(registerDto);

		assertThat(token).isEqualTo(tokenValue);
		Mockito.verify(entity, Mockito.times(1)).setPassword(encodedPassword);
		Mockito.verify(passwordEncoder, Mockito.times(1)).encode(entity.getPassword());
		Mockito.verify(userService, Mockito.times(1)).create(Mockito.any());
		Mockito.verify(jwtUtils, Mockito.times(1)).generateAccessToken(Mockito.any());
	}

	@Test
	void login_phoneNumberNull_throwsException() {
		assertThatThrownBy(() -> authService.login(null, "aaa")).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userService, Mockito.never()).getByPhoneNumber(Mockito.any());
	}

	@Test
	void login_passwordNull_throwsException() {
		assertThatThrownBy(() -> authService.login("123", null)).isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(userService, Mockito.never()).getByPhoneNumber(Mockito.any());
	}

	@Test
	void login_passwordNotMatches_throwsException() {
		String passwordParam = "123";
		String number = "777777777";
		User user = UserTestData.entity();

		Mockito.when(userService.getByPhoneNumber(number)).thenReturn(Optional.of(user));
		Mockito.when(passwordEncoder.matches(passwordParam, user.getPassword())).thenReturn(false);

		assertThatThrownBy(() -> authService.login(number, passwordParam)).isInstanceOf(AuthenticationFailed.class);

		Mockito.verify(jwtUtils, Mockito.never()).generateAccessToken(Mockito.any());
	}

	@Test
	void login_notFoundUser_throwsException() {
		String passwordParam = "123";
		String number = "777777777";

		Mockito.when(userService.getByPhoneNumber(number)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> authService.login(number, passwordParam)).isInstanceOf(AuthenticationFailed.class);

		Mockito.verify(passwordEncoder, Mockito.never()).matches(Mockito.any(), Mockito.any());
		Mockito.verify(jwtUtils, Mockito.never()).generateAccessToken(Mockito.any());
	}

	@Test
	void login_noBase64_throwsException() {
		String number = "Basic abc:cda";

		assertThatThrownBy(() -> authService.login(number)).isInstanceOf(ValidationException.class);

		Mockito.verify(passwordEncoder, Mockito.never()).matches(Mockito.any(), Mockito.any());
		Mockito.verify(jwtUtils, Mockito.never()).generateAccessToken(Mockito.any());
	}

	@Test
	void login_invalidFormatLength1_throwsException() {
		String basicAuth = "Basic " + Base64.getEncoder().encodeToString("Basic usernamepassword".getBytes());

		assertThatThrownBy(() -> authService.login(basicAuth)).isInstanceOf(AuthenticationFailed.class);

		Mockito.verify(passwordEncoder, Mockito.never()).matches(Mockito.any(), Mockito.any());
		Mockito.verify(jwtUtils, Mockito.never()).generateAccessToken(Mockito.any());
	}

	@Test
	void login_invalidFormatLength0_throwsException() {
		String basicAuth = "Basic " + Base64.getEncoder().encodeToString("".getBytes());

		assertThatThrownBy(() -> authService.login(basicAuth)).isInstanceOf(AuthenticationFailed.class);

		Mockito.verify(passwordEncoder, Mockito.never()).matches(Mockito.any(), Mockito.any());
		Mockito.verify(jwtUtils, Mockito.never()).generateAccessToken(Mockito.any());
	}

	@Test
	void login_noBasicPrefix_throwsException() {
		String basicAuth = Base64.getEncoder().encodeToString("abc".getBytes());

		assertThatThrownBy(() -> authService.login(basicAuth)).isInstanceOf(ValidationException.class)
			.hasMessage("Invalid Basic auth header");

		Mockito.verify(passwordEncoder, Mockito.never()).matches(Mockito.any(), Mockito.any());
		Mockito.verify(jwtUtils, Mockito.never()).generateAccessToken(Mockito.any());
	}

	@Test
	void login_basicParamCorrect_tokenGenerated() {
		String phoneNumber = "123456789";
		String password = "password";
		String basicAuth = "Basic "
				+ Base64.getEncoder().encodeToString(String.join(":", phoneNumber, password).getBytes());
		String tokenValue = "token_value";

		User entity = Mockito.spy(UserTestData.entity(true));

		Mockito.when(userService.getByPhoneNumber(phoneNumber)).thenReturn(Optional.of(entity));
		Mockito.when(passwordEncoder.matches(password, entity.getPassword())).thenReturn(true);
		Mockito.when(jwtUtils.generateAccessToken(entity)).thenReturn(tokenValue);

		String token = authService.login(basicAuth);

		assertThat(token).isEqualTo(tokenValue);
		Mockito.verify(userService, Mockito.times(1)).getByPhoneNumber(phoneNumber);
		Mockito.verify(jwtUtils, Mockito.times(1)).generateAccessToken(entity);
	}

}