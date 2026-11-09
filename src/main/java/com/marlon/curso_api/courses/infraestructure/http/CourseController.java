package com.marlon.curso_api.courses.infraestructure.http;

import com.marlon.curso_api.courses.application.CourseResponse;
import com.marlon.curso_api.courses.application.CreateCourseCommand;
import com.marlon.curso_api.courses.application.CreateCourseHandler;
import com.marlon.curso_api.courses.application.GetAllCoursesQueryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/courses") // La URL final será /api/v1/courses
public class CourseController {
  private final CreateCourseHandler createCourseHandler;
  private final GetAllCoursesQueryHandler getAllCoursesQueryHandler;

  // Inyección de dependencias de ambos Handlers (CQRS)
  public CourseController(
      CreateCourseHandler createCourseHandler,
      GetAllCoursesQueryHandler getAllCoursesQueryHandler) {
    this.createCourseHandler = createCourseHandler;
    this.getAllCoursesQueryHandler = getAllCoursesQueryHandler;
  }

  @Operation(summary = "Crear un curso")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Curso creado correctamente"),
    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
    @ApiResponse(responseCode = "401", description = "No autenticado"),
    @ApiResponse(responseCode = "403", description = "No tienes permisos de administrador")
  })
  @SecurityRequirement(name = "bearerAuth")
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CourseResponse> create(@Valid @RequestBody CreateCourseCommand command) {
    CourseResponse response = createCourseHandler.handle(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // 2. GET: Despacha al Query Handler y devuelve HTTP 200 OK con la lista
  @GetMapping
  public ResponseEntity<List<CourseResponse>> getAll() {
    List<CourseResponse> response = getAllCoursesQueryHandler.handle();
    return ResponseEntity.ok(response);
  }
}
