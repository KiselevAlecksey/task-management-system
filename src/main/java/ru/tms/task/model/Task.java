package ru.tms.task.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.tms.task.enums.TaskPriority;
import ru.tms.task.enums.TaskStatus;
import ru.tms.user.model.User;

import java.time.LocalDateTime;

import static ru.tms.task.enums.TaskStatus.NO_STATUS;

@Entity
@Table(name = "tasks")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    TaskStatus status = NO_STATUS;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    TaskPriority priority;

    @Column(name = "title")
    String title;

    @Column(name = "description")
    String description;

    // проверить какой тип загрузки использовать
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executor_id", nullable = false)
    User executor;

    @Column(name = "created_date", nullable = false)
    LocalDateTime created;
}
