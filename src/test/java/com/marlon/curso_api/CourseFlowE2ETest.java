package com.marlon.curso_api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.marlon.curso_api.courses.infraestructure.persistence.SpringDataCourseRepository;
import com.marlon.curso_api.identity.domain.Role;
import com.marlon.curso_api.identity.infrastructure.persistence.UserJpaEntity;
import com.marlon.curso_api.identity.infrastructure.persistence.UserJpaRepository;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CourseFlowE2ETest {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserJpaRepository userRepository;

  @Autowired private SpringDataCourseRepository courseRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  private String testEmail;

  @AfterEach
  void cleanUp() {
    courseRepository.findAll().stream()
        .filter(course -> course.getTitle().startsWith("Curso E2E "))
        .forEach(course -> courseRepository.deleteById(course.getId()));

    if (testEmail != null) {
      userRepository.findByEmail(testEmail).ifPresent(userRepository::delete);
    }
  }

  @Test
  void shouldRegisterLoginAndRejectCourseCreationAsUser() throws Exception {
    testEmail = uniqueEmail();

    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "email": "%s",
                      "password": "123456"
                    }
                    """
                        .formatted(testEmail)))
        .andExpect(status().isCreated());

    String token = login(testEmail, "123456");

    mockMvc
        .perform(
            post("/courses")
                .header("Authorization", "Bearer " + token)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(courseJson()))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldLoginAsAdminAndCreateCourse() throws Exception {
    testEmail = uniqueEmail();
    userRepository.save(
        new UserJpaEntity(null, testEmail, passwordEncoder.encode("123456"), Role.ADMIN));

    String token = login(testEmail, "123456");

    String response =
        mockMvc
            .perform(
                post("/courses")
                    .header("Authorization", "Bearer " + token)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(courseJson()))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Number courseId = JsonPath.read(response, "$.id");
    org.junit.jupiter.api.Assertions.assertNotNull(courseId);
  }

  private String login(String email, String password) throws Exception {
    String response =
        mockMvc
            .perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "email": "%s",
                          "password": "%s"
                        }
                        """
                            .formatted(email, password)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return JsonPath.read(response, "$.token");
  }

  private String courseJson() {
    return """
    {
      "title": "Curso E2E %s",
      "description": "Curso creado mediante prueba de integración",
      "price": 99.99
    }
    """
        .formatted(UUID.randomUUID());
  }

  private String uniqueEmail() {
    return "e2e-%s@test.com".formatted(UUID.randomUUID());
  }
}
