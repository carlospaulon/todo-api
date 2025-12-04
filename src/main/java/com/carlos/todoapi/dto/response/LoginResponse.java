package com.carlos.todoapi.dto.response;

public record LoginResponse(

        String token,
        String type,
        String username,
        Long expiresIn

) {

    public LoginResponse(String token, String username, Long expiresIn) {
        this(token, "Bearer", username, expiresIn);
    }
}
