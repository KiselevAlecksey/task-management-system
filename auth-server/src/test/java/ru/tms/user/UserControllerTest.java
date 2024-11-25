package ru.tms.user;

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
import ru.tms.SecurityApplication;
import ru.tms.auth.AuthenticationService;
import ru.tms.config.JwtAuthenticationFilter;
import ru.tms.config.JwtService;
import ru.tms.config.SecurityConfig;
import ru.tms.auth.dto.AuthenticationResponse;
import ru.tms.auth.dto.RegisterRequest;
import ru.tms.token.TokenRepository;
import ru.tms.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.tms.utils.TestData.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {SecurityApplication.class})
@DisplayName("UserController")
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class UserControllerTest {

    @MockBean
    private UserRestClient restClient;

    @MockBean
    private UserRepository userRepository;

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

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    String jwtToken;

    @BeforeEach
    void init() {
        jwtToken = getJwtToken();
    }

    @Test
    @DisplayName("Должен получить всех пользователей")
    void should_find_all_users() throws Exception {

        when(userService.findAll()).thenReturn(createUserDtoList());

        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService, times(1)).findAll();
    }

    @Test
    @DisplayName("Должен получить пользователя по id")
    void should_get_by_id_user() throws Exception {

        when(userService.getById(TEST_USER_ID)).thenReturn(createdUserDto());

        mockMvc.perform(get("/users/{userId}", TEST_USER_ID)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService, times(1)).getById(TEST_USER_ID);
    }

    @Test
    @DisplayName("Должен создать пользователя")
    void should_create_user() throws Exception {

        when(userService.create(createUserDto())).thenReturn(createdUserDto());

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(mapper.writeValueAsString(createUserDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        verify(userService, times(1)).create(createUserDto());
    }

    @Test
    @DisplayName("Должен обновить пользователя")
    void should_update_user() throws Exception {

        when(userService.update(updateUserDto())).thenReturn(updatedUserDto());

        mockMvc.perform(patch("/users/{userId}", TEST_USER_ID)
                        .header("Authorization", "Bearer " + jwtToken)
                .content(mapper.writeValueAsString(updateUserDto()))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService, times(1)).update(updateUserDto());
    }

    @Test
    @DisplayName("Должен удалить пользователя")
    void should_remove_user() throws Exception {
        doNothing().when(userService).remove(eq(TEST_USER_ID));

        mockMvc.perform(delete("/users/{userId}", TEST_USER_ID)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(userService, times(1)).remove(TEST_USER_ID);
    }

    private String getJwtToken() {
        RegisterRequest request = createAdminRegisterRequest();
        AuthenticationResponse mockResponse = new AuthenticationResponse(TOKEN_REAL, "mockRefreshToken");
        String jwtToken = mockResponse.getAccessToken();
        String refreshToken = mockResponse.getRefreshToken();
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
        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(user);
        when(tokenRepository.findByToken(jwtToken)).thenReturn(Optional.of(createToken(jwtToken, refreshToken)));
        return jwtToken;
    }
}