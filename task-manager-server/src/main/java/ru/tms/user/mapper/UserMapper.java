package ru.tms.user.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tms.user.dto.UserCreateDto;
import ru.tms.user.dto.UserResponseDto;
import ru.tms.user.dto.UserUpdateDto;
import ru.tms.user.model.User;

@Component
@RequiredArgsConstructor
public final class UserMapper {

    public User mapToUser(UserCreateDto request) {
        return User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .role(request.getERole())
                .build();
    }

    public User mapToUser(UserUpdateDto request) {
        return User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .role(request.getERole())
                .build();
    }

    public UserResponseDto mapToUserDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public User updateUserFields(User user, UserUpdateDto request) {

        if (request.hasEmail()) {
            user.setEmail(request.getEmail());
        }
        if (request.hasName()) {
            user.setName(request.getName());
        }
        if (request.hasRole()) {
            user.setRole(request.getERole());
        }
        return user;
    }
}
