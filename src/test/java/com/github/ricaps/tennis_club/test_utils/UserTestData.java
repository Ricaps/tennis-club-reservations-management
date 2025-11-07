package com.github.ricaps.tennis_club.test_utils;

import com.github.ricaps.tennis_club.api.user.RoleDto;
import com.github.ricaps.tennis_club.api.user.UserBasicView;
import com.github.ricaps.tennis_club.api.user.UserCreateDto;
import com.github.ricaps.tennis_club.api.user.UserDetailedView;
import com.github.ricaps.tennis_club.api.user.UserRegisterDto;
import com.github.ricaps.tennis_club.peristence.entity.Role;
import com.github.ricaps.tennis_club.peristence.entity.User;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTestData {

	private UserTestData() {
		super();
	}

	public static User entity() {
		return entity(true);
	}

	public static User entity(boolean randomPhone) {
		return User.builder()
			.firstName("John")
			.familyName("Doe")
			.password("12345")
			.phoneNumber(randomPhone ? getRandomPhoneNumber() : "777777777")
			.uid(UUID.randomUUID())
			.roles(new HashSet<>(List.of(Role.USER, Role.ADMIN)))
			.build();
	}

	public static UserCreateDto createUser(boolean randomPhone) {
		return new UserCreateDto("John", "Doe", randomPhone ? getRandomPhoneNumber() : "777777777", "123456",
				Set.of(RoleDto.USER));
	}

	public static UserCreateDto createInvalid(boolean randomPhone) {
		return new UserCreateDto("John", "", randomPhone ? getRandomPhoneNumber() : "777777777", "123456",
				Set.of(RoleDto.USER));
	}

	public static UserRegisterDto createRegister(boolean randomPhone) {
		return new UserRegisterDto("John", "Doe", randomPhone ? getRandomPhoneNumber() : "777777777", "123456");
	}

	public static UserDetailedView viewUser(UUID uuid) {
		return new UserDetailedView(uuid, "John", "Doe", "777777777", Set.of(RoleDto.USER));
	}

	public static UserBasicView viewBasicUser(UUID uuid) {
		return new UserBasicView(uuid, "John", "Doe", "777777777");
	}

	public static void compareViewAndCreate(UserDetailedView userViewDto, UserCreateDto createDto) {
		assertThat(userViewDto.uid()).isNotNull();
		assertThat(userViewDto.firstName()).isEqualTo(createDto.firstName());
		assertThat(userViewDto.familyName()).isEqualTo(createDto.familyName());
		assertThat(userViewDto.phoneNumber()).isEqualTo(createDto.phoneNumber());
		assertThat(userViewDto.roles()).isEqualTo(createDto.roles());
	}

	public static void compareViewAndEntity(UserDetailedView viewDto, User entity) {
		assertThat(viewDto.uid()).isEqualTo(entity.getUid());
		assertThat(viewDto.firstName()).isEqualTo(entity.getFirstName());
		assertThat(viewDto.familyName()).isEqualTo(entity.getFamilyName());
		assertThat(viewDto.phoneNumber()).isEqualTo(entity.getPhoneNumber());
		assertThat(dtoRolesToEntity(viewDto.roles())).isEqualTo(entity.getRoles());
	}

	public static void compareViewAndEntity(UserBasicView viewDto, User entity) {
		assertThat(viewDto.uid()).isEqualTo(entity.getUid());
		assertThat(viewDto.firstName()).isEqualTo(entity.getFirstName());
		assertThat(viewDto.familyName()).isEqualTo(entity.getFamilyName());
		assertThat(viewDto.phoneNumber()).isEqualTo(entity.getPhoneNumber());
	}

	private static Set<Role> dtoRolesToEntity(Set<RoleDto> roleDtos) {
		return roleDtos.stream().map(dto -> Role.valueOf(dto.name())).collect(Collectors.toSet());
	}

	private static String getRandomPhoneNumber() {
		StringBuilder stringBuilder = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 9; i++) {
			stringBuilder.append(random.nextInt(10));
		}

		return stringBuilder.toString();
	}

}
