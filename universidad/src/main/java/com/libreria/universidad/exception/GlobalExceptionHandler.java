package com.libreria.universidad.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
    ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage(), List.of());
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }
  @ExceptionHandler(DuplicateResourceException.class)
  public ResponseEntity<ApiError> handleDuplicate(DuplicateResourceException ex) {
    ApiError error = new ApiError(HttpStatus.CONFLICT.value(), ex.getMessage(), List.of());
    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
    List<String> details = ex.getBindingResult().getFieldErrors().stream()
      .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
      .collect(Collectors.toList());
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), "Error de validacion", details);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneral(Exception ex) {
    ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Error interno del servidor: " + ex.getMessage(), List.of());
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
