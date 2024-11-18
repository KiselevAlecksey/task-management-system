package ru.tms.user;


import ru.tms.user.dto.UserCreateDto;
import ru.tms.user.dto.UserResponseDto;
import ru.tms.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> findAll();

    UserResponseDto getById(long id);

    UserResponseDto create(UserCreateDto userRequest);

    UserResponseDto update(UserUpdateDto userRequest);

    void remove(long id);
}
