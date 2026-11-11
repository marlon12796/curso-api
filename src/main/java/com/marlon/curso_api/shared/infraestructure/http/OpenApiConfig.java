package com.marlon.curso_api.shared.infraestructure.http;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info =
        @Info(
            title = "Curso API",
            version = "v1",
            description = "API de cursos con autenticacion JWT y despliegue automatizado"),
    servers = {@Server(url = "https://java.marloncloud.com", description = "Produccion")})
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer")
public class OpenApiConfig {}
