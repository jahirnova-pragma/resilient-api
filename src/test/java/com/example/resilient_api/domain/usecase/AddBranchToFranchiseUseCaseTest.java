package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Franchise;
import com.example.resilient_api.domain.model.gateways.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AddBranchToFranchiseUseCaseTest {

    private static final String FRANCHISE_ID = "FRANCHISE-001";
    private static final String BRANCH_ID = "SUC-001";
    private static final String ERROR_MESSAGE = "Franquicia no encontrada con id: " + FRANCHISE_ID;

    private FranchiseRepository franchiseRepository;
    private AddBranchToFranchiseUseCase useCase;

    @BeforeEach
    void setUp() {
        franchiseRepository = mock(FranchiseRepository.class);
        useCase = new AddBranchToFranchiseUseCase(franchiseRepository);
    }

    @Test
    void shouldAddBranchWhenNotPresent() {
        Franchise franchise = buildFranchise();
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(franchise));
        when(franchiseRepository.save(any(Franchise.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<Franchise> result = useCase.execute(FRANCHISE_ID, BRANCH_ID);

        StepVerifier.create(result)
                .assertNext(updatedFranchise -> {
                    assertThat(updatedFranchise.getBranchs()).contains(BRANCH_ID);
                })
                .verifyComplete();

        ArgumentCaptor<Franchise> captor = ArgumentCaptor.forClass(Franchise.class);
        verify(franchiseRepository).save(captor.capture());
        assertThat(captor.getValue().getBranchs()).contains(BRANCH_ID);
    }

    @Test
    void shouldNotDuplicateBranch() {
        Franchise franchise = buildFranchiseWithBranch();
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(franchise));
        when(franchiseRepository.save(any(Franchise.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<Franchise> result = useCase.execute(FRANCHISE_ID, BRANCH_ID);

        StepVerifier.create(result)
                .assertNext(updatedFranchise -> {
                    assertThat(updatedFranchise.getBranchs()).hasSize(1).containsExactly(BRANCH_ID);
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenFranchiseNotFound() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.empty());

        Mono<Franchise> result = useCase.execute(FRANCHISE_ID, BRANCH_ID);

        StepVerifier.create(result)
                .expectErrorSatisfies(error -> assertThat(error)
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage(ERROR_MESSAGE))
                .verify();
    }

    private Franchise buildFranchise() {
        return Franchise.builder()
                .id(FRANCHISE_ID)
                .nombre("Test Franchise")
                .branchs(Collections.emptyList())
                .build();
    }

    private Franchise buildFranchiseWithBranch() {
        return Franchise.builder()
                .id(FRANCHISE_ID)
                .nombre("Test Franchise")
                .branchs(Collections.singletonList(BRANCH_ID))
                .build();
    }
}
