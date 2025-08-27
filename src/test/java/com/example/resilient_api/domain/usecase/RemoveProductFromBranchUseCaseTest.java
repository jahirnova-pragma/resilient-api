package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Branch;
import com.example.resilient_api.domain.model.gateways.BranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RemoveProductFromBranchUseCaseTest {

    private static final String BRANCH_ID = "SUC-01";
    private static final String PRODUCT_ID = "PROD-01";
    private static final String NOT_FOUND_ID = "SUC-404";

    private BranchRepository branchRepository;
    private RemoveProductFromBranchUseCase useCase;

    @BeforeEach
    void setUp() {
        branchRepository = Mockito.mock(BranchRepository.class);
        useCase = new RemoveProductFromBranchUseCase(branchRepository);
    }

    @Test
    void shouldRemoveProductFromBranch() {
        Branch branch = buildBranchWithProducts(List.of(PRODUCT_ID));
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Mono.just(branch));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(branch));

        StepVerifier.create(useCase.execute(BRANCH_ID, PRODUCT_ID))
                .expectNextMatches(s -> !s.getProductos().contains(PRODUCT_ID))
                .verifyComplete();
    }

    @Test
    void shouldReturnBranchWhenProductNotFound() {
        Branch branch = buildBranchWithProducts(List.of("OTHER-PROD"));
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Mono.just(branch));

        StepVerifier.create(useCase.execute(BRANCH_ID, PRODUCT_ID))
                .expectNextMatches(s -> s.getProductos().contains("OTHER-PROD"))
                .verifyComplete();
    }

    @Test
    void shouldThrowWhenBranchNotFound() {
        when(branchRepository.findById(NOT_FOUND_ID)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(NOT_FOUND_ID, PRODUCT_ID))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    private Branch buildBranchWithProducts(List<String> products) {
        return new Branch(BRANCH_ID, "Branch Name", new ArrayList<>(products));
    }
}
