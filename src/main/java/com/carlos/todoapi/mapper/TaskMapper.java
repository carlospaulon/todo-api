package com.carlos.todoapi.mapper;

import com.carlos.todoapi.dto.request.CreateTaskRequest;
import com.carlos.todoapi.dto.response.TaskResponse;
import com.carlos.todoapi.entity.Task;
import com.carlos.todoapi.entity.User;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    //Entidade
    public Task toEntity(CreateTaskRequest request, User user) {
        Task task = new Task();

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());
        task.setUser(user);
        return task;
    }

    //Repsosta da api
    public TaskResponse toResponse(Task task) {
        return new TaskResponse(
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
        );
    }
}
