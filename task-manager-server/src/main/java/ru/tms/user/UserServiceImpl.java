package ru.tms.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tms.exception.NotFoundException;
import ru.tms.exception.ParameterConflictException;
import ru.tms.user.dto.UserCreateDto;
import ru.tms.user.dto.UserResponseDto;
import ru.tms.user.dto.UserUpdateDto;
import ru.tms.user.mapper.UserMapper;
import ru.tms.user.model.User;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserResponseDto create(UserCreateDto userRequest) {

        User user = userMapper.mapToUser(userRequest);

        checkEmailConflict(user.getEmail());

        User userCreated = userRepository.save(user);

        return userMapper.mapToUserDto(userCreated);
    }

    @Override
    public UserResponseDto update(UserUpdateDto userRequest) {

        User userUpdated = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (!(userUpdated.getEmail().equals(userRequest.getEmail()))) {
            checkEmailConflict(userMapper.mapToUser(userRequest).getEmail());
        }

        userMapper.updateUserFields(userUpdated, userRequest);

        User user = userRepository.save(userUpdated);

        return userMapper.mapToUserDto(user);
    }

    @Override
    public void remove(Long id) {
        userRepository.deleteById(id);
    }

    private void checkEmailConflict(String email) {
        if (userRepository.existsByEmailContainingIgnoreCase(email)) {
            throw new ParameterConflictException("email", "Тайкой email уже занят");
        }
    }
}