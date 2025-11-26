package com.carlos.todoapi.dto.response;

import java.time.LocalDateTime;

public record RegisterResponse(
        Long id,
        String username,
        String email,
        LocalDateTime createdAt
) {
}
