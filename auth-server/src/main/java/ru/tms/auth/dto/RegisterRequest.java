package ru.tms.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.tms.user.model.Role;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {

    Long id;

    @NotBlank
    String name;

    @NotBlank
    @Email
    String email;

    @NotBlank
    String password;

    String role;

    @Hidden
    Role eRole;
}
