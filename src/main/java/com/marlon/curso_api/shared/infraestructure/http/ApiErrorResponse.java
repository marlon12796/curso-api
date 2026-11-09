package com.marlon.curso_api.shared.infraestructure.http;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(
    LocalDateTime timestamp, int status, String error, Map<String, String> errors) {}
