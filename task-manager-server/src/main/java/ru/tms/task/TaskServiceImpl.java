package ru.tms.task;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.tms.exception.ParameterNotValidException;
import ru.tms.task.dto.comment.CommentCreateDto;
import ru.tms.task.dto.comment.CommentResponseDto;
import ru.tms.task.dto.param.*;
import ru.tms.task.dto.task.TaskCreateDto;
import ru.tms.task.dto.task.TaskResponseDto;
import ru.tms.task.dto.task.TaskUpdateDto;
import ru.tms.task.enums.TaskPriority;
import ru.tms.task.enums.TaskStatus;
import ru.tms.exception.NotFoundException;
import ru.tms.task.model.Comment;
import ru.tms.task.model.Task;
import ru.tms.user.UserRepository;
import ru.tms.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    public TaskResponseDto create(TaskCreateDto createDto) {
        Task task = taskMapper.toTask(createDto);

        User creator = userRepository.findById(createDto.getCreatorId())
                .orElseThrow(() -> new NotFoundException("пользователь не найден"));

        User executor = userRepository.findById(createDto.getExecutorId())
                .orElseThrow(() -> new NotFoundException("пользователь не найден"));

        task.getCreator().setEmail(creator.getEmail());
        task.getCreator().setName(creator.getName());

        task.getExecutor().setName(executor.getName());
        task.getExecutor().setEmail(executor.getEmail());
        task.setCreated(LocalDateTime.now());

        Task created = taskRepository.save(task);

        return taskMapper.toTaskDto(created);
    }

    @Override
    public TaskResponseDto update(TaskUpdateDto updateDto) {

        Task task = taskRepository.findById(updateDto.getId())
                .orElseThrow(() -> new NotFoundException("Задача не найдена"));

        User user;
        if (updateDto.getExecutorId() != null) {
            user = userRepository.findById(updateDto.getExecutorId())
                    .orElseThrow(() -> new NotFoundException("Пользователь \"исполнитель\" не найден"));
            task.setExecutor(user);
        }

        updateTaskFields(task, updateDto);

        Task updatedTask = taskRepository.save(task);

        return taskMapper.toTaskDto(updatedTask);
    }

    @Override
    public void remove(long taskId) {
        taskRepository.deleteById(taskId);
    }

    @Override
    public TaskResponseDto getById(long taskId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Задача не найдена"));

        List<Comment> commentList = commentRepository.findAllByTaskIdIn(Collections.singletonList(taskId));

        return taskMapper.toTaskDto(task, commentList);
    }

    @Override
    public List<TaskResponseDto> getPage(CommonTaskParam taskParam) {
        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
        Pageable pageable = PageRequest.of(taskParam.from(), taskParam.size(), sortByCreated);
        List<Task> taskList = Collections.emptyList();

        if (taskParam.creatorId() != null && taskParam.executorId() != null) {
            taskList = taskRepository.findByCreatorIdAndExecutorId(taskParam.creatorId(),
                    taskParam.executorId(), pageable).stream().toList();
        }

        if (taskParam.creatorId() == null && taskParam.executorId() == null) {
            taskList = taskRepository.findAll(pageable).stream().toList();
        } else if (taskParam.creatorId() == null) {
            taskList = taskRepository.findByExecutorId(taskParam.executorId(), pageable).stream()
                    .toList();
        } else if (taskParam.executorId() == null) {
            taskList = taskRepository.findByCreatorId(taskParam.creatorId(), pageable).stream()
                    .toList();
        }

        Map<Long, List<Comment>> commentMap = getCommentMap(taskList);

        return taskList.stream().map(task -> taskMapper.toTaskDto(task,
                        commentMap.get(task.getId()) == null
                                ? Collections.emptyList() : commentMap.get(task.getId())))
                .toList();
    }

    private Map<Long, List<Comment>> getCommentMap(List<Task> taskList) {
        List<Long> taskIds = taskList.stream().map(Task::getId).toList();
        List<Comment> commentList = commentRepository.findAllByTaskIdIn(taskIds);
        Map<Long, List<Comment>> commentMap = new HashMap<>();

        if (!commentList.isEmpty()) {
            commentList.forEach(comment -> {
                long taskId = comment.getTask().getId();
                if (!commentMap.containsKey(taskId)) {
                    commentMap.put(taskId, new ArrayList<>());
                }
                List<Comment> taskComments = commentMap.get(taskId);
                taskComments.add(comment);
            });
        }
        return commentMap;
    }

    @Override
    public TaskResponseDto assignExecutor(long taskId, Long executorId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Задача не найдена"));

        User user = userRepository.findById(executorId)
                    .orElseThrow(() -> new NotFoundException("Пользователь \"исполнитель\" не найден"));
        task.setExecutor(user);

        return taskMapper.toTaskDto(task);
    }

    @Override
    public TaskResponseDto changeStatusOrPriority(AdminStatusAndPriorityParam param) {
        long taskId = param.getTaskId();
        TaskStatus status = param.getStatus();
        TaskPriority priority = param.getPriority();
        Task updated = taskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("Задача не найдена"));

        if (!(updated.getStatus().equals(status) && updated.getPriority().equals(priority))) {

            if (status != null) {
                updated.setStatus(status);
            }

            if (priority != null) {
                updated.setPriority(priority);
            }

            updated = taskRepository.save(updated);
        }

        return taskMapper.toTaskDto(updated);
    }

    @Override
    public TaskResponseDto changePriority(TaskPriority priority, long taskId) {
        Task updated = taskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("Задача не найдена"));

        if (!(updated.getPriority().equals(priority))) {

            updated.setPriority(priority);

            taskRepository.save(updated);
        }

        return taskMapper.toTaskDto(updated);
    }

    @Override
    public TaskResponseDto changeStatus(UserStatusParam param) {
        Task updated = taskRepository.findById(param.getTaskId())
                .orElseThrow(() -> new NotFoundException("Задача не найдена"));

        User user = userRepository.findById(param.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (!(updated.getExecutor().getId().equals(user.getId())
                || updated.getCreator().getId().equals(user.getId()))) {
            throw new ParameterNotValidException("Пользователь не имеет доступа к смене статуса",
                    param.getUserId().toString());
        }

        if (!(updated.getStatus().equals(param.getStatus()))) {

            updated.setStatus(param.getStatus());

            taskRepository.save(updated);
        }

        return taskMapper.toTaskDto(updated);
    }

    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public CommentResponseDto createComment(CommentCreateDto createDto) {
        User user = userRepository.findById(createDto.getCreatorId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Task task = taskRepository.findById(createDto.getTaskId())
                .orElseThrow(() -> new NotFoundException("Задача не найдена"));

        if (!(task.getExecutor().getId().equals(user.getId())
                || task.getCreator().getId().equals(user.getId()))) {
            throw new ParameterNotValidException("Пользователь не имеет доступа к комментированию",
                    createDto.getCreatorId().toString());
        }

        Comment newComment = taskMapper.toComment(createDto);

        newComment.setTask(task);
        newComment.setCreator(user);
        newComment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(newComment);

        return taskMapper.toCommentDto(savedComment);
    }

    private void updateTaskFields(Task task, TaskUpdateDto updateDto) {
        if (updateDto.getTitle() != null) {
            task.setTitle(updateDto.getTitle());
        }

        if (updateDto.getDescription() != null) {
            task.setDescription(updateDto.getDescription());
        }

        if (updateDto.getTaskStatus() != null) {
            task.setStatus(updateDto.getTaskStatus());
        }

        if (updateDto.getTaskPriority() != null) {
            task.setPriority(updateDto.getTaskPriority());
        }
    }
}
