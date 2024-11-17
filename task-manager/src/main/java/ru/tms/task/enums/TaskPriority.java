package ru.tms.task.enums;

import java.util.Optional;

public enum TaskPriority {
        BLOCKER,
        CRITICAL,
        MAJOR,
        MINOR,
        TRIVIAL;

    public static Optional<TaskPriority> from(String status) {
        for (TaskPriority value : TaskPriority.values()) {
            if (value.name().equals(status)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
