package com.carlos.todoapi.service;

import com.carlos.todoapi.dto.request.RegisterRequest;
import com.carlos.todoapi.dto.response.RegisterResponse;
import com.carlos.todoapi.entity.User;
import com.carlos.todoapi.exception.EmailAlreadyExistsException;
import com.carlos.todoapi.exception.UserAlreadyExistsException;
import com.carlos.todoapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public RegisterResponse registerUser(RegisterRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("User already exist!");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already exist!");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        User saved = userRepository.save(user);

        return new RegisterResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getCreatedAt()
        );
    }
}
