package mk.ukim.finki.wp.jan2022.g1.service.impl;

import mk.ukim.finki.wp.jan2022.g1.model.Task;
import mk.ukim.finki.wp.jan2022.g1.model.TaskCategory;
import mk.ukim.finki.wp.jan2022.g1.model.User;
import mk.ukim.finki.wp.jan2022.g1.model.exceptions.InvalidTaskIdException;
import mk.ukim.finki.wp.jan2022.g1.repository.TaskRepository;
import mk.ukim.finki.wp.jan2022.g1.service.TaskService;
import mk.ukim.finki.wp.jan2022.g1.service.UserService;
import org.springframework.stereotype.Service;

import javax.transaction.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    private final UserService userService;
    private final TaskRepository taskRepository;

    public TaskServiceImpl(UserService userService, TaskRepository taskRepository) {
        this.userService = userService;
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> listAll() {
        return this.taskRepository.findAll();
    }

    @Override
    public Task findById(Long id) {
        return this.taskRepository.findById(id).orElseThrow(InvalidTaskIdException::new);
    }

    @Override
    @Transactional
    public Task create(String title, String description, TaskCategory category, List<Long> assignees, LocalDate dueDate) {
        List<User> assigneesList = null;
        if (assignees != null) {
            assigneesList = assignees.stream().map(r -> this.userService.findById(r)).collect(Collectors.toList());
        }
        return this.taskRepository.save(new Task(title, description, category, assigneesList, dueDate));
    }

    @Override
    @Transactional
    public Task update(Long id, String title, String description, TaskCategory category, List<Long> assignees) {
        List<User> assigneesList = null;
        Task task = findById(id);
        if (assignees != null) {
            assigneesList = assignees.stream().map(r -> this.userService.findById(r)).collect(Collectors.toList());
        }
        task.setCategory(category);
        task.setDescription(description);
        task.setAssignees(assigneesList);
        task.setTitle(title);
        return this.taskRepository.save(task);
    }

    @Override
    public Task delete(Long id) {
        Task task = findById(id);
        this.taskRepository.delete(task);
        return task;
    }

    @Override
    public Task markDone(Long id) {
        Task task = findById(id);
        task.setDone(true);
        this.taskRepository.save(task);
        this.taskRepository.delete(task);
        return task;
    }

    @Override
    public List<Task> filter(Long assigneeId, Integer lessThanDayBeforeDueDate) {
        User user = null;
        if (assigneeId != null && lessThanDayBeforeDueDate == null) {
            user = this.userService.findById(assigneeId);
            return this.taskRepository.findAllByAssigneesContaining(user);
        } else if (assigneeId == null && lessThanDayBeforeDueDate != null) {
            LocalDate date = LocalDate.now().plusDays(lessThanDayBeforeDueDate);
            return this.taskRepository.findAllByDueDateBefore(date);
        } else if (assigneeId != null && lessThanDayBeforeDueDate != null) {
            user = this.userService.findById(assigneeId);
            LocalDate date = LocalDate.now().plusDays(lessThanDayBeforeDueDate);
            return this.taskRepository.findAllByAssigneesContainingAndDueDateBefore(user,date);
        } else return listAll();
    }
}
