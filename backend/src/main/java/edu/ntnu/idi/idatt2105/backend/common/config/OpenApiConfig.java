package edu.ntnu.idi.idatt2105.backend.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for the Regula backend.
 * <p>
 * Defines global API metadata and the HTTP bearer security scheme used for
 * JWT-based authentication in the generated documentation.
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Regula API",
        version = "v1",
        description = "API documentation for the Regula backend"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}

