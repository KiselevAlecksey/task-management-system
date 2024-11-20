package ru.tms.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.tms.auth.AuthenticationService;
import ru.tms.dto.AuthenticationResponse;
import ru.tms.dto.RegisterRequest;
import ru.tms.token.TokenRepository;
import ru.tms.user.model.Role;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.tms.utils.TestData.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController")
class UserControllerTest {

    private static final String token = "eyJhbGciOiJIUzI1NiJ9" +
            ".eyJyb2xlIjoiUk9MRV9VU0VSIiwic3ViIjoiaXZhbjFAZXhhbX" +
            "BsZS5jb20iLCJpYXQiOjE3MzIwNTc1MjEsImV4cCI6MTczMjE0MzkyMX0" +
            ".OzM7xnNCWN1ZoL7MWhKF5cP09EImjeaCiAIEl8bCOOc";

    @Autowired
    private AuthenticationService authenticationService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Должен получить всех пользователей")
    void should_find_all_users() throws Exception {
        when(userService.findAll()).thenReturn(createUserDtoList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService, times(1)).findAll();
    }

    @Test
    @DisplayName("Должен получить пользователя по id")
    void should_get_by_id_user() throws Exception {

        when(userService.getById(TEST_USER_ID)).thenReturn(createdUserDto());

        mockMvc.perform(get("/users/{userId}", TEST_USER_ID))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService, times(1)).getById(TEST_USER_ID);
    }

    @Test
    @DisplayName("Должен создать пользователя")
    void should_create_user() throws Exception {

        /*AuthenticationResponse mockResponse = new AuthenticationResponse(token, "mockRefreshToken");
         */
        RegisterRequest request = createAdminRegisterRequest();

        //when(authenticationService.register(request)).thenReturn(mockResponse);

        when(userService.create(createUserDto())).thenReturn(createdUserDto());

        String accessToken = obtainAccessToken("user1", "pass", "ADMIN");

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + accessToken)
                                .content(mapper.writeValueAsString(createUserDto()))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        verify(userService, times(1)).create(createUserDto());
    }

    @Test
    @DisplayName("Должен создать пользователя")
    void should_create_user2() throws Exception {

        AuthenticationResponse mockResponse = new AuthenticationResponse("mockAccessToken", "mockRefreshToken");

        RegisterRequest request = createAdminRegisterRequest();

        when(authenticationService.register(request)).thenReturn(mockResponse);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.access_token").value("mockAccessToken"))
                .andExpect(jsonPath("$.refresh_token").value("mockRefreshToken"))
                .andDo(print())
                .andReturn();

        // Извлекаем access token из ответа
        String mockAccessToken = result.getResponse().getContentAsString();
        int startIndex = mockAccessToken.indexOf("\"access_token\":\"") + "\"access_token\":".length();
        int endIndex = mockAccessToken.indexOf("\",\"refresh_token");
        mockAccessToken = mockAccessToken.substring(startIndex, endIndex);

        when(userService.create(createUserDto())).thenReturn(createdUserDto());

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + "mockAccessToken")
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

        mockMvc.perform(delete("/users/{userId}", TEST_USER_ID))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService, times(1)).remove(TEST_USER_ID);
    }

    private String obtainAccessToken(String username, String password, String role) throws Exception {

        ResultActions result
                = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createAdminRegisterRequest())))
                .andExpect(status().isCreated());

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }
}