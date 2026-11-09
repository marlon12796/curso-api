package com.marlon.curso_api.courses.domain;

public class Course {
  private final Long id;
  private String title;
  private String description;
  private double price;
  private CourseStatus status;

  // Constructor con validaciones de negocio del Dominio (nace en DRAFT)
  public Course(Long id, String title, String description, double price) {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("El título del curso no puede estar vacío");
    }
    if (price < 0) {
      throw new IllegalArgumentException("El precio no puede ser negativo");
    }
    this.id = id;
    this.title = title;
    this.description = description;
    this.price = price;
    this.status = CourseStatus.DRAFT;
  }

  // Constructor para reconstruir desde la Base de Datos (con su estado
  // persistido)
  public Course(Long id, String title, String description, double price, CourseStatus status) {
    this(id, title, description, price);
    this.status = status;
  }

  // Regla de negocio: publicar el curso
  public void publish() {
    this.status = CourseStatus.PUBLISHED;
  }

  // Getters para consultar los datos
  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public double getPrice() {
    return price;
  }

  public CourseStatus getStatus() {
    return status;
  }

  // Método de Dominio exclusivo para reconstruir desde la BD:
  public static Course reconstitute(
      Long id, String title, String description, double price, CourseStatus status) {
    return new Course(id, title, description, price, status);
  }
}
