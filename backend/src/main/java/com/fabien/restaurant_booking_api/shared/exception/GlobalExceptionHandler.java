package com.fabien.restaurant_booking_api.shared.exception;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Void> handleEntityNotFound(EntityNotFoundException ex) {
    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, List<String>>> handleValidation(
      MethodArgumentNotValidException ex) {
    Map<String, List<String>> errors = new HashMap<>();

    ex.getBindingResult().getFieldErrors().forEach(error -> {
      String fieldName = error.getField();
      String errorMessage = error.getDefaultMessage();
      errors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
    });

    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage());
    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, String>> handleDataIntegrity(
      DataIntegrityViolationException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Une ressource avec ces données existe déjà");
    return ResponseEntity.status(409).body(error);
  }
}
