package ru.tms.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.tms.user.dto.UserCreateDto;
import ru.tms.user.dto.UserResponseDto;
import ru.tms.user.dto.UserUpdateDto;
import ru.tms.user.model.Role;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@PreAuthorize("hasAnyRole('ADMIN', 'USER_CREATOR')")
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('admin:create', 'userCreator:createUser')")
    public UserResponseDto create(@RequestBody @Validated UserCreateDto userRequest) {
        log.info("Handling incoming API request");

        log.info("==> Create user is {} start", userRequest.getEmail());
        if (userRequest.getRole() != null) {
            Role role = Role.from(userRequest.getRole().toUpperCase())
                    .orElseThrow(() -> new IllegalArgumentException("Не поддерживаемая роль: " + userRequest.getRole()));
            userRequest.setERole(role);
        }
        UserResponseDto created = userService.create(userRequest);
        log.info("<== Created user is {} complete", userRequest.getEmail());
        return created;
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:update')")
    public UserResponseDto update(@RequestBody @Validated UserUpdateDto userRequest, @PathVariable Long id) {
        log.info("==> Update user is id {} start", id);
        if (userRequest.getRole() != null) {
            Role role = Role.from(userRequest.getRole().toUpperCase())
                    .orElseThrow(() -> new IllegalArgumentException("Не поддерживаемая роль: " + userRequest.getRole()));
            userRequest.setERole(role);
        }
        userRequest.setId(id);
        UserResponseDto updated = userService.update(userRequest);
        log.info("<== Updated user is id {} complete", id);
        return updated;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:delete')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void userRemove(@PathVariable Long id) {
        log.info("==> Users remove user id {} start", id);
        userService.remove(id);
        log.info("<== Users remove user id {} complete", id);
    }

}
