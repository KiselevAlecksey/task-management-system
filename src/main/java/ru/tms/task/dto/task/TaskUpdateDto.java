package ru.tms.task.dto.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.tms.task.enums.TaskPriority;
import ru.tms.task.enums.TaskStatus;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class TaskUpdateDto {
    Long id;

    @Size(min = 2)
    String title;

    @Size(min = 2)
    String description;

    @Size(min = 4)
    String status;

    @Size(min = 5)
    String priority;

    Long executorId;

    TaskStatus taskStatus;

    TaskPriority taskPriority;
}