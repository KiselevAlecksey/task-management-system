package ru.tms.task.dto.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.tms.task.enums.TaskPriority;
import ru.tms.task.enums.TaskStatus;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserStatusParam {
    TaskStatus status;
    Long userId;
    Long taskId;
}
