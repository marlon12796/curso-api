package com.marlon.curso_api.shared.infraestructure.http;

import com.marlon.curso_api.identity.domain.exception.EmailAlreadyRegisteredException;
import com.marlon.curso_api.identity.domain.exception.InvalidCredentialsException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // <-- El Exception Filter global de Spring Boot
public class GlobalExceptionHandler {

  @ExceptionHandler(EmailAlreadyRegisteredException.class)
  public ResponseEntity<Map<String, Object>> handleEmailAlreadyRegistered(
      EmailAlreadyRegisteredException ex) {
    return buildError(HttpStatus.CONFLICT, ex.getMessage());
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidCredentials(
      InvalidCredentialsException ex) {
    return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage());
  }

  private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message) {
    Map<String, Object> error = new HashMap<>();
    error.put("timestamp", LocalDateTime.now());
    error.put("status", status.value());
    error.put("error", status.getReasonPhrase());
    error.put("message", message);
    return ResponseEntity.status(status).body(error);
  }

  // 2. Captura los errores de validación de @Valid (@NotBlank, @Positive)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidationErrors(
      MethodArgumentNotValidException ex) {
    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));
    ApiErrorResponse response =
        new ApiErrorResponse(
            LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Validation Error", fieldErrors);
    return ResponseEntity.badRequest().body(response);
  }

  // 3. Captura rutas que no existen (ej: /coursesx o /cualquier-cosa)
  @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNoResourceFound(
      org.springframework.web.servlet.resource.NoResourceFoundException ex) {
    Map<String, Object> error = new HashMap<>();
    error.put("timestamp", LocalDateTime.now());
    error.put("status", HttpStatus.NOT_FOUND.value());
    error.put("error", "Not Found");
    error.put("message", "La ruta solicitada no existe: " + ex.getResourcePath());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }
}
