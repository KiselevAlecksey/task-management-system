package ru.tms.task.dto.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.tms.task.dto.comment.CommentResponseDto;
import ru.tms.task.enums.TaskPriority;
import ru.tms.task.enums.TaskStatus;
import ru.tms.user.dto.UserResponseDto;
import ru.tms.user.model.User;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskResponseDto {

    Long id;

    String title;

    String description;

    TaskStatus status;

    TaskPriority priority;

    UserResponseDto creator;

    UserResponseDto executor;

    List<CommentResponseDto> comments;
}
