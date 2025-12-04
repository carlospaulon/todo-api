package com.carlos.todoapi.controller;

import com.carlos.todoapi.dto.request.LoginRequest;
import com.carlos.todoapi.dto.request.RegisterRequest;
import com.carlos.todoapi.dto.response.LoginResponse;
import com.carlos.todoapi.dto.response.RegisterResponse;
import com.carlos.todoapi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody @Valid RegisterRequest registerRequest) {

        RegisterResponse response = authService.registerUser(registerRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse response = authService.loginUser(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
