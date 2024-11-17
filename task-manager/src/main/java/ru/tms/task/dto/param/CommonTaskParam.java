package ru.tms.task.dto.param;

public record CommonTaskParam(Long creatorId, Long executorId, int from, int size) {
}
