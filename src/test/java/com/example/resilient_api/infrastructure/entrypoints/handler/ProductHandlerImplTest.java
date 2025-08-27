package com.example.resilient_api.infrastructure.entrypoints.handler;

import com.example.resilient_api.domain.model.Product;
import com.example.resilient_api.domain.usecase.UpdateProductNameUseCase;
import com.example.resilient_api.domain.usecase.UpdateProductStockUseCase;
import com.example.resilient_api.infrastructure.entrypoints.dto.UpdateProductNameRequest;
import com.example.resilient_api.infrastructure.entrypoints.util.APIResponse;
import com.example.resilient_api.infrastructure.entrypoints.util.ErrorDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProductHandlerImplTest {

    private static final String PRODUCT_ID = "PROD-123";
    private static final int NEW_STOCK = 50;
    private static final String NEW_NAME = "Updated Product";
    private static final String EMPTY_NAME = "   ";

    @Mock
    private UpdateProductStockUseCase updateProductStockUseCase;

    @Mock
    private UpdateProductNameUseCase updateProductNameUseCase;

    @InjectMocks
    private ProductHandlerImpl productHandler;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToRouterFunction(
                RouterFunctions.route()
                        .PUT("/products/{productId}/stock/{stock}", productHandler::updateStock)
                        .PATCH("/products/{id}", productHandler::updateProductName)
                        .build()
        ).build();
    }

    @Test
    void shouldUpdateStockSuccessfully() {
        Product product = buildProduct(PRODUCT_ID, NEW_NAME, NEW_STOCK);

        when(updateProductStockUseCase.execute(PRODUCT_ID, NEW_STOCK)).thenReturn(Mono.just(product));

        webTestClient.put()
                .uri("/products/{productId}/stock/{stock}", PRODUCT_ID, NEW_STOCK)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<APIResponse<Product>>() {})
                .value(apiResponse -> {
                    assert apiResponse.getCode().equals("200");
                    assert apiResponse.getIdentifier().equals(PRODUCT_ID);
                    assert apiResponse.getData().getStock() == NEW_STOCK;
                });
    }

    @Test
    void shouldReturnBadRequestWhenUpdateStockFails() {
        when(updateProductStockUseCase.execute(PRODUCT_ID, NEW_STOCK))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid stock value")));

        webTestClient.put()
                .uri("/products/{productId}/stock/{stock}", PRODUCT_ID, NEW_STOCK)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<APIResponse<Void>>() {})
                .value(apiResponse -> {
                    assert apiResponse.getCode().equals("400");
                    assert apiResponse.getMessage().equals("Invalid stock value");
                });
    }

    @Test
    void shouldUpdateProductNameSuccessfully() {
        Product product = buildProduct(PRODUCT_ID, NEW_NAME, NEW_STOCK);

        when(updateProductNameUseCase.execute(PRODUCT_ID, NEW_NAME)).thenReturn(Mono.just(product));

        webTestClient.patch()
                .uri("/products/{id}", PRODUCT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new UpdateProductNameRequest(NEW_NAME)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<APIResponse<Product>>() {})
                .value(apiResponse -> {
                    assert apiResponse.getCode().equals("200");
                    assert apiResponse.getIdentifier().equals(PRODUCT_ID);
                    assert apiResponse.getData().getName().equals(NEW_NAME);
                });
    }

    @Test
    void shouldReturnBadRequestWhenNameIsBlank() {
        webTestClient.patch()
                .uri("/products/{id}", PRODUCT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new UpdateProductNameRequest(EMPTY_NAME)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("400")
                .jsonPath("$.message").exists();
    }

    @Test
    void shouldReturnInternalServerErrorWhenUpdateNameFails() {
        when(updateProductNameUseCase.execute(PRODUCT_ID, NEW_NAME))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        webTestClient.patch()
                .uri("/products/{id}", PRODUCT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new UpdateProductNameRequest(NEW_NAME)))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo("500")
                .jsonPath("$.data.code").isEqualTo("UPDATE_PRODUCT_NAME_ERROR")
                .jsonPath("$.data.message").isEqualTo("DB error");
    }

    private Product buildProduct(String id, String name, int stock) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setStock(stock);
        return product;
    }
}
