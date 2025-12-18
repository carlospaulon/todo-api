package com.carlos.todoapi.dto.request;

import com.carlos.todoapi.entity.TaskPriority;
import com.carlos.todoapi.entity.TaskStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateTaskRequest(
        @NotBlank(message = "Title cannot be blank")
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        TaskStatus status,

        TaskPriority priority,

        @Future(message = "Due date must be in the future")
        LocalDate dueDate


) {

    //Compact constructor
    public CreateTaskRequest {
        if (status == null) {
            status = TaskStatus.PENDING;
        }
    }
}

