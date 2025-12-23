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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest validRegisterRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        validRegisterRequest = new RegisterRequest(
                "testuser",
                "test@example.com",
                "password123"
        );

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("encoded_password");
    }

    @Test
    @DisplayName("Should register user successfully")
    void testRegisterUser_Success() {

        //Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        //Act
        RegisterResponse response = authService.registerUser(validRegisterRequest);

        //Assert
        assertNotNull(response);
        assertEquals("testuser", response.username());
        assertEquals("test@example.com", response.email());

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void testRegisterUser_UsernameExists() {
        //Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        //Act and Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            authService.registerUser(validRegisterRequest);
        });

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testRegisterUser_EmailExists() {
        //Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        //Act and Assert
        assertThrows(EmailAlreadyExistsException.class, () -> {
            authService.registerUser(validRegisterRequest);
        });

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should login successfully and return JWT token")
    void testLoginUser_Sucess() {
        //Arrange
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(jwtUtil.generateToken(anyString())).thenReturn("mock_jwt_token");
        when(jwtUtil.getExpiration()).thenReturn(3600000L);

        //Act
        LoginResponse response = authService.loginUser(loginRequest);

        //Assert
        assertNotNull(response);
        assertEquals("mock_jwt_token", response.token());
        assertEquals("Bearer", response.type());
        assertEquals("testuser", response.username());
        assertEquals(3600000L, response.expiresIn());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken("testuser");
    }

    @Test
    @DisplayName("Should throw exception when login credentials are invalid")
    void testLoginUser_InvalidCredentials() {
        //Arrange
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Invalid credentials"));

        //Act and Assert
        assertThrows(BadCredentialsException.class, () -> {
            authService.loginUser(loginRequest);
        });

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(anyString());
    }
}
