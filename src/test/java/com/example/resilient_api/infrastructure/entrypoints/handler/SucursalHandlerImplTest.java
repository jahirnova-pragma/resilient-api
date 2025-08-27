package com.example.resilient_api.infrastructure.entrypoints.handler;

import com.example.resilient_api.domain.model.Sucursal;
import com.example.resilient_api.domain.usecase.AddProductoToSucursalUseCase;
import com.example.resilient_api.domain.usecase.RemoveProductFromSucursalUseCase;
import com.example.resilient_api.domain.usecase.UpdateSucursalNameUseCase;
import com.example.resilient_api.infrastructure.entrypoints.dto.UpdateSucursalNameRequest;
import com.example.resilient_api.infrastructure.entrypoints.util.APIResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;

public class SucursalHandlerImplTest {

    private static final String SUCURSAL_ID = "SUC-001";
    private static final String PRODUCT_ID = "PROD-001";
    private static final String NEW_NAME = "Sucursal Central";
    private static final String EMPTY_NAME = "   ";

    @Mock
    private AddProductoToSucursalUseCase addProductoToSucursalUseCase;

    @Mock
    private RemoveProductFromSucursalUseCase removeProductFromSucursalUseCase;

    @Mock
    private UpdateSucursalNameUseCase updateSucursalNameUseCase;

    @InjectMocks
    private SucursalHandlerImpl sucursalHandler;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToRouterFunction(
                RouterFunctions.route()
                        .POST("/sucursales/{sucursalId}/productos/{productoId}", sucursalHandler::addProducto)
                        .DELETE("/sucursales/{sucursalId}/productos/{productId}", sucursalHandler::removeProduct)
                        .PATCH("/sucursales/{id}", sucursalHandler::updateSucursalName)
                        .build()
        ).build();
    }

    private Sucursal buildSucursal(String id, String name) {
        return new Sucursal(id, name, List.of(PRODUCT_ID));
    }

    @Test
    void shouldAddProductoSuccessfully() {
        Sucursal sucursal = buildSucursal(SUCURSAL_ID, NEW_NAME);

        when(addProductoToSucursalUseCase.execute(SUCURSAL_ID, PRODUCT_ID)).thenReturn(Mono.just(sucursal));

        webTestClient.post()
                .uri("/sucursales/{sucursalId}/productos/{productoId}", SUCURSAL_ID, PRODUCT_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo("200")
                .jsonPath("$.identifier").isEqualTo(SUCURSAL_ID);
    }

    @Test
    void shouldReturnInternalServerErrorWhenAddProductoFails() {
        when(addProductoToSucursalUseCase.execute(SUCURSAL_ID, PRODUCT_ID))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        webTestClient.post()
                .uri("/sucursales/{sucursalId}/productos/{productoId}", SUCURSAL_ID, PRODUCT_ID)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo("500")
                .jsonPath("$.message").isEqualTo("DB error");
    }

    @Test
    void shouldRemoveProductoSuccessfully() {
        Sucursal sucursal = buildSucursal(SUCURSAL_ID, NEW_NAME);

        when(removeProductFromSucursalUseCase.execute(SUCURSAL_ID, PRODUCT_ID)).thenReturn(Mono.just(sucursal));

        webTestClient.delete()
                .uri("/sucursales/{sucursalId}/productos/{productId}", SUCURSAL_ID, PRODUCT_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(SUCURSAL_ID);
    }

    @Test
    void shouldReturnBadRequestWhenRemoveProductoFails() {
        when(removeProductFromSucursalUseCase.execute(SUCURSAL_ID, PRODUCT_ID))
                .thenReturn(Mono.error(new IllegalArgumentException("Product not found")));

        webTestClient.delete()
                .uri("/sucursales/{sucursalId}/productos/{productId}", SUCURSAL_ID, PRODUCT_ID)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("400")
                .jsonPath("$.message").isEqualTo("Product not found");
    }

    @Test
    void shouldUpdateSucursalNameSuccessfully() {
        Sucursal sucursal = buildSucursal(SUCURSAL_ID, NEW_NAME);

        when(updateSucursalNameUseCase.execute(SUCURSAL_ID, NEW_NAME)).thenReturn(Mono.just(sucursal));

        webTestClient.patch()
                .uri("/sucursales/{id}", SUCURSAL_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new UpdateSucursalNameRequest(NEW_NAME)))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo("200")
                .jsonPath("$.identifier").isEqualTo(SUCURSAL_ID);
    }

    @Test
    void shouldReturnBadRequestWhenNameIsBlank() {
        webTestClient.patch()
                .uri("/sucursales/{id}", SUCURSAL_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new UpdateSucursalNameRequest(EMPTY_NAME)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("400");
    }

    @Test
    void shouldReturnInternalServerErrorWhenUpdateNameFails() {
        when(updateSucursalNameUseCase.execute(SUCURSAL_ID, NEW_NAME))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        webTestClient.patch()
                .uri("/sucursales/{id}", SUCURSAL_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new UpdateSucursalNameRequest(NEW_NAME)))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo("500")
                .jsonPath("$.message").isEqualTo("Internal server error updating sucursal name");
    }
}
