package com.ecom.product_service.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productServiceOpenAPI() {

        Info info = new Info()
                .title("Product Service API")
                .version("1.0.0")
                .description("RESTful API for E-commerce Product Management Service with JWT Authentication");

        // Define JWT security scheme
        SecurityScheme securityScheme = new SecurityScheme()
                .name("bearer-jwt")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter JWT token obtained from authentication service");

        return new OpenAPI()
                .info(info)
                .servers(List.of())
                .components(new Components().addSecuritySchemes("bearer-jwt", securityScheme));
    }

    
}
