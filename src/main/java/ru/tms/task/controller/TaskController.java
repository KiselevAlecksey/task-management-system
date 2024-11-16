package ru.tms.task.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.tms.task.TaskService;
import ru.tms.task.dto.comment.CommentCreateDto;
import ru.tms.task.dto.comment.CommentResponseDto;
import ru.tms.task.dto.task.TaskResponseDto;
import ru.tms.task.dto.param.CommonTaskParam;

import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/tasks")
public class TaskController {

    public static final String USER_ID_HEADER = "X-TaskManager-User-Id";

    private final TaskService taskService;

    @GetMapping
    public List<TaskResponseDto> getPage(@RequestParam(required = false) Long creatorId,
                                         @RequestParam(required = false) Long executorId,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        CommonTaskParam taskParam = new CommonTaskParam(creatorId, executorId, from, size);
        log.info("==> Get tasks page {} start", taskParam);
        List<TaskResponseDto> tasks = taskService.getPage(taskParam);
        log.info("<== Retrieved tasks page complete, count: {}", tasks.size());
        return tasks;
    }

    @PostMapping("/{taskId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable long taskId,
            @RequestBody @Validated CommentCreateDto comment) {
        log.info("==> Comment create {} start", comment);
        comment.setCreatorId(userId);
        comment.setTaskId(taskId);
        CommentResponseDto dto = taskService.createComment(comment);
        log.info("<== Comment created {} complete", dto);
        return dto;
    }
}
