package ru.tms.task.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tms.task.TaskService;
import ru.tms.task.dto.param.UserStatusParam;
import ru.tms.task.dto.task.TaskResponseDto;
import ru.tms.task.enums.TaskStatus;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping(path = "/users/tasks")
public class UserTaskController {

    public static final String USER_ID_HEADER = "X-TaskManager-User-Id";

    private final TaskService taskService;

    @PatchMapping("/{taskId}/status")
    @PreAuthorize("hasAuthority('user:update')")
    public TaskResponseDto changeStatus(@RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(required = false) String status, @PathVariable long taskId) {
        log.info("==> Change status {} start", status);
        TaskStatus taskStatus = TaskStatus.from(status)
                .orElseThrow(() -> new IllegalArgumentException("Не поддерживаемое состояние: " + status));

        TaskResponseDto changed = taskService.changeStatus(new UserStatusParam(taskStatus, userId, taskId));
        log.info("<== Retrieved status {} complete", status);
        return changed;
    }
}
