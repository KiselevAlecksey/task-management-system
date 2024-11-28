package ru.tms.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.tms.exception.NotFoundException;
import ru.tms.exception.ParameterConflictException;
import ru.tms.user.dto.UserCreateDto;
import ru.tms.user.dto.UserResponseDto;
import ru.tms.user.dto.UserUpdateDto;
import ru.tms.user.mapper.UserMapper;
import ru.tms.user.model.User;

import java.security.Principal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;


    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }

        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

    @Override
    public List<UserResponseDto> findAll() {

        List<User> users = userRepository.findAll();

        return userRepository.findAll().stream().map(userMapper::mapToUserDto).toList();
    }

    @Override
    public UserResponseDto getById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return userMapper.mapToUserDto(user);
    }

    @Override
    public UserResponseDto create(UserCreateDto userRequest) {

        User user = userMapper.mapToUser(userRequest);

        checkEmailConflict(user.getEmail());

        User userCreated = userRepository.save(user);

        return userMapper.mapToUserDto(userCreated);
    }

    @Override
    @Transactional
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
    @Transactional
    public void remove(long id) {
        userRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void checkEmailConflict(String email) {
        UserEmail userEmails = userRepository.findByEmailContainingIgnoreCase(email);

        if (userEmails != null && userEmails.getEmail().equals(email)) {
            throw new ParameterConflictException("email", "Тайкой email уже занят");
        }
    }
}