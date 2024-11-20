package ru.tms.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.tms.TaskManagerServer;
import ru.tms.auth.AuthenticationService;
import ru.tms.config.JwtAuthenticationFilter;
import ru.tms.config.JwtService;
import ru.tms.config.SecurityConfig;
import ru.tms.dto.AuthenticationResponse;
import ru.tms.dto.RegisterRequest;
import ru.tms.task.TaskService;
import ru.tms.task.dto.param.AdminStatusAndPriorityParam;
import ru.tms.task.dto.param.UserStatusParam;
import ru.tms.token.TokenRepository;
import ru.tms.user.UserService;
import ru.tms.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.tms.utils.TestData.*;

@WebMvcTest(AdminTaskController.class)
@AutoConfigureMockMvc
@DisplayName("AdminTaskController")
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@ContextConfiguration(classes = {TaskManagerServer.class})
class UserTaskControllerTest {

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private TokenRepository tokenRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private LogoutHandler logoutHandler;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserService userService;

    @MockBean
    private TaskService taskService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    String jwtToken;

    @BeforeEach
    void init() {
        jwtToken = getJwtToken();
    }

    @Test
    @DisplayName("Изменение статуса должно завершиться успешно")
    void should_change_status() throws Exception {
        UserStatusParam param = getUserParam();

        when(taskService.changeStatus(param)).thenReturn(responseTaskDto());

        mockMvc.perform(patch("/users/tasks/{taskId}/status", TEST_ID_ONE)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("status", "WAITING")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(taskService, times(1)).changeStatus(param);
    }

    private String getJwtToken() {
        RegisterRequest request = createRegisterRequest();
        AuthenticationResponse mockResponse = new AuthenticationResponse(TOKEN_REAL, "mockRefreshToken");
        String jwtToken = mockResponse.getAccessToken();
        String refreshToken = mockResponse.getAccessToken();
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getERole())
                .build();

        when(userService.create(createUserDto())).thenReturn(createdUserDto());
        when(jwtService.generateToken(user)).thenReturn(jwtToken);
        when(jwtService.extractUsername(jwtToken)).thenReturn(request.getEmail());
        when(jwtService.isTokenValid(jwtToken, user)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(jwtToken)).thenReturn(user);
        when(tokenRepository.findByToken(jwtToken)).thenReturn(Optional.of(createToken(jwtToken, refreshToken)));
        return jwtToken;
    }
}