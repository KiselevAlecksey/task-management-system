package ru.tms.user;


import ru.tms.user.dto.UserCreateDto;
import ru.tms.user.dto.UserResponseDto;
import ru.tms.user.dto.UserUpdateDto;

import java.util.UUID;

public interface UserService {
    UserResponseDto create(UserCreateDto userRequest);

    UserResponseDto update(UserUpdateDto userRequest);

    void remove(Long id);
}
