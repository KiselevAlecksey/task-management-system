package ru.tms.task.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.tms.task.TaskService;
import ru.tms.task.enums.TaskPriority;
import ru.tms.task.enums.TaskStatus;
import ru.tms.task.dto.task.TaskCreateDto;
import ru.tms.task.dto.task.TaskResponseDto;
import ru.tms.task.dto.task.TaskUpdateDto;
import ru.tms.task.dto.param.AdminStatusAndPriorityParam;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping(path = "/admin/tasks")
public class AdminTaskController {

    public static final String USER_ID_HEADER = "X-TaskManager-User-Id";

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('admin:create')")
    public TaskResponseDto create(@RequestHeader(USER_ID_HEADER) long creatorId,
                                  @RequestBody @Validated TaskCreateDto createDto) {
        log.info("==> Create task {} start", createDto);
        AdminStatusAndPriorityParam param = statusAndPriorityValid(createDto.getStatus(), createDto.getPriority());

        createDto.setCreatorId(creatorId);
        createDto.setTaskStatus(param.getStatus());
        createDto.setTaskPriority(param.getPriority());
        TaskResponseDto created = taskService.create(createDto);
        log.info("<== Created task {} complete", created);
        return created;
    }

    @PatchMapping("/{taskId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public TaskResponseDto update(@PathVariable long taskId,
                                  @RequestBody @Validated TaskUpdateDto updateDto) {
        log.info("==> Update task with id {} start", taskId);
        AdminStatusAndPriorityParam param = statusAndPriorityValid(updateDto.getStatus(), updateDto.getPriority());

        updateDto.setId(taskId);
        updateDto.setTaskStatus(param.getStatus());
        updateDto.setTaskPriority(param.getPriority());
        TaskResponseDto updated = taskService.update(updateDto);
        log.info("<== Updated task with id {} complete", taskId);
        return updated;
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable long taskId) {
        log.info("==> Remove task with id {} start", taskId);
        taskService.remove(taskId);
        log.info("<== Removed task with id {} complete", taskId);
    }

    @PatchMapping("/{taskId}/assign/{executorId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public TaskResponseDto assignExecutor(@RequestHeader(USER_ID_HEADER) long creatorId,
                                          @PathVariable long taskId, @PathVariable long executorId) {
        log.info("==> Assign executor with id {} to task ID {} start", executorId, taskId);
        TaskResponseDto assigned = taskService.assignExecutor(taskId, executorId);
        log.info("<== Assigned executor to task id {} complete", taskId);
        return assigned;
    }

    @PatchMapping("/{taskId}/status-and-priority")
    @PreAuthorize("hasAuthority('admin:update')")
    public TaskResponseDto changeStatusOrPriority(@RequestParam(required = false) String status,
                                                  @RequestParam(required = false) String priority,
                                                  @PathVariable long taskId) {
        AdminStatusAndPriorityParam param = statusAndPriorityValid(status, priority);
        param.setTaskId(taskId);
        log.info("==> Change status {} and priority {} start", status, priority);
        TaskResponseDto changed = taskService.changeStatusOrPriority(param);
        log.info("<== Retrieved status {} and priority {} complete", status, priority);
        return changed;
    }

    private static AdminStatusAndPriorityParam statusAndPriorityValid(String status, String priority) {
        TaskStatus taskStatus = null;
        TaskPriority taskPriority = null;


        if (status != null) {
            String statusUpperCase = status.toUpperCase();
            taskStatus = TaskStatus.from(statusUpperCase)
                    .orElseThrow(() -> new IllegalArgumentException("не поддерживаемое состояние: " + status));
        }

        if (priority != null) {
            String priorityUpperCase = priority.toUpperCase();
            taskPriority = TaskPriority.from(priorityUpperCase)
                    .orElseThrow(() -> new IllegalArgumentException("не поддерживаемое состояние: " + priority));
        }

        return new AdminStatusAndPriorityParam(taskStatus, taskPriority);
    }
}

