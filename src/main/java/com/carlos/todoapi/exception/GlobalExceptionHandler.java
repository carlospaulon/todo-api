package com.carlos.todoapi.exception;

import com.carlos.todoapi.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //auxiliary method
    private ErrorResponse buildErrorMessage(
            String message,
            HttpStatus status,
            HttpServletRequest request
    ) {
        return new ErrorResponse(
                message,
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                request != null ? request.getRequestURI() : null
        );

    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistException(UserAlreadyExistsException ex, HttpServletRequest request) {

        ErrorResponse error = buildErrorMessage(
                ex.getMessage(),
                HttpStatus.CONFLICT,
                request
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistException(EmailAlreadyExistsException ex, HttpServletRequest request) {

        ErrorResponse error = buildErrorMessage(
                ex.getMessage(),
                HttpStatus.CONFLICT,
                request
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialException(BadCredentialsException ex, HttpServletRequest request) {
        String message = "Invalid username or password";

        ErrorResponse error = buildErrorMessage(
                message,
                HttpStatus.UNAUTHORIZED,
                request
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFoundException(TaskNotFoundException ex, HttpServletRequest request) {

        ErrorResponse error = buildErrorMessage(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                request
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAccessException(UnauthorizedAccessException ex, HttpServletRequest request) {

        ErrorResponse error = buildErrorMessage(
                ex.getMessage(),
                HttpStatus.FORBIDDEN,
                request
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex, HttpServletRequest request) {

        ErrorResponse error = buildErrorMessage(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                request
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String message = "Invalid JSON format. Please check your request body";

        ErrorResponse error = buildErrorMessage(
                message,
                HttpStatus.BAD_REQUEST,
                request
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = String.format(
                "Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(),
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );

        ErrorResponse error = buildErrorMessage(
                message,
                HttpStatus.BAD_REQUEST,
                request
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

        Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();

        String message = String.format(
                "Method '%s' not supported for this endpoint. Supported methods: %s",
                ex.getMethod(),
                supportedMethods != null ? supportedMethods : "N/A"
        );

        ErrorResponse error = buildErrorMessage(
                message,
                HttpStatus.METHOD_NOT_ALLOWED,
                request
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);

    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameterException(MissingServletRequestParameterException ex, HttpServletRequest request) {
        String message = String.format(
                "Required parameter '%s' of type '%s' is missing",
                ex.getParameterName(),
                ex.getParameterType()
        );

        ErrorResponse error = buildErrorMessage(
                message,
                HttpStatus.BAD_REQUEST,
                request
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        String message = "You don't have permission to access this resource";

        ErrorResponse error = buildErrorMessage(
                message,
                HttpStatus.FORBIDDEN,
                request
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {

        log.error("Unexpected error occurred at {}: {}",
                request.getRequestURI(),
                ex.getMessage(),
                ex);

        String message = "An unexpected error occurred. Please try again later.";

        ErrorResponse error = buildErrorMessage(
                message,
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
