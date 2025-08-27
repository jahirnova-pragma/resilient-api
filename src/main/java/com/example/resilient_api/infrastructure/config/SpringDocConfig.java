package com.example.resilient_api.infrastructure.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public GroupedOpenApi franchiseApi() {
        return GroupedOpenApi.builder()
                .group("franchises")
                .pathsToMatch("/franchises/**")
                .build();
    }

    @Bean
    public GroupedOpenApi branchApi() {
        return GroupedOpenApi.builder()
                .group("branch")
                .pathsToMatch("/branchs/**")
                .build();
    }

    @Bean
    public GroupedOpenApi productApi() {
        return GroupedOpenApi.builder()
                .group("products")
                .pathsToMatch("/products/**")
                .build();
    }
}
