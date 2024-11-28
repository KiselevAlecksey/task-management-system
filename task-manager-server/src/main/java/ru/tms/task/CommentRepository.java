package ru.tms.task;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tms.task.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByTaskIdIn(List<Long> taskIds);
}
