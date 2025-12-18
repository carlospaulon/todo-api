package com.carlos.todoapi.controller;


import com.carlos.todoapi.dto.request.CreateTaskRequest;
import com.carlos.todoapi.dto.request.UpdateStatusRequest;
import com.carlos.todoapi.dto.request.UpdateTaskRequest;
import com.carlos.todoapi.dto.response.TaskResponse;
import com.carlos.todoapi.entity.TaskStatus;
import com.carlos.todoapi.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody @Valid CreateTaskRequest taskRequest) {

        String username = getCurrentUsername();

        TaskResponse response = taskService.createTask(taskRequest, username);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(@RequestParam(required = false) TaskStatus status, @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable) {

        String username = getCurrentUsername();

        Page<TaskResponse> tasks = taskService.getTasksByUserAndStatus(username, status, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {

        String username = getCurrentUsername();

        TaskResponse response = taskService.getTaskById(id, username);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody @Valid UpdateTaskRequest request) {

        String username = getCurrentUsername();

        TaskResponse response = taskService.updateTask(id, request, username);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable Long id, @RequestBody @Valid UpdateStatusRequest request) {

        String username = getCurrentUsername();

        TaskResponse response = taskService.updateTaskStatus(id, request.status(), username);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {

        String username = getCurrentUsername();

        taskService.deleteTask(id, username);

        return ResponseEntity.noContent().build();

    }

    //Auxiliar method
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}

