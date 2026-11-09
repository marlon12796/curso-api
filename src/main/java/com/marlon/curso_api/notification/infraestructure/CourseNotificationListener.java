package com.marlon.curso_api.notification.infraestructure;

import com.marlon.curso_api.courses.domain.event.CourseCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class CourseNotificationListener {
  @Async // 👈 ¡EJECUTA EN UN HILO SEPARADO! No bloquea la petición HTTP
  @EventListener // 👈 Escucha automáticamente cuando alguien publica CourseCreatedEvent
  public void onCourseCreated(CourseCreatedEvent event) throws InterruptedException {
    System.out.println(
        "⏳ [INICIO PROCESO EN SEGUNDO PLANO - Hilo: " + Thread.currentThread().getName() + "]");
    // Simulamos que el servicio de correos tarda 3 segundos:
    Thread.sleep(3000);
    System.out.println(
        "📧 [CORREO ENVIADO] Bienvenido al curso: "
            + event.title()
            + " (ID: "
            + event.courseId()
            + ")");
  }
}
