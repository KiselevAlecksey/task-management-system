package ru.tms.task;

import ru.tms.task.dto.comment.CommentCreateDto;
import ru.tms.task.dto.comment.CommentResponseDto;
import ru.tms.task.dto.param.*;
import ru.tms.task.dto.task.TaskCreateDto;
import ru.tms.task.dto.task.TaskResponseDto;
import ru.tms.task.dto.task.TaskUpdateDto;
import ru.tms.task.enums.TaskPriority;

import java.util.List;

public interface TaskService {

    TaskResponseDto create(TaskCreateDto createDto);

    TaskResponseDto update(TaskUpdateDto updateDto);

    void remove(long taskId);

    TaskResponseDto getById(long taskId);

    List<TaskResponseDto> getPage(CommonTaskParam taskParam);

    TaskResponseDto assignExecutor(long taskId, long executorId);

    TaskResponseDto changeStatusOrPriority(AdminStatusAndPriorityParam param);

    TaskResponseDto changePriority(TaskPriority priority, long taskId);

    TaskResponseDto changeStatus(UserStatusParam param);

    CommentResponseDto createComment(CommentCreateDto createDto);
}
