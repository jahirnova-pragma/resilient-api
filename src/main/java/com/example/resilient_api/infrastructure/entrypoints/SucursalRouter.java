package com.example.resilient_api.infrastructure.entrypoints;

import com.example.resilient_api.infrastructure.entrypoints.handler.SucursalHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class SucursalRouter {

    @Bean
    public RouterFunction<?> sucursalRoutes(SucursalHandlerImpl handler) {
        return route(POST("/sucursales/{sucursalId}/productos/{productoId}")
                        .and(accept(MediaType.APPLICATION_JSON)),
                handler::addProducto)
                .andRoute(DELETE("/sucursales/{sucursalId}/productos/{productId}")
                        .and(accept(MediaType.APPLICATION_JSON)),
                handler::removeProduct);
    }
}
