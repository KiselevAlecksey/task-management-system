package ru.tms.userduplicate;


import ru.tms.userduplicate.dto.UserCreateDto;
import ru.tms.userduplicate.dto.UserResponseDto;
import ru.tms.userduplicate.dto.UserUpdateDto;

public interface UserService {
    UserResponseDto create(UserCreateDto userRequest);

    UserResponseDto update(UserUpdateDto userRequest);

    void remove(long id);
}
