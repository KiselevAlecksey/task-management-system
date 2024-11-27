package ru.tms.utils;

import lombok.NoArgsConstructor;
import ru.tms.task.dto.comment.CommentCreateDto;
import ru.tms.task.dto.comment.CommentResponseDto;
import ru.tms.task.dto.param.AdminStatusAndPriorityParam;
import ru.tms.task.dto.param.UserStatusParam;
import ru.tms.task.dto.task.TaskCreateDto;
import ru.tms.task.dto.task.TaskResponseDto;
import ru.tms.task.dto.task.TaskUpdateDto;
import ru.tms.task.enums.TaskPriority;
import ru.tms.task.enums.TaskStatus;
import ru.tms.token.Token;
import ru.tms.token.TokenType;
import ru.tms.user.dto.UserCreateDto;
import ru.tms.user.dto.UserResponseDto;
import ru.tms.user.dto.UserUpdateDto;
import ru.tms.user.model.Role;
import ru.tms.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
public class TestData {

    public static final long TEST_USER_ID = 1L;
    public static final UUID TEST_USER_UUID = UUID.fromString("17adb078-4fc3-4c90-9f18-6e9cfdc1108d");
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
    public static final String TOKEN_ADMIN = "eyJhbGciOiJIUzI1NiJ9" +
            ".eyJyb2xlIjoiUk9MRV9VU0VSIiwic3ViIjoiaXZhbjFAZXhhbX" +
            "BsZS5jb20iLCJpYXQiOjE3MzIwNTc1MjEsImV4cCI6MTczMjE0MzkyMX0" +
            ".OzM7xnNCWN1ZoL7MWhKF5cP09EImjeaCiAIEl8bCOOc";
    public static final String TOKEN_USER = "eyJhbGciOiJIUzI1NiJ9" +
            ".eyJyb2xlIjoiVVNFUiIsIm5hbWUiOiJVc2VyIiwidXNlcklkIjoxLCJzd" +
            "WIiOiJ1c2VyQG1haWwuY29tIiwiaWF0IjoxNzMyMzc2MzQxLCJleHAiOjE" +
            "3MzI0NjI3NDF9.jRUDeiIqspxGZUjSFbj_n8QHgdpQg2sNNdwGxCElGJM";


    public static final String USER_ID_HEADER = "X-TaskManager-User-Id";
    public static final String TITLE = "Implement feature B";
    public static final String DESCRIPTION = "Detailed description of the feature implementation.";
    public static final String NO_STATUS = "NO_STATUS";
    public static final String WAITING = "WAITING";
    public static final String MAJOR = "MAJOR";
    public static final String BLOCKER = "BLOCKER";

    public static User createUser() {
        return new User(TEST_USER_UUID, "Иван Иванов", "ivan@example.com", EROLE_USER);
    }

    public static UserCreateDto createUserDto() {
        return new UserCreateDto(TEST_USER_UUID, "Иван Иванов2", "ivan2@example.com", PASS, null);
    }

    public static UserCreateDto createUserDto(String name, String description) {
        return new UserCreateDto(TEST_USER_UUID, name, description, ROLE_USER, EROLE_USER);
    }

    public static UserCreateDto createUserDto(String name, String email, String role, Role eRole) {
        return new UserCreateDto(TEST_USER_UUID, name, email, role, eRole);
    }

    public static UserResponseDto createdUserDto(Long userId, String name, String description) {
        return new UserResponseDto(TEST_USER_UUID, name, description);
    }

    public static UserResponseDto createdUserDto(String name, String description) {
        return new UserResponseDto(TEST_USER_UUID, name, description);
    }

    public static UserResponseDto createdUserDto() {
        return new UserResponseDto(TEST_USER_UUID, "Иван Иванов", "ivan@example.com");
    }

    public static UserResponseDto createdUser2Dto() {
        return new UserResponseDto(TEST_USER2_ID, "Петр Петров", "petr@example.com");
    }

