package com.carlos.todoapi.dto.request;

import com.carlos.todoapi.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull(message = "Status cannot be null")
        TaskStatus status
) {
}
