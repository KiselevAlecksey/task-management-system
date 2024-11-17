package ru.tms.task.dto.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.tms.task.enums.TaskPriority;
import ru.tms.task.enums.TaskStatus;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskCreateDto {
    Long id;

    @NotBlank
    String title;

    @NotBlank
    String description;

    String status;

    String priority;

    Long creatorId;

    Long executorId;

    TaskStatus taskStatus;

    TaskPriority taskPriority;
}