    public static UserResponseDto createdUser3Dto() {
        return new UserResponseDto(TEST_USER3_ID, "Анна Сидорова", "anna@example.com");
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

    public static UserCreateDto createUserCreateDto() {
        return new UserCreateDto(TEST_USER_ID, "Иван Иванов2", "ivan2@example.com", PASS, Role.USER);
    }

    public static UserCreateDto createAdminUserCreateDto() {
        return new UserCreateDto(TEST_USER_ID, "Иван Иванов2", "ivan2@example.com", PASS, Role.ADMIN);
    }

    public static Token createToken(String token, String refreshToken) {
        return new Token(TEST_ID_ONE, token, refreshToken, TokenType.BEARER, false, false, createUser());
    }

    // tasks

    public static TaskCreateDto createTaskDto() {
        return new TaskCreateDto(TEST_ID_ONE, TITLE, DESCRIPTION,
                NO_STATUS, MAJOR, TEST_USER_ID, TEST_USER2_ID,
                TaskStatus.NO_STATUS, TaskPriority.MAJOR);
    }

    public static TaskUpdateDto updateTaskDto() {
        return new TaskUpdateDto(TEST_ID_ONE, TITLE, DESCRIPTION,
                WAITING, BLOCKER, TEST_USER2_ID,
                TaskStatus.WAITING, TaskPriority.BLOCKER);
    }

    public static TaskResponseDto responseTaskDto() {
        return new TaskResponseDto(TEST_ID_ONE, TITLE, DESCRIPTION, TaskStatus.NO_STATUS,
                TaskPriority.MAJOR, createdUserDto(), createdUser2Dto(), createCommentDtoList());
    }

    public static TaskResponseDto responseTaskDto(TaskStatus status, TaskPriority priority) {
        return new TaskResponseDto(TEST_ID_ONE, TITLE, DESCRIPTION, status,
                priority, createdUserDto(), createdUser2Dto(), createCommentDtoList());
    }

    public static AdminStatusAndPriorityParam getAdminParam() {
        return new AdminStatusAndPriorityParam(TaskStatus.NO_STATUS, TaskPriority.MAJOR);
    }

    public static UserStatusParam getUserParam() {
        return new UserStatusParam(TaskStatus.NO_STATUS, TEST_USER_ID, TEST_ID_ONE);
    }


    //comments

    public static List<CommentResponseDto> createCommentDtoList() {
        List<CommentResponseDto> list = new ArrayList<>(3);
        list.add(new CommentResponseDto(TEST_ID_ONE, createdUserDto(),
                "Велосипедом доволен, спасибо!", "2023-12-08 12:00:00"));
        list.add(new CommentResponseDto(TEST_ID_TWO, createdUser2Dto(),
                "Отличная книга!", "2023-10-16 10:00:00"));
        list.add(new CommentResponseDto(TEST_ID_THREE, createdUser3Dto(),
                "Очень удобный ноутбук.", "2023-11-21 11:00:00"));

        return list;
    }

    public static CommentCreateDto createCommentDto() {
        return new CommentCreateDto("Велосипедом доволен, спасибо!", TEST_USER_ID, TEST_ID_ONE);
    }

    public static CommentResponseDto createdCommentDto() {
        return new CommentResponseDto(
                TEST_ID_ONE, createdUserDto(), "Велосипедом доволен, спасибо!",
                convertDatePattern(NOW_DATE_TIME));
    }

    public static CommentResponseDto createdCommentDto(Long commentId) {
        return new CommentResponseDto(
                commentId, createdUserDto(), "Велосипедом доволен, спасибо!",
                convertDatePattern(LocalDateTime.now()));
    }

    public static CommentResponseDto createdBookCommentDto(Long commentId) {
        return new CommentResponseDto(
                commentId, createdUserDto(), "Отличная книга!",
                convertDatePattern(LocalDateTime.of(2023, 10, 16, 7, 0)));
    }

    public static CommentResponseDto createdBookCommentDto() {
        return new CommentResponseDto(
                TEST_ID_ONE, createdUserDto(), "Отличная книга!",
                convertDatePattern(LocalDateTime.of(2023, 10, 16, 7, 0)));
    }

    public static List<CommentResponseDto> createCommentBookDtoList() {
        List<CommentResponseDto> list = new ArrayList<>(3);

        list.add(new CommentResponseDto(
                TEST_ID_ONE, createdUserDto(), "Отличная книга!",
                convertDatePattern(LocalDateTime.of(2023, 10, 16, 10, 0))));

        return list;
    }

    public static String convertDatePattern(LocalDateTime localDateTime) {
        String dateTimeformatted = null;

        if (localDateTime != null) {
            dateTimeformatted = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                    .withZone(ZoneOffset.UTC)
                    .format(localDateTime);
        }
        return dateTimeformatted;
    }
}