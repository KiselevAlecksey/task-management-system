package ru.tms.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.Rollback;
import ru.tms.dto.AuthenticationResponse;
import ru.tms.token.Token;
import ru.tms.token.TokenRepository;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
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