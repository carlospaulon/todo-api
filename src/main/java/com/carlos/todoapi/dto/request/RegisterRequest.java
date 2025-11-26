package com.carlos.todoapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @Email(message = "Invalid email format")
        @NotBlank(message = "E-mail cannot be blank")
        @Size(max = 100, message = "E-mail has a maximum of 100 characters")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
        String password
) {
}
