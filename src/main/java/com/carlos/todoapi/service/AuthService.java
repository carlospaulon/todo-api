package com.carlos.todoapi.service;

import com.carlos.todoapi.dto.request.LoginRequest;
import com.carlos.todoapi.dto.request.RegisterRequest;
import com.carlos.todoapi.dto.response.LoginResponse;
import com.carlos.todoapi.dto.response.RegisterResponse;
import com.carlos.todoapi.entity.User;
import com.carlos.todoapi.exception.EmailAlreadyExistsException;
import com.carlos.todoapi.exception.UserAlreadyExistsException;
import com.carlos.todoapi.repository.UserRepository;
import com.carlos.todoapi.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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

    public LoginResponse loginUser(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            String token = jwtUtil.generateToken(authentication.getName());

            Long expiresIn = jwtUtil.getExpiration(); //avaliar a annotation @Getter no atributo do JwtUtil

            return new LoginResponse(
                    token,
                    authentication.getName(),
                    expiresIn
            );


        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
