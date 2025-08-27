package com.example.resilient_api.infrastructure.entrypoints.handler;

import com.example.resilient_api.domain.model.Branch;
import com.example.resilient_api.domain.usecase.AddProductToBranchUseCase;
import com.example.resilient_api.domain.usecase.RemoveProductFromBranchUseCase;
import com.example.resilient_api.domain.usecase.UpdateBranchNameUseCase;
import com.example.resilient_api.infrastructure.entrypoints.dto.UpdateBranchNameRequest;
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

public class BranchHandlerImplTest {

    private static final String BRANCH_ID = "SUC-001";
    private static final String PRODUCT_ID = "PROD-001";
    private static final String NEW_NAME = "Sucursal Central";
    private static final String EMPTY_NAME = "   ";

    @Mock
    private AddProductToBranchUseCase addProductToBranchUseCase;

    @Mock
    private RemoveProductFromBranchUseCase removeProductFromBranchUseCase;

    @Mock
    private UpdateBranchNameUseCase updateBranchNameUseCase;

    @InjectMocks
    private BranchHandlerImpl branchHandler;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToRouterFunction(
                RouterFunctions.route()
                        .POST("/branchs/{branchId}/products/{productoId}", branchHandler::addProducto)
                        .DELETE("/branchs/{branchId}/products/{productId}", branchHandler::removeProduct)
                        .PATCH("/branchs/{id}", branchHandler::updateBranchName)
                        .build()
        ).build();
    }

    private Branch buildBranch(String id, String name) {
        return new Branch(id, name, List.of(PRODUCT_ID));
    }

    @Test
    void shouldAddProductoSuccessfully() {
        Branch branch = buildBranch(BRANCH_ID, NEW_NAME);

        when(addProductToBranchUseCase.execute(BRANCH_ID, PRODUCT_ID)).thenReturn(Mono.just(branch));

        webTestClient.post()
                .uri("/branchs/{branchId}/products/{productoId}", BRANCH_ID, PRODUCT_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo("200")
                .jsonPath("$.identifier").isEqualTo(BRANCH_ID);
    }

    @Test
    void shouldReturnInternalServerErrorWhenAddProductoFails() {
        when(addProductToBranchUseCase.execute(BRANCH_ID, PRODUCT_ID))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        webTestClient.post()
                .uri("/branchs/{branchId}/products/{productoId}", BRANCH_ID, PRODUCT_ID)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo("500")
                .jsonPath("$.message").isEqualTo("DB error");
    }

    @Test
    void shouldRemoveProductoSuccessfully() {
        Branch branch = buildBranch(BRANCH_ID, NEW_NAME);

        when(removeProductFromBranchUseCase.execute(BRANCH_ID, PRODUCT_ID)).thenReturn(Mono.just(branch));

        webTestClient.delete()
                .uri("/branchs/{branchId}/products/{productId}", BRANCH_ID, PRODUCT_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(BRANCH_ID);
    }

    @Test
    void shouldReturnBadRequestWhenRemoveProductoFails() {
        when(removeProductFromBranchUseCase.execute(BRANCH_ID, PRODUCT_ID))
                .thenReturn(Mono.error(new IllegalArgumentException("Product not found")));

        webTestClient.delete()
                .uri("/branchs/{branchId}/products/{productId}", BRANCH_ID, PRODUCT_ID)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("400")
                .jsonPath("$.message").isEqualTo("Product not found");
    }

    @Test
    void shouldUpdateBranchNameSuccessfully() {
        Branch branch = buildBranch(BRANCH_ID, NEW_NAME);

        when(updateBranchNameUseCase.execute(BRANCH_ID, NEW_NAME)).thenReturn(Mono.just(branch));

        webTestClient.patch()
                .uri("/branchs/{id}", BRANCH_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new UpdateBranchNameRequest(NEW_NAME)))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo("200")
                .jsonPath("$.identifier").isEqualTo(BRANCH_ID);
    }

    @Test
    void shouldReturnBadRequestWhenNameIsBlank() {
        webTestClient.patch()
                .uri("/branchs/{id}", BRANCH_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new UpdateBranchNameRequest(EMPTY_NAME)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("400");
    }

    @Test
    void shouldReturnInternalServerErrorWhenUpdateNameFails() {
        when(updateBranchNameUseCase.execute(BRANCH_ID, NEW_NAME))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        webTestClient.patch()
                .uri("/branchs/{id}", BRANCH_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new UpdateBranchNameRequest(NEW_NAME)))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo("500")
                .jsonPath("$.message").isEqualTo("Internal server error updating sucursal name");
    }
}
