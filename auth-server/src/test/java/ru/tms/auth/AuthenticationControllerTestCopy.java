package ru.tms.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.tms.auth.AuthenticationController;
import ru.tms.auth.AuthenticationService;
import ru.tms.config.JwtAuthenticationFilter;
import ru.tms.config.JwtService;
import ru.tms.config.SecurityConfig;
import ru.tms.dto.AuthenticationResponse;
import ru.tms.dto.RegisterRequest;
import ru.tms.token.TokenRepository;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.tms.utils.TestData.createRegisterRequest;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {SecurityConfig.class})
@DisplayName("AuthenticationController")
class AuthenticationControllerTestCopy {

    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private TokenRepository tokenRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private LogoutHandler logoutHandler;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper mapper = new ObjectMapper();

    @Before("")
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Должен зарегистрировать нового пользователя")
    void should_register_new_user() throws Exception {
        AuthenticationResponse mockResponse = new AuthenticationResponse("mockAccessToken", "mockRefreshToken");

        RegisterRequest request = createRegisterRequest();

        when(authenticationService.register(request)).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(authenticationService, times(1)).register(createRegisterRequest());
    }
}