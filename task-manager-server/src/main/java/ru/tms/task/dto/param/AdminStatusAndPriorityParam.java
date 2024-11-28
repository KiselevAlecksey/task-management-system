package ru.tms.task.dto.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.tms.task.enums.TaskPriority;
import ru.tms.task.enums.TaskStatus;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminStatusAndPriorityParam {
    TaskStatus status;
    TaskPriority priority;
    Long taskId;


    public AdminStatusAndPriorityParam(TaskStatus status, TaskPriority priority) {
        this.status = status;
        this.priority = priority;
        this.taskId = null;
    }
}
