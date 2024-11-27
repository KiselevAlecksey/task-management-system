package ru.tms.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.tms.exception.NotFoundException;
import ru.tms.user.dto.UserCreateDto;
import ru.tms.user.dto.UserResponseDto;
import ru.tms.user.dto.UserUpdateDto;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static ru.tms.user.Constant.TOKEN_BEARER;

@Slf4j
@Service
public class UserRestClient {

    RestClient client = RestClient.create("http://localhost:8080");

    public void register(UserCreateDto createDto, String token) {
        try {
            log.info("==> Register user is {} to task-manager-server start", createDto.getEmail());
            client
                    .post()
                    .uri("/users")
                    .header(TOKEN_BEARER, token)
                    .contentType(APPLICATION_JSON)
                    .body(createDto)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (request, response) -> {
                        throw new NotFoundException("User not created");
                    })
                    .body(UserResponseDto.class);
            log.info("<== Register user is {} to task-manager-server end", createDto.getEmail());
        } catch (
                RestClientException e) {
            log.info("==> Ошибка запроса к серверу: " + e.getMessage(), createDto.getEmail());
        }
    }

    public void create(UserCreateDto createDto, String token) {
        try {
            log.info("==> Create user is {} to task-manager-server start", createDto.getEmail());
            client
                    .post()
                    .uri("/users")
                    .header(TOKEN_BEARER, token)
                    .contentType(APPLICATION_JSON)
                    .body(createDto)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (request, response) -> {
                        throw new NotFoundException("User not created");
                    })
                    .body(UserResponseDto.class);
            log.info("<== Created user is {} to task-manager-server end", createDto.getEmail());
        } catch (
                RestClientException e) {
            log.info("==> Ошибка запроса к серверу: " + e.getMessage(), createDto.getEmail());
        }
    }

    public void update(UserUpdateDto updateDto, String token) {
        try {
            log.info("==> Create user is {} to task-manager-server start", updateDto.getEmail());
            client
                    .post()
                    .uri("/users/{userId}", updateDto.getId())
                    .header(TOKEN_BEARER, token)
                    .contentType(APPLICATION_JSON)
                    .body(updateDto)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (request, response) -> {
                        throw new NotFoundException("User not created");
                    })
                    .body(UserResponseDto.class);
            log.info("<== Created user is {} to task-manager-server end", updateDto.getEmail());
        } catch (
                RestClientException e) {
            log.info("==> Ошибка запроса к серверу: " + e.getMessage(), updateDto.getEmail());
        }
    }

    public void remove(long userId, String token) {
        try {
            log.info("==> Create user is {} to task-manager-server start", userId);
            client
                    .post()
                    .uri("/users/{userId}", userId)
                    .header(TOKEN_BEARER, token)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (request, response) -> {
                        throw new NotFoundException("User not created");
                    })
                    .body(UserResponseDto.class);
            log.info("<== Created user is {} to task-manager-server end", userId);
        } catch (
                RestClientException e) {
            log.info("==> Ошибка запроса к серверу: " + e.getMessage(), userId);
        }
    }

}
