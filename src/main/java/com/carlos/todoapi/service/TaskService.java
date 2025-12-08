package com.carlos.todoapi.service;

import com.carlos.todoapi.dto.request.CreateTaskRequest;
import com.carlos.todoapi.dto.response.TaskResponse;
import com.carlos.todoapi.entity.Task;
import com.carlos.todoapi.entity.User;
import com.carlos.todoapi.exception.TaskNotFoundException;
import com.carlos.todoapi.exception.UnauthorizedAccessException;
import com.carlos.todoapi.exception.UserNotFoundException;
import com.carlos.todoapi.mapper.TaskMapper;
import com.carlos.todoapi.repository.TaskRepository;
import com.carlos.todoapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

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

    //Certo
    public TaskResponse createTask(CreateTaskRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Task task = taskMapper.toEntity(request, user);

        Task taskSaved = taskRepository.save(task);

        return taskMapper.toResponse(taskSaved);
    }

    public List<TaskResponse> getTasksByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Task> tasks = taskRepository.findByUserId(user.getId());

        return tasks.stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());

    }

    //Trocar a exceção
    public TaskResponse getTaskById(Long id, String username) {
        Task taskById= taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task by ID not found"));

        //se o usuário não for o mesmo, lança exceção
        if (!taskById.getUser().getUsername().equals(username)) {
            throw new UnauthorizedAccessException("Acesso não autorizado");
        }

        return taskMapper.toResponse(taskById);

    }
}
