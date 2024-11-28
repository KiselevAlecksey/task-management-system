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
import ru.tms.config.JwtAuthenticationFilter;
import ru.tms.config.JwtService;
import ru.tms.config.SecurityConfig;
import ru.tms.task.TaskService;
import ru.tms.task.dto.param.UserStatusParam;
import ru.tms.token.TokenRepository;
import ru.tms.user.UserService;
import ru.tms.user.dto.UserCreateDto;
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

        when(taskService.changeStatus(param)).thenReturn(responseTaskDtoStatusWaiting());

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
        UserCreateDto request = createUserCreateDto();
        String jwtToken = TOKEN_USER;
        User user = User.builder()
                .id(request.getId())
                .name(request.getName())
                .email(request.getEmail())
                .role(request.getERole())
                .build();

        when(userService.create(createUserDto())).thenReturn(createdUserDto());
        when(jwtService.generateToken(user)).thenReturn(jwtToken);
        when(jwtService.extractUsername(jwtToken)).thenReturn(request.getEmail());
        when(jwtService.extractUser(jwtToken)).thenReturn(user);
        when(jwtService.isTokenValid(jwtToken, user)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(jwtToken)).thenReturn(user);
        when(tokenRepository.findByToken(jwtToken)).thenReturn(Optional.of(createToken(jwtToken, "mockRefreshToken")));
        return jwtToken;
    }
}