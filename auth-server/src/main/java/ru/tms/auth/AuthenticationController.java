package ru.tms.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.tms.auth.dto.AuthenticationResponse;
import ru.tms.auth.dto.AuthenticationRequest;
import ru.tms.auth.dto.RegisterRequest;
import ru.tms.config.LogoutService;
import ru.tms.auth.dto.ChangePasswordRequest;
import ru.tms.user.model.Role;

import java.io.IOException;
import java.security.Principal;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {
    private final AuthenticationService authService;
    private final LogoutService logoutService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Validated RegisterRequest request) {
        log.info("==> Create new user: {} start", request.getEmail());
        Role role = Role.from(request.getRole())
                .orElseThrow(() -> new IllegalArgumentException("Не поддерживаемая роль: " + request.getRole()));
        request.setERole(role);

        AuthenticationResponse response = authService.register(request);
        log.info("<== Response from service: {} complete", response.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Validated AuthenticationRequest request) {
        log.info("==> Authentication user: {} start", request.getEmail());
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("==> Refresh token user: start");
        if (request.getHeader("Authorization") == null
                || !request.getHeader("Authorization").startsWith("Bearer ")) {
            throw new AccessDeniedException("Access denied");
        }
        authService.refreshToken(request, response);
        log.info("<== Refresh token user: end");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("==> Logout token user: start");
        if (request.getHeader("Authorization") == null
                || !request.getHeader("Authorization").startsWith("Bearer ")) {
            throw new AccessDeniedException("Access denied");
        }
        logoutService.logout(request, response, authentication);
        log.info("<== Logout token user: end");
        return ResponseEntity.ok().body("Конец сессии");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody @Validated ChangePasswordRequest request,
            @AuthenticationPrincipal Principal principal
            ) {

        log.info("==> Change password user: {} start", principal.getName());
        authService.changePassword(request, principal);
        log.info("<== Change password user: {} complete", principal.getName());
        return ResponseEntity.ok().body("Пароль успешно изменён");
    }
}

