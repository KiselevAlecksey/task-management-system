package ru.tms.task;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.tms.task.dto.comment.CommentCreateDto;
import ru.tms.task.dto.task.TaskCreateDto;
import ru.tms.task.dto.task.TaskResponseDto;
import ru.tms.task.dto.task.TaskUpdateDto;
import ru.tms.task.dto.comment.CommentResponseDto;
import ru.tms.task.model.Comment;
import ru.tms.task.model.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "creatorId", target = "creator.id")
    @Mapping(source = "executorId", target = "executor.id")
    Task toTask(TaskCreateDto createDto);

    Task toTask(TaskUpdateDto updateDto);

    @Mapping(source = "creator.id", target = "creator.id")
    @Mapping(source = "executor.id", target = "executor.id")
    TaskResponseDto toTaskDto(Task task);

    @Mappings({
            @Mapping(source = "task.creator.id", target = "creator.id"),
            @Mapping(source = "task.creator.id", target = "executor.id"),
            @Mapping(target = "comments", source = "commentList")
    })
    TaskResponseDto toTaskDto(Task task, List<Comment> commentList);

    @Mapping(source = "creator.id", target = "creator.id")
    @Mapping(target = "created", expression = "java(formatDateTime(comment.getCreated()))")
    CommentResponseDto toCommentDto(Comment comment);

    @Mapping(source = "creatorId", target = "creator.id")
    @Mapping(source = "taskId", target = "task.id")
    Comment toComment(CommentCreateDto createDto);

    default String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime != null ? formatter.format(dateTime) : null;
    }

    default List<CommentResponseDto> map(List<Comment> comments) {
        return comments.stream()
                .map(this::toCommentDto)
                .toList();
    }
}
