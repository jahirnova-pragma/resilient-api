package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Branch;
import com.example.resilient_api.domain.model.gateways.BranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AddProductToBranchUseCaseTest {

    private static final String BRANCH_ID = "SUC-001";
    private static final String PRODUCT_ID = "PROD-001";
    private static final String NON_EXISTENT_BRANCH_ID = "SUC-999";
    private static final String ERROR_MESSAGE = "Sucursal no encontrada con id: " + NON_EXISTENT_BRANCH_ID;

    private BranchRepository branchRepository;
    private AddProductToBranchUseCase useCase;

    @BeforeEach
    void setUp() {
        branchRepository = mock(BranchRepository.class);
        useCase = new AddProductToBranchUseCase(branchRepository);
    }

    @Test
    void shouldAddNewProductToBranch() {
        Branch existingBranch = buildBranch(BRANCH_ID, new ArrayList<>());
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Mono.just(existingBranch));
        when(branchRepository.save(any(Branch.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

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
        Branch existingBranch = buildBranch(BRANCH_ID, products);
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Mono.just(existingBranch));
        when(branchRepository.save(any(Branch.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.execute(BRANCH_ID, PRODUCT_ID))
                .assertNext(savedBranch -> {
                    assertThat(savedBranch.getProductos()).hasSize(1).containsExactly(PRODUCT_ID);
                })
                .verifyComplete();

        verifySaveContainsProduct(PRODUCT_ID);
    }

    @Test
    void shouldThrowErrorWhenBranchDoesNotExist() {
        when(branchRepository.findById(NON_EXISTENT_BRANCH_ID)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(NON_EXISTENT_BRANCH_ID, PRODUCT_ID))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException
                        && error.getMessage().equals(ERROR_MESSAGE))
                .verify();
    }

    private Branch buildBranch(String id, List<String> products) {
        Branch branch = new Branch();
        branch.setId(id);
        branch.setProductos(products);
        return branch;
    }

    private void verifySaveContainsProduct(String productId) {
        ArgumentCaptor<Branch> captor = ArgumentCaptor.forClass(Branch.class);
        verify(branchRepository).save(captor.capture());
        assertThat(captor.getValue().getProductos()).contains(productId);
    }
}
