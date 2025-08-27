package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Sucursal;
import com.example.resilient_api.domain.model.gateways.SucursalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RemoveProductFromSucursalUseCaseTest {

    private static final String BRANCH_ID = "SUC-01";
    private static final String PRODUCT_ID = "PROD-01";
    private static final String NOT_FOUND_ID = "SUC-404";

    private SucursalRepository sucursalRepository;
    private RemoveProductFromSucursalUseCase useCase;

    @BeforeEach
    void setUp() {
        sucursalRepository = Mockito.mock(SucursalRepository.class);
        useCase = new RemoveProductFromSucursalUseCase(sucursalRepository);
    }

    @Test
    void shouldRemoveProductFromSucursal() {
        Sucursal sucursal = buildSucursalWithProducts(List.of(PRODUCT_ID));
        when(sucursalRepository.findById(BRANCH_ID)).thenReturn(Mono.just(sucursal));
        when(sucursalRepository.save(any(Sucursal.class))).thenReturn(Mono.just(sucursal));

        StepVerifier.create(useCase.execute(BRANCH_ID, PRODUCT_ID))
                .expectNextMatches(s -> !s.getProductos().contains(PRODUCT_ID))
                .verifyComplete();
    }

    @Test
    void shouldReturnSucursalWhenProductNotFound() {
        Sucursal sucursal = buildSucursalWithProducts(List.of("OTHER-PROD"));
        when(sucursalRepository.findById(BRANCH_ID)).thenReturn(Mono.just(sucursal));

        StepVerifier.create(useCase.execute(BRANCH_ID, PRODUCT_ID))
                .expectNextMatches(s -> s.getProductos().contains("OTHER-PROD"))
                .verifyComplete();
    }

    @Test
    void shouldThrowWhenSucursalNotFound() {
        when(sucursalRepository.findById(NOT_FOUND_ID)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(NOT_FOUND_ID, PRODUCT_ID))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    private Sucursal buildSucursalWithProducts(List<String> products) {
        return new Sucursal(BRANCH_ID, "Branch Name", new ArrayList<>(products));
    }
}
