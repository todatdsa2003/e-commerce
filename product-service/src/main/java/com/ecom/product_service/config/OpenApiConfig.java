package com.ecom.product_service.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI productServiceOpenAPI() {

        Info info = new Info()
                .title("Product Service API")
                .version("1.0.0")
                .description("RESTful API for E-commerce Product Management Service");

        return new OpenAPI()
                .info(info).servers(List.of());
    }

    
}
