package com.example.resilient_api.infrastructure.entrypoints;

import com.example.resilient_api.infrastructure.entrypoints.handler.FranchiseHandlerImpl;
import com.example.resilient_api.infrastructure.entrypoints.handler.ProductHandlerImpl;
import com.example.resilient_api.infrastructure.entrypoints.handler.SucursalHandlerImpl;
import com.example.resilient_api.infrastructure.entrypoints.handler.SucursalHandlerImplTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ApiRouterTest {

    private static final String BASE_FRANCHISE = "/franchises";
    private static final String BASE_PRODUCTS = "/products";
    private static final String BASE_BRANCHES = "/sucursales";
    private static final String FRANCHISE_ID = "FRA-123";
    private static final String PRODUCT_ID = "PRO-456";
    private static final String BRANCH_ID = "SUC-789";
    private static final String STOCK_VALUE = "20";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FranchiseHandlerImpl franchiseHandler;

    @MockBean
    private ProductHandlerImpl productHandler;

    @MockBean
    private SucursalHandlerImpl sucursalHandler;

    @BeforeEach
    void setup() {
        mockAllHandlers();
    }

    @Test
    void shouldRouteToCreateFranchise() {
        post(BASE_FRANCHISE).expectStatus().isOk();
    }

    @Test
    void shouldRouteToAddSucursalToFranchise() {
        post(BASE_FRANCHISE + "/" + FRANCHISE_ID + "/sucursales").expectStatus().isOk();
    }

    @Test
    void shouldRouteToGetMaxStockPerBranch() {
        get(BASE_FRANCHISE + "/" + FRANCHISE_ID + "/max-stock").expectStatus().isOk();
    }

    @Test
    void shouldRouteToUpdateFranchiseName() {
        patch(BASE_FRANCHISE + "/" + FRANCHISE_ID).expectStatus().isOk();
    }

    @Test
    void shouldRouteToUpdateProductStock() {
        put(BASE_PRODUCTS + "/" + PRODUCT_ID + "/stock/" + STOCK_VALUE).expectStatus().isOk();
    }

    @Test
    void shouldRouteToUpdateProductName() {
        patch(BASE_PRODUCTS + "/" + PRODUCT_ID).expectStatus().isOk();
    }

    @Test
    void shouldRouteToAddProductToBranch() {
        post(BASE_BRANCHES + "/" + BRANCH_ID + "/productos/" + PRODUCT_ID).expectStatus().isOk();
    }

    @Test
    void shouldRouteToRemoveProductFromBranch() {
        delete(BASE_BRANCHES + "/" + BRANCH_ID + "/productos/" + PRODUCT_ID).expectStatus().isOk();
    }

    @Test
    void shouldRouteToUpdateBranchName() {
        patch(BASE_BRANCHES + "/" + BRANCH_ID).expectStatus().isOk();
    }

    private void mockAllHandlers() {
        when(franchiseHandler.createFranchise(any())).thenReturn(Mono.just(ServerResponse.ok().build().block()));
        when(franchiseHandler.addSucursalToFranchise(any())).thenReturn(Mono.just(ServerResponse.ok().build().block()));
        when(franchiseHandler.getMaxStockPerBranch(any())).thenReturn(Mono.just(ServerResponse.ok().build().block()));
        when(franchiseHandler.updateFranchiseName(any())).thenReturn(Mono.just(ServerResponse.ok().build().block()));

        when(productHandler.updateStock(any())).thenReturn(Mono.just(ServerResponse.ok().build().block()));
        when(productHandler.updateProductName(any())).thenReturn(Mono.just(ServerResponse.ok().build().block()));

        when(sucursalHandler.addProducto(any())).thenReturn(Mono.just(ServerResponse.ok().build().block()));
        when(sucursalHandler.removeProduct(any())).thenReturn(Mono.just(ServerResponse.ok().build().block()));
        when(sucursalHandler.updateSucursalName(any())).thenReturn(Mono.just(ServerResponse.ok().build().block()));
    }

    private WebTestClient.ResponseSpec get(String uri) {
        return webTestClient.get().uri(uri).accept(MediaType.APPLICATION_JSON).exchange();
    }

    private WebTestClient.ResponseSpec post(String uri) {
        return webTestClient.post().uri(uri).contentType(MediaType.APPLICATION_JSON).exchange();
    }

    private WebTestClient.ResponseSpec put(String uri) {
        return webTestClient.put().uri(uri).contentType(MediaType.APPLICATION_JSON).exchange();
    }

    private WebTestClient.ResponseSpec patch(String uri) {
        return webTestClient.patch().uri(uri).contentType(MediaType.APPLICATION_JSON).exchange();
    }

    private WebTestClient.ResponseSpec delete(String uri) {
        return webTestClient.delete().uri(uri).accept(MediaType.APPLICATION_JSON).exchange();
    }
}
