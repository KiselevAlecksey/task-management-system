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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.tms.config.JwtAuthenticationFilter;
import ru.tms.config.JwtService;
import ru.tms.auth.dto.AuthenticationRequest;
import ru.tms.auth.dto.AuthenticationResponse;
import ru.tms.auth.dto.RegisterRequest;
import ru.tms.user.UserRestClient;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.tms.utils.TestData.createRegisterRequest;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthenticationController")
@ContextConfiguration
class AuthenticationControllerTest {

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
    @DisplayName("Должен вернуть токены нового пользователя")
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

        when(authenticationService.register(createRegisterRequest())).thenReturn(mockResponse);

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
    @DisplayName("Должен обновить токен доступа")
    @WithMockUser(authorities = { "USER" })
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
}