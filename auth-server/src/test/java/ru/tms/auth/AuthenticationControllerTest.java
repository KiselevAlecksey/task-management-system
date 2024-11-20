package ru.tms.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.tms.dto.AuthenticationRequest;
import ru.tms.dto.AuthenticationResponse;
import ru.tms.dto.RegisterRequest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.tms.utils.TestData.createRegisterRequest;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthenticationController")
class AuthenticationControllerTest {

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Должен зарегистрировать нового пользователя")
    void should_register_new_user() throws Exception {
        AuthenticationResponse mockResponse = new AuthenticationResponse("mockAccessToken", "mockRefreshToken");

        RegisterRequest request = createRegisterRequest();

        when(authenticationService.register(request)).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.access_token").value("mockAccessToken"))
                .andExpect(jsonPath("$.refresh_token").value("mockRefreshToken"))
                .andDo(print());

        verify(authenticationService, times(1)).register(request);
    }

    @Test
    @DisplayName("Должен аутентифицировать существующего пользователя")
    void should_authenticate_existing_user() throws Exception {
        AuthenticationResponse mockResponse = new AuthenticationResponse("mockAccessToken", "mockRefreshToken");

        AuthenticationRequest request = new AuthenticationRequest();
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
    @WithMockUser(roles = "USER")
    void should_refresh_access_token() throws Exception {
        Mockito.doNothing().when(authenticationService).refreshToken(any(), any());

        mockMvc.perform(post("/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        Mockito.verify(authenticationService, Mockito.times(1)).refreshToken(any(), any());
    }
}