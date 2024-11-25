package ru.tms.userduplicate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.tms.config.multitenancy.TenantContext;
import ru.tms.exception.NotFoundException;
import ru.tms.exception.ParameterConflictException;
import ru.tms.userduplicate.dto.UserCreateDto;
import ru.tms.userduplicate.dto.UserResponseDto;
import ru.tms.userduplicate.dto.UserUpdateDto;
import ru.tms.userduplicate.mapper.UserMapper;
import ru.tms.userduplicate.model.UserDuplicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserResponseDto create(UserCreateDto userRequest) {

        UserDuplicate user = userMapper.mapToUser(userRequest);
        TenantContext.setCurrentTenant("tms");
        log.info(TenantContext.getCurrentTenant());
        checkEmailConflict(user.getEmail());

        UserDuplicate userCreated = userRepository.save(user);

        return userMapper.mapToUserDto(userCreated);
    }

    @Override
    @Transactional
    public UserResponseDto update(UserUpdateDto userRequest) {

        UserDuplicate userUpdated = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (!(userUpdated.getEmail().equals(userRequest.getEmail()))) {
            checkEmailConflict(userMapper.mapToUser(userRequest).getEmail());
        }

        userMapper.updateUserFields(userUpdated, userRequest);

        UserDuplicate user = userRepository.save(userUpdated);

        return userMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public void remove(long id) {
        userRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void checkEmailConflict(String email) {
        if (userRepository.existsByEmailContainingIgnoreCase(email)) {
            throw new ParameterConflictException("email", "Тайкой email уже занят");
        }
    }
}