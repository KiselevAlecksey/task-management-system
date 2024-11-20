package ru.tms.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.tms.dto.AuthenticationResponse;
import ru.tms.dto.AuthenticationRequest;
import ru.tms.dto.RegisterRequest;
import ru.tms.user.model.Role;

import java.io.IOException;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Validated RegisterRequest request) {
        log.info("Create new user: {}", request);
        Role role = Role.from(request.getRole())
                .orElseThrow(() -> new IllegalArgumentException("Не поддерживаемая роль: " + request.getRole()));

        request.setERole(role);

        ResponseEntity<AuthenticationResponse> response = ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
        log.info("Response from service: {}", response);
        return response;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Validated AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }
}

