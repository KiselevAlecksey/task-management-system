package ru.tms.userduplicate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tms.userduplicate.dto.UserCreateDto;
import ru.tms.userduplicate.dto.UserResponseDto;
import ru.tms.userduplicate.dto.UserUpdateDto;
import ru.tms.userduplicate.model.UserDuplicate;

@Component
@RequiredArgsConstructor
public final class UserMapper {

    public UserDuplicate mapToUser(UserCreateDto request) {
        return UserDuplicate.builder()
                .id(request.getId())
                .email(request.getEmail())
                .name(request.getName())
                .role(request.getERole())
                .build();
    }

    public UserDuplicate mapToUser(UserUpdateDto request) {
        return UserDuplicate.builder()
                .email(request.getEmail())
                .name(request.getName())
                .role(request.getERole())
                .build();
    }

    public UserResponseDto mapToUserDto(UserDuplicate user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public UserDuplicate updateUserFields(UserDuplicate user, UserUpdateDto request) {

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
