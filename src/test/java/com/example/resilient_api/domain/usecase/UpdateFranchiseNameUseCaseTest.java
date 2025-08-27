package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Franchise;
import com.example.resilient_api.domain.model.gateways.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

class UpdateFranchiseNameUseCaseTest {

    private static final String FRANCHISE_ID = "FRAN-001";
    private static final String OLD_NAME = "Old Franchise";
    private static final String NEW_NAME = "New Franchise";
    private static final String ERROR_MESSAGE = "Franchise not found";

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldUpdateFranchiseNameSuccessfully() {
        Franchise franchise = buildFranchise(OLD_NAME);
        Franchise updatedFranchise = buildFranchise(NEW_NAME);

        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(franchise));
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(updatedFranchise));

        StepVerifier.create(updateFranchiseNameUseCase.execute(FRANCHISE_ID, NEW_NAME))
                .expectNextMatches(result -> NEW_NAME.equals(result.getNombre()))
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenFranchiseNotFound() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.empty());

        StepVerifier.create(updateFranchiseNameUseCase.execute(FRANCHISE_ID, NEW_NAME))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException
                        && ERROR_MESSAGE.equals(error.getMessage()))
                .verify();
    }

    private Franchise buildFranchise(String name) {
        Franchise franchise = new Franchise();
        franchise.setId(FRANCHISE_ID);
        franchise.setNombre(name);
        return franchise;
    }
}
