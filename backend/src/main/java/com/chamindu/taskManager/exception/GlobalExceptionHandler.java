package com.chamindu.taskManager.exception;

import com.chamindu.taskManager.dto.ErrorResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handles validation errors such as blank title or past due date
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField()
                                + ": "
                                + error.getDefaultMessage()
                )
                .collect(Collectors.joining(", "));
        
        log.warn("Request validation failed: {}", message);

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                message
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }
    // Handles your custom BadRequestException
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(
            BadRequestException ex
    ) {

        log.warn("Bad request: {}", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }
    // Handles invalid JSON or invalid enum values
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(
            HttpMessageNotReadableException ex
    ) {

        log.warn("Invalid JSON request body");

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Request Body",
                "The request contains invalid JSON or an invalid field value"
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }
    // Handles invalid URL values such as /api/tasks/abc
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex
    ) {
        log.warn(
                "Invalid parameter: name={}, value={}",
                ex.getName(),
                ex.getValue()
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Parameter",
                ex.getName() + " has an invalid value"
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    // Handles missing or invalid JWT tokens
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex
    ) {

        log.warn("Unauthorized request: {}", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.UNAUTHORIZED
        );
    }

    // Handles resources that cannot be found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex
    ) {
        log.warn("Resource not found: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.NOT_FOUND
        );
    }

    // Handles all unexpected backend errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception ex
    ) {
        // This prints the full technical error in Docker logs
        log.error("Unexpected server error", ex);
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected server error occurred"
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}