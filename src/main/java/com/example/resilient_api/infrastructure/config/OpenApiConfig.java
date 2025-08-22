package com.example.resilient_api.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Resilient API")
                        .version("1.0")
                        .description("API para franquicias, sucursales y productos")
                        .version("1.0.0")
                        .contact(new Contact().name("Andres Nova").email("andres.nova@example.com"))

                );
    }
}
