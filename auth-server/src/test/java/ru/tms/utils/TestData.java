package ru.tms.utils;

import lombok.NoArgsConstructor;
import ru.tms.auth.dto.AuthenticationRequest;
import ru.tms.auth.dto.RegisterRequest;
import ru.tms.token.Token;
import ru.tms.token.TokenType;
import ru.tms.user.dto.UserCreateDto;
import ru.tms.user.dto.UserResponseDto;
import ru.tms.user.dto.UserUpdateDto;
import ru.tms.user.model.Role;
import ru.tms.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class TestData {

    public static final long TEST_USER_ID = 1L;
    public static final long TEST_USER2_ID = 2L;
    public static final long TEST_USER3_ID = 3L;

    public static final long TEST_ID_ONE = 1L;
    public static final long TEST_ID_TWO = 2L;
    public static final long TEST_ID_THREE = 3L;
    public static final long TEST_ID_FOUR = 4L;

    public static final LocalDateTime START_BOOKING = LocalDateTime.of(2024, 10, 28, 12, 0);
    public static final LocalDateTime END_BOOKING = LocalDateTime.of(2024, 10, 28, 13, 0);
    public static final String START = "2024-10-28T12:00:00";
    public static final String END = "2024-10-28T13:00:00";
    public static final String CONFIRM_TIME = "2024-10-29T12:00:00";
    public static final String CONFIRM_UPDATED_TIME = "2023-10-02T12:00:00";
    public static final LocalDateTime START_BOOK = LocalDateTime.of(2023, 10, 1, 12, 0);
    public static final LocalDateTime END_BOOK = LocalDateTime.of(2023, 10, 15, 12, 0);

    public static final long ONE_DAY_IN_MILLIS = 86_400_000L;
    public static final long ONE_DAY_IN_MINUTES = 1440L;
    public static final LocalDateTime NOW_DATE_TIME = LocalDateTime.now();
    public static final List<Token> TOKENS = Collections.emptyList();
    public static final String PASS = "password";
    public static final String ROLE_GUEST = "GUEST";
    public static final String ROLE_USER = "USER";
    public static final Role EROLE_USER = Role.USER;
    public static final String TOKEN = "mockAccessToken";
    public static final String TOKEN_REAL = "eyJhbGciOiJIUzI1NiJ9" +
            ".eyJyb2xlIjoiUk9MRV9VU0VSIiwic3ViIjoiaXZhbjFAZXhhbX" +
            "BsZS5jb20iLCJpYXQiOjE3MzIwNTc1MjEsImV4cCI6MTczMjE0MzkyMX0" +
            ".OzM7xnNCWN1ZoL7MWhKF5cP09EImjeaCiAIEl8bCOOc";

    public static User createUser() {
        return new User(TEST_USER_ID, "Иван Иванов", "ivan@example.com", PASS, EROLE_USER, TOKENS);
    }

    public static UserCreateDto createUserDto() {
        return new UserCreateDto(TEST_USER_ID, "Иван Иванов2", "ivan2@example.com", PASS, null);
    }

    public static UserCreateDto createUserDto(String name, String description) {
        return new UserCreateDto(TEST_USER_ID, name, description, PASS, ROLE_USER);
    }

    public static UserCreateDto createUserDto(String name, String email, String pass, String role) {
        return new UserCreateDto(TEST_USER_ID, name, email, pass, role);
    }

    public static UserResponseDto createdUserDto(Long userId, String name, String description) {
        return new UserResponseDto(userId, name, description);
    }

    public static UserResponseDto createdUserDto(String name, String description) {
        return new UserResponseDto(TEST_USER_ID, name, description);
    }

    public static UserResponseDto createdUserDto() {
        return new UserResponseDto(TEST_USER_ID, "Иван Иванов", "ivan@example.com");
    }

    public static UserResponseDto createdUser2Dto() {
        return new UserResponseDto(TEST_USER2_ID, "Петр Петров", "petr@example.com");
    }

    public static UserUpdateDto updateUserDto() {
        return new UserUpdateDto(TEST_USER_ID, "Иван Петров", "petrov@example.ru", PASS, ROLE_GUEST, Role.GUEST);
    }

    public static UserUpdateDto updateUserDto(Long userId, String name, String email) {
        return new UserUpdateDto(userId, name, email, PASS, ROLE_GUEST);
    }

    public static UserUpdateDto updateUserDto(String name, String email, String pass, String role) {
        return new UserUpdateDto(TEST_USER_ID, name, email, pass, role);
    }

    public static UserResponseDto updatedUserDto(Long userId, String name, String description) {
        return new UserResponseDto(userId, name, description);
    }

    public static UserResponseDto updatedUserDto() {
        return new UserResponseDto(TEST_USER_ID, "Иван Иванов", "ivan@example.ru");
    }

    public static List<UserResponseDto> createUserDtoList() {
        List<UserResponseDto> list = new ArrayList<>(3);
        list.add(new UserResponseDto(TEST_USER_ID, "Иван Иванов", "ivan@example.com"));
        list.add(new UserResponseDto(TEST_USER2_ID, "Петр Петров", "petr@example.com"));
        list.add(new UserResponseDto(TEST_USER3_ID, "Анна Сидорова", "anna@example.com"));

        return list;
    }

    // authentication

    public static RegisterRequest createRegisterRequest() {
        return new RegisterRequest(TEST_USER_ID, "Иван Иванов1", "ivan1@example.com", PASS, ROLE_USER, Role.USER);
    }

    public static RegisterRequest createAdminRegisterRequest() {
        return new RegisterRequest(TEST_USER_ID, "Иван Иванов", "ivan@example.com", PASS, "ADMIN", Role.ADMIN);
    }

    public static Token createToken(String token, String refreshToken) {
        return new Token(TEST_ID_ONE, token, refreshToken, TokenType.BEARER, false, false, createUser());
    }

    public static AuthenticationRequest createAuthenticationRequest() {
        return new AuthenticationRequest("ivan1@example.com", PASS);
    }
}