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
import ru.tms.token.TokenRepository;
import ru.tms.user.UserService;

@WebMvcTest(AdminTaskController.class)
@AutoConfigureMockMvc
@DisplayName("AdminTaskController")
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@ContextConfiguration(classes = {TaskManagerServer.class})
class TaskControllerTest {
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
    void getPage() {
    }

    @Test
    void createComment() {
    }

    @Test
    void getById() {
    }

    private String getJwtToken() {
        return jwtToken;
    }
}