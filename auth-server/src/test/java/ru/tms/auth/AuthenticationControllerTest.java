package ru.tms.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.tms.config.JwtAuthenticationFilter;
import ru.tms.config.JwtService;
import ru.tms.auth.dto.AuthenticationRequest;
import ru.tms.auth.dto.AuthenticationResponse;
import ru.tms.auth.dto.RegisterRequest;
import ru.tms.config.LogoutService;
import ru.tms.token.TokenRepository;
import ru.tms.auth.dto.ChangePasswordRequest;
import ru.tms.user.UserRestClient;
import ru.tms.user.model.User;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.tms.utils.TestData.*;

@WebMvcTest({AuthenticationController.class, LogoutService.class})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthenticationController")
@ContextConfiguration
class AuthenticationControllerTest {

    @MockBean
    private TokenRepository tokenRepository;

    @MockBean
    private UserRestClient restClient;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Должен вернуть токены нового пользователя при регистрации")
    void should_return_authentication_response_on_successful_registration() {
        RegisterRequest request = createRegisterRequest();
        AuthenticationResponse mockResponse = new AuthenticationResponse(
                request.getId(), "mockAccessToken", "mockRefreshToken");

        when(authenticationService.register(request)).thenReturn(mockResponse);

        AuthenticationResponse response = authenticationService.register(request);

        assertNotNull(response);
        assertEquals("mockAccessToken", response.getAccessToken());
        assertEquals("mockRefreshToken", response.getRefreshToken());
    }

    @Test
    @DisplayName("Должен зарегистрировать нового пользователя")
    void should_register_new_user() throws Exception {
        RegisterRequest request = createRegisterRequest();
        AuthenticationResponse mockResponse = new AuthenticationResponse(
                request.getId(), "mockAccessToken", "mockRefreshToken");

        when(authenticationService.register(request)).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(mockResponse)))
                .andExpect(jsonPath("$.access_token").value("mockAccessToken"))
                .andExpect(jsonPath("$.refresh_token").value("mockRefreshToken"))
                .andDo(print());

        verify(authenticationService, times(1)).register(createRegisterRequest());
    }

    @Test
    @DisplayName("Должен аутентифицировать существующего пользователя")
    void should_authenticate_existing_user() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest();
        AuthenticationResponse mockResponse = new AuthenticationResponse("mockAccessToken", "mockRefreshToken");


        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(authenticationService.authenticate(request)).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("mockAccessToken"))
                .andExpect(jsonPath("$.refresh_token").value("mockRefreshToken"))
                .andDo(print());

        verify(authenticationService, times(1)).authenticate(request);
    }

    @Test
    @DisplayName("Должен закончить сессию аутентифицированного пользователя")
    void should_close_authentication_session_user() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = createUser();

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        request.setAttribute("Authorization", "Bearer mockAccessToken");
        when(request.getHeader("Authorization")).thenReturn("Bearer mockAccessToken");
        when(authentication.getName()).thenReturn("ivan@example.com");

        doReturn(authorities).when(authentication).getAuthorities();

        when(tokenRepository.findByToken("mockAccessToken"))
                .thenReturn(Optional.of(createToken("mockAccessToken", "mockRefreshToken")));

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + "mockAccessToken"))
                .andExpect(status().isOk())
                .andExpect(content().string("Конец сессии"))
                .andDo(print());
    }

    @Test
    @DisplayName("не должен закончить сессию аутентифицированного пользователя с неверным токеном")
    void should_not_close_authentication_session_with_wrong_token_user() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(content().string("Доступ запрещен"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("Должен выбросить AccessDeniedException при отсутствии токена")
    void should_throw_access_denied_exception_when_no_token() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Должен выбросить AccessDeniedException при неверном токене")
    void should_throw_access_denied_exception_when_invalid_token() throws Exception {
        String token = "invalidToken";

        when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Должен обновить токен доступа")
    @WithMockUser(authorities = {"USER"})
    void should_refresh_access_token() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        doNothing().when(authenticationService).refreshToken(request, response);

        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .header("Authorization", "Bearer " + "mockAccessToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(authenticationService, times(1)).refreshToken(any(), any());
    }

    @Test
    @DisplayName("Не должен обновить токен доступа")
    @WithMockUser(authorities = {"USER"})
    void should_not_refresh_access_token() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        doNothing().when(authenticationService).refreshToken(request, response);

        mockMvc.perform(post("/api/v1/auth/refresh-token"))
                .andExpect(status().isForbidden())
                .andDo(print());

        verify(authenticationService, times(0)).refreshToken(any(), any());
    }

    @Test
    @DisplayName("Должен обновить пароль пользователя")
    void should_change_user_password() throws Exception {
        SecurityContext securityContext = mock(SecurityContext.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        Authentication authentication = mock(Authentication.class);
        Principal principal = mock(Principal.class);
        User userDetails = createUser();

        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .currentPassword(userDetails.getPassword())
                .newPassword("newpass")
                .confirmationPassword("newpass")
                .build();

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        request.setAttribute("Authorization", "Bearer mockAccessToken");
        when(request.getHeader("Authorization")).thenReturn("Bearer mockAccessToken");
        when(authentication.getName()).thenReturn("ivan@example.com");
        when(principal.getName()).thenReturn("ivan@example.com");
        when(authentication.getPrincipal()).thenReturn(principal);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        doReturn(authorities).when(authentication).getAuthorities();
        doNothing().when(authenticationService).changePassword(changePasswordRequest, principal);

        mockMvc.perform(post("/api/v1/auth/change-password")
                        .header("Authorization", "Bearer mockAccessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk())
                .andDo(print());

        verify(authenticationService, times(1)).changePassword(any(), any());
    }
}