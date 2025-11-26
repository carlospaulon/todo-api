package com.carlos.todoapi.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        LocalDateTime timestamp,
        int status
) {
}
