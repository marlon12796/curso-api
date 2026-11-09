package com.marlon.curso_api.courses.infraestructure.http;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.marlon.curso_api.courses.application.CourseResponse;
import com.marlon.curso_api.courses.application.CreateCourseHandler;
import com.marlon.curso_api.courses.application.GetAllCoursesQueryHandler;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@SpringBootTest
@AutoConfigureMockMvc
@Import(com.marlon.curso_api.shared.infraestructure.security.SecurityConfig.class)
class CourseControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CreateCourseHandler createCourseHandler;

  @MockitoBean private GetAllCoursesQueryHandler getAllCoursesQueryHandler;

  @Test
  void shouldGetCoursesWithoutAuthentication() throws Exception {
    CourseResponse course =
        new CourseResponse(1L, "Curso Java", "Curso de Spring Boot", 100, "DRAFT");

    when(getAllCoursesQueryHandler.handle()).thenReturn(List.of(course));

    mockMvc
        .perform(get("/courses"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("Curso Java"));
  }

  @Test
  void shouldCreateCourseAsAdmin() throws Exception {
    CourseResponse course =
        new CourseResponse(1L, "Curso Java", "Curso de Spring Boot", 100, "DRAFT");

    when(createCourseHandler.handle(any())).thenReturn(course);

    mockMvc
        .perform(
            post("/courses")
                .with(adminUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Curso Java",
                      "description": "Curso de Spring Boot",
                      "price": 100
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("Curso Java"));
  }

  @Test
  void shouldRejectCourseCreationForUser() throws Exception {
    mockMvc
        .perform(
            post("/courses")
                .with(user("user@test.com").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Curso Java",
                      "description": "Curso de Spring Boot",
                      "price": 100
                    }
                    """))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldRejectInvalidCourse() throws Exception {
    mockMvc
        .perform(
            post("/courses")
                .with(adminUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "",
                      "description": "",
                      "price": -10
                    }
                    """))
        .andExpect(status().isBadRequest());
  }

  private RequestPostProcessor adminUser() {
    return user("admin@test.com").roles("ADMIN");
  }
}
