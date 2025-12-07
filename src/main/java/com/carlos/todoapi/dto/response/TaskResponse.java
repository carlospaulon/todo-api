package com.carlos.todoapi.dto.response;

import com.carlos.todoapi.entity.TaskPriority;
import com.carlos.todoapi.entity.TaskStatus;
import com.carlos.todoapi.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(

        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        Long userId,
        String username,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {

}
