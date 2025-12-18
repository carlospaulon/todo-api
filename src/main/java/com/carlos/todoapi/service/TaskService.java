package com.carlos.todoapi.service;

import com.carlos.todoapi.dto.request.CreateTaskRequest;
import com.carlos.todoapi.dto.request.UpdateTaskRequest;
import com.carlos.todoapi.dto.response.TaskResponse;
import com.carlos.todoapi.entity.Task;
import com.carlos.todoapi.entity.TaskStatus;
import com.carlos.todoapi.entity.User;
import com.carlos.todoapi.exception.TaskNotFoundException;
import com.carlos.todoapi.exception.UnauthorizedAccessException;
import com.carlos.todoapi.exception.UserNotFoundException;
import com.carlos.todoapi.mapper.TaskMapper;
import com.carlos.todoapi.repository.TaskRepository;
import com.carlos.todoapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
    }

    public TaskResponse createTask(CreateTaskRequest request, String username) {
        User user = getCurrentUser(username);

        Task task = taskMapper.toEntity(request, user);

        Task taskSaved = taskRepository.save(task);

        return taskMapper.toResponse(taskSaved);
    }

    public Page<TaskResponse> getTasksByUser(String username, Pageable pageable) {
        User user = getCurrentUser(username);
        Page<Task> tasks = taskRepository.findByUserId(user.getId(), pageable);

        return tasks.map(taskMapper::toResponse);

    }

    public TaskResponse getTaskById(Long id, String username) {
        Task taskById = getTaskAndVerifyOwnership(id, username);

        return taskMapper.toResponse(taskById);

    }

    public TaskResponse updateTask(Long id, UpdateTaskRequest request, String username) {
        Task task = getTaskAndVerifyOwnership(id, username);

        if (request.title() != null) {
            task.setTitle(request.title());
        }

        if (request.description() != null) {
            task.setDescription(request.description());
        }

        if (request.status() != null) {
            task.setStatus(request.status());
        }

        if (request.priority() != null) {
            task.setPriority(request.priority());
        }

        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }

        Task taskSaved = taskRepository.save(task);

        return taskMapper.toResponse(taskSaved);

    }

    public TaskResponse updateTaskStatus(Long id, TaskStatus status, String username) {
        Task task = getTaskAndVerifyOwnership(id, username);

        task.setStatus(status);
        Task taskSaved = taskRepository.save(task);

        return taskMapper.toResponse(taskSaved);

    }

    public void deleteTask(Long id, String username) {

        Task task = getTaskAndVerifyOwnership(id, username);
        taskRepository.delete(task);

    }

    public Page<TaskResponse> getTasksByUserAndStatus(String username, TaskStatus status, Pageable pageable) {
        User user = getCurrentUser(username);

        Page<Task> tasks;

        if (status == null) {
            tasks = taskRepository.findByUserId(user.getId(), pageable);
        } else {
            tasks = taskRepository.findByUserIdAndStatus(user.getId(), status, pageable);
        }

        return tasks.map(taskMapper::toResponse);
    }


    //Utility methods
    private User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private Task getCurrentTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task by ID not found"));
    }

    private Task getTaskAndVerifyOwnership(Long id, String username) {
        Task task = getCurrentTask(id);

        if (!task.getUser().getUsername().equals(username)) {
            throw new UnauthorizedAccessException("Acesso não autorizado");
        }

        return task;
    }

}
