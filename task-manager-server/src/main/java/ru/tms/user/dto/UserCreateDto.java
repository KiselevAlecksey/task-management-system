package ru.tms.user.dto;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.tms.user.model.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateDto {

    @Hidden
    Long id;

    @NotBlank
    String name;

    @NotBlank
    String email;

    String role;

    @Hidden
    Role eRole;
}
