package ru.tms.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.tms.auth.dto.AuthenticationResponse;
import ru.tms.auth.dto.ChangePasswordRequest;
import ru.tms.exception.ParameterConflictException;
import ru.tms.token.Token;
import ru.tms.token.TokenRepository;
import ru.tms.user.model.User;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.tms.utils.TestData.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@DisplayName("AuthenticationService")
class AuthenticationServiceImplTest {
    private final AuthenticationService authenticationService;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    private final TokenRepository tokenRepository;

    @Test
    @DisplayName("Должен регистрировать нового пользователя")
    void should_register_user() {
        AuthenticationResponse responseDto = authenticationService.register(createRegisterRequest());

        String accessToken = responseDto.getAccessToken();
        String refreshToken = responseDto.getRefreshToken();

        assertEquals(accessToken, responseDto.getAccessToken(), "Поля должны совпадать");
        assertEquals(refreshToken, responseDto.getRefreshToken(), "Поля должны совпадать");
    }

    @Test
    @DisplayName("Не должен регистрировать нового пользователя с занятой почтой")
    void should_not_register_user_with_email_conflict() {
        authenticationService.register(createRegisterRequest());

        ParameterConflictException exception = assertThrows(ParameterConflictException.class, () -> {
            authenticationService.register(createRegisterRequest());
        });

        assertEquals(exception.getReason(), "Тайкой email уже занят");
    }

    @Test
    @DisplayName("Должен аутентифицировать пользователя")
    void should_authenticate_user() {
        authenticationService.register(createRegisterRequest());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        AuthenticationResponse responseDto = authenticationService.authenticate(createAuthenticationRequest());

        String accessToken = responseDto.getAccessToken();
        String refreshToken = responseDto.getRefreshToken();

        assertEquals(accessToken, responseDto.getAccessToken(), "Поля должны совпадать");
        assertEquals(refreshToken, responseDto.getRefreshToken(), "Поля должны совпадать");
    }

    @Test
    @DisplayName("Должен обновлять токен пользователя")
    void should_refresh_token() throws IOException {
        AuthenticationResponse responseDto = authenticationService.register(createRegisterRequest());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String token = responseDto.getAccessToken();
        Token tokenCreated = tokenRepository.findByToken(token).orElseThrow();
        HttpServletRequest request = new CustomHttpServletRequestWrapper(httpServletRequest,
                "Bearer " + token);

        authenticationService.refreshToken(request, httpServletResponse);

        Token refreshToken = tokenRepository.findByRefreshToken(token).orElseThrow();

        assertNotEquals(tokenCreated.getToken(), refreshToken.getToken(), "Поля должны совпадать");
    }

    @Test
    @DisplayName("Должен обновить пароль пользователя")
    void should_change_user_password() {
        User userDetails = createUser();

        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .currentPassword("password")
                .newPassword("newpass")
                .confirmationPassword("newpass")
                .build();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, "", Collections.singletonList(new SimpleGrantedAuthority("USER")));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        authenticationService.changePassword(changePasswordRequest, authentication);
    }

    @Test
    @DisplayName("Должен обновить пароль пользователя")
    void should_change_user_wrong_password() {
        User userDetails = createUser();

        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .currentPassword("wrongpass")
                .newPassword("newpass")
                .confirmationPassword("newpass")
                .build();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, "",
                        Collections.singletonList(new SimpleGrantedAuthority("USER")));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            authenticationService.changePassword(changePasswordRequest, authentication);
        });

        assertEquals(exception.getMessage(), "Wrong password");
    }

    static class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
        private final String authHeader;

        public CustomHttpServletRequestWrapper(HttpServletRequest request, String authHeader) {
            super(request);
            this.authHeader = authHeader;
        }

        @Override
        public String getHeader(String name) {
            if (HttpHeaders.AUTHORIZATION.equals(name)) {
                return authHeader;
            }
            return super.getHeader(name);
        }
    }
}