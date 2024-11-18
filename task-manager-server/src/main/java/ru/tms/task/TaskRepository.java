package ru.tms.task;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tms.task.model.Task;


import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCreatorIdAndExecutorId(Long creatorId, Long executorId, Pageable pageable);

    List<Task> findByCreatorId(Long creatorId, Pageable pageable);

    List<Task> findByExecutorId(Long executorId, Pageable pageable);
}
