package com.github.ricaps.tennis_club.rest;

import com.github.ricaps.tennis_club.api.user.UserRegisterDto;
import com.github.ricaps.tennis_club.business.service.definition.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.github.ricaps.tennis_club.business.service.AuthServiceImpl.BASIC_PREFIX;
import static com.github.ricaps.tennis_club.security.filter.JwtFilter.BEARER_PREFIX;

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Authentication API",
		description = "Provides ability to register, login (get access token) and get refresh token")
@Validated
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@Operation(description = "Registers a user")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "User registered successfully.",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
					headers = @Header(name = "Authorization", description = "JWT token")),
			@ApiResponse(responseCode = "400", description = "Validation of input request failed"),
			@ApiResponse(responseCode = "404", description = "Court not found") })
	@PostMapping("/register")
	public ResponseEntity<Void> register(@Valid @RequestBody UserRegisterDto userRegisterDto) {
		String jwtToken = authService.register(userRegisterDto);

		HttpHeaders httpHeaders = new HttpHeaders();
		addAuthorizationHeader(httpHeaders, jwtToken);

		return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatus.CREATED.value())).headers(httpHeaders).build();
	}

	@Operation(description = "Log-in a user using Basic Authentication")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User logged-in successfully.",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
					headers = @Header(name = "Authorization", description = "JWT token")),
			@ApiResponse(responseCode = "400", description = "Validation of input request failed"),
			@ApiResponse(responseCode = "404", description = "Court not found") })
	@PostMapping("/login")
	public ResponseEntity<Void> login(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
		if (authHeader == null || !authHeader.startsWith(BASIC_PREFIX)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		String jwtToken = authService.login(authHeader);
		HttpHeaders httpHeaders = new HttpHeaders();
		addAuthorizationHeader(httpHeaders, jwtToken);

		return ResponseEntity.ok().headers(httpHeaders).build();
	}

	private void addAuthorizationHeader(HttpHeaders httpHeaders, String token) {
		httpHeaders.add(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);
	}

}
