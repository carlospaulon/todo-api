package com.carlos.todoapi.service;

import com.carlos.todoapi.dto.request.CreateTaskRequest;
import com.carlos.todoapi.dto.response.TaskResponse;
import com.carlos.todoapi.entity.Task;
import com.carlos.todoapi.entity.User;
import com.carlos.todoapi.repository.TaskRepository;
import com.carlos.todoapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.rmi.AccessException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public TaskResponse createTask(CreateTaskRequest request, String username) {
        //TODO: Criar Exceptions específicas
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        //TODO: Criar Mapper para Task e TaskResponse
        //TODO: Ou método auxiliar para conversão da Response
        Task task = new Task();

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());
        task.setUser(user);


        Task taskSaved = taskRepository.save(task);
        return new TaskResponse(
                taskSaved.getId(),
                taskSaved.getTitle(),
                taskSaved.getDescription(),
                task.getStatus(),
                taskSaved.getPriority(),
                taskSaved.getDueDate(),
                taskSaved.getUser().getId(),
                username,
                taskSaved.getCreatedAt(),
                taskSaved.getUpdatedAt()
        );
    }

    public List<TaskResponse> getTasksByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        List<Task> tasks = taskRepository.findByUserId(user.getId());

        List<TaskResponse> taskResponses = tasks.stream().map(task -> new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getUser().getId(),
                task.getUser().getUsername(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        )).collect(Collectors.toList());

        return taskResponses;

    }

    //o que está dando errado
    public TaskResponse getTaskById(Long id, String username) {
        Task taskById= taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found"));

        //se o usuário não for o mesmo, lança exceção
        if (!taskById.getUser().getUsername().equals(username)) {
            throw new BadCredentialsException("Não aceito");
        }

        return new TaskResponse(
                taskById.getId(),
                taskById.getTitle(),
                taskById.getDescription(),
                taskById.getStatus(),
                taskById.getPriority(),
                taskById.getDueDate(),
                taskById.getUser().getId(),
                taskById.getUser().getUsername(),
                taskById.getCreatedAt(),
                taskById.getUpdatedAt()
        );

    }
}
