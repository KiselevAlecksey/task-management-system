package ru.tms.task.enums;

import java.util.Optional;

public enum TaskStatus {
    NO_STATUS,
    WAITING,
    IN_PROGRESS,
    REVIEW,
    IN_TESTING,
    REOPENED,
    DONE;

    public static Optional<TaskStatus> from(String status) {
        for (TaskStatus value : TaskStatus.values()) {
            if (value.name().equals(status)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
