package com.carlos.todoapi.dto.request;

import com.carlos.todoapi.entity.TaskPriority;
import com.carlos.todoapi.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateTaskRequest(
        @NotBlank
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,

        String description,

        TaskStatus status,

        TaskPriority priority,

        LocalDate dueDate


) {

    //Compact constructor
    public CreateTaskRequest {
        if (status == null) {
            status = TaskStatus.PENDING;
        }
    }
}

