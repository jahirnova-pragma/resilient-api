package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Sucursal;
import com.example.resilient_api.domain.model.gateways.SucursalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AddProductoToSucursalUseCaseTest {

    private static final String BRANCH_ID = "SUC-001";
    private static final String PRODUCT_ID = "PROD-001";
    private static final String NON_EXISTENT_BRANCH_ID = "SUC-999";
    private static final String ERROR_MESSAGE = "Sucursal no encontrada con id: " + NON_EXISTENT_BRANCH_ID;

    private SucursalRepository sucursalRepository;
    private AddProductoToSucursalUseCase useCase;

    @BeforeEach
    void setUp() {
        sucursalRepository = mock(SucursalRepository.class);
        useCase = new AddProductoToSucursalUseCase(sucursalRepository);
    }

    @Test
    void shouldAddNewProductToBranch() {
        Sucursal existingBranch = buildSucursal(BRANCH_ID, new ArrayList<>());
        when(sucursalRepository.findById(BRANCH_ID)).thenReturn(Mono.just(existingBranch));
        when(sucursalRepository.save(any(Sucursal.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.execute(BRANCH_ID, PRODUCT_ID))
                .assertNext(savedBranch -> {
                    assertThat(savedBranch.getProductos()).contains(PRODUCT_ID);
                })
                .verifyComplete();

        verifySaveContainsProduct(PRODUCT_ID);
    }

    @Test
    void shouldNotDuplicateProductIfAlreadyExists() {
        List<String> products = new ArrayList<>(List.of(PRODUCT_ID));
        Sucursal existingBranch = buildSucursal(BRANCH_ID, products);
        when(sucursalRepository.findById(BRANCH_ID)).thenReturn(Mono.just(existingBranch));
        when(sucursalRepository.save(any(Sucursal.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.execute(BRANCH_ID, PRODUCT_ID))
                .assertNext(savedBranch -> {
                    assertThat(savedBranch.getProductos()).hasSize(1).containsExactly(PRODUCT_ID);
                })
                .verifyComplete();

        verifySaveContainsProduct(PRODUCT_ID);
    }

    @Test
    void shouldThrowErrorWhenBranchDoesNotExist() {
        when(sucursalRepository.findById(NON_EXISTENT_BRANCH_ID)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(NON_EXISTENT_BRANCH_ID, PRODUCT_ID))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException
                        && error.getMessage().equals(ERROR_MESSAGE))
                .verify();
    }

    private Sucursal buildSucursal(String id, List<String> products) {
        Sucursal sucursal = new Sucursal();
        sucursal.setId(id);
        sucursal.setProductos(products);
        return sucursal;
    }

    private void verifySaveContainsProduct(String productId) {
        ArgumentCaptor<Sucursal> captor = ArgumentCaptor.forClass(Sucursal.class);
        verify(sucursalRepository).save(captor.capture());
        assertThat(captor.getValue().getProductos()).contains(productId);
    }
}
