package ru.tms.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UserUpdateDto")
class UserUpdateDtoTest {

    @Test
    @DisplayName("Должен проверить поле name")
    void should_has_name() {
        UserUpdateDto dtoWithName = new UserUpdateDto();
        dtoWithName.setName("Имя пользователя");
        dtoWithName.isNotBlank(dtoWithName.getName());

        assertTrue(dtoWithName.hasName(), "hasName должен вернуть true, если имя установлено");

        UserUpdateDto dtoWithoutName = new UserUpdateDto();
        assertFalse(dtoWithoutName.hasName(), "hasName должен вернуть false, если имя не установлено");
    }

    @Test
    @DisplayName("Должен проверить поле email")
    void should_has_email() {
        UserUpdateDto dtoWithEmail = new UserUpdateDto();
        dtoWithEmail.setEmail("user@example.com");
        dtoWithEmail.isNotBlank(dtoWithEmail.getEmail());

        assertTrue(dtoWithEmail.hasEmail(), "hasEmail должен вернуть true, если email установлен");

        UserUpdateDto dtoWithoutEmail = new UserUpdateDto();
        assertFalse(dtoWithoutEmail.hasEmail(), "hasEmail должен вернуть false, если email не установлен");
    }

    @Test
    @DisplayName("Должен проверить поле role")
    void should_has_role() {
        UserUpdateDto dtoWithRole = new UserUpdateDto();
        dtoWithRole.setRole("ADMIN");
        assertTrue(dtoWithRole.hasRole(), "hasRole должен вернуть true, если роль установлена");

        UserUpdateDto dtoWithoutRole = new UserUpdateDto();
        assertFalse(dtoWithoutRole.hasRole(), "hasRole должен вернуть false, если роль не установлена");
    }
}