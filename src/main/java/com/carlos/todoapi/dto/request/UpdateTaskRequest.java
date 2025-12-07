package com.carlos.todoapi.dto.request;

import com.carlos.todoapi.entity.TaskPriority;
import com.carlos.todoapi.entity.TaskStatus;

import java.time.LocalDate;

public record UpdateTaskRequest(

        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate

) {
}
