package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Branch;
import com.example.resilient_api.domain.model.gateways.BranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class UpdateBranchNameUseCaseTest {

    private static final String BRANCH_ID = "SUC-123";
    private static final String BRANCH_NAME = "Old Name";
    private static final String UPDATED_NAME = "New Name";
    private static final String ERROR_MESSAGE = "Sucursal not found";

    private BranchRepository branchRepository;
    private UpdateBranchNameUseCase useCase;

    @BeforeEach
    void setUp() {
        branchRepository = Mockito.mock(BranchRepository.class);
        useCase = new UpdateBranchNameUseCase(branchRepository);
    }

    @Test
    void shouldUpdateBranchNameWhenExists() {
        Branch branch = buildBranch(BRANCH_ID, BRANCH_NAME);
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Mono.just(branch));
        when(branchRepository.save(any(Branch.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.execute(BRANCH_ID, UPDATED_NAME))
                .expectNextMatches(result -> result.getId().equals(BRANCH_ID) && result.getNombre().equals(UPDATED_NAME))
                .verifyComplete();

        verify(branchRepository).findById(BRANCH_ID);
        verify(branchRepository).save(any(Branch.class));
    }

    @Test
    void shouldReturnErrorWhenBranchNotFound() {
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(BRANCH_ID, UPDATED_NAME))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException
                        && throwable.getMessage().equals(ERROR_MESSAGE))
                .verify();

        verify(branchRepository).findById(BRANCH_ID);
        verify(branchRepository, never()).save(any(Branch.class));
    }

    private Branch buildBranch(String id, String name) {
        Branch branch = new Branch();
        branch.setId(id);
        branch.setNombre(name);
        return branch;
    }
}
