package com.ecom.product_service.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.ecom.product_service.response.ErrorResponse;
import com.ecom.product_service.service.MessageService;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageService messageService;
    
    @Value("${spring.servlet.multipart.max-file-size:10MB}")
    private String maxFileSize;

    public GlobalExceptionHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                messageService.getMessage("error.validation"),
                LocalDateTime.now(),
                errors);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = messageService.getMessage("error.constraint");

        String exceptionMessage = ex.getMessage();
        if (exceptionMessage != null) {
            if (exceptionMessage.contains("foreign key constraint") || exceptionMessage.contains("fk_")) {
                if (exceptionMessage.contains("fk_products_brand")) {
                    message = messageService.getMessage("error.brand.in-use");
                } else if (exceptionMessage.contains("fk_products_category")) {
                    message = messageService.getMessage("error.category.in-use");
                } 
                else {
                    message = messageService.getMessage("error.data.in-use");
                }
            }
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                message,
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        // Extract numeric value from maxFileSize (e.g., "10MB" -> "10")
        String sizeValue = maxFileSize.replaceAll("[^0-9]", "");
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                messageService.getMessage("error.upload.size-exceeded", new Object[]{sizeValue}),
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        // Handle pagination errors and other IllegalArgumentException
        String message;
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("page")) {
            message = messageService.getMessage("error.pagination.invalid");
        } else {
            message = messageService.getMessage("error.internal", new Object[]{ex.getMessage()});
        }
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        // Don't handle Spring Security exceptions here - let SecurityConfig handlers deal with them
        if (ex instanceof AccessDeniedException ||
            ex instanceof AuthenticationException) {
            throw (RuntimeException) ex;
        }
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                messageService.getMessage("error.internal", new Object[]{ex.getMessage()}),
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
