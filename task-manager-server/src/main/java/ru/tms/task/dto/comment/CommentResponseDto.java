package ru.tms.task.dto.comment;

import ru.tms.userduplicate.dto.UserResponseDto;

public record CommentResponseDto(
        long id,

        UserResponseDto creator,

        String content,

        String created) {
}
