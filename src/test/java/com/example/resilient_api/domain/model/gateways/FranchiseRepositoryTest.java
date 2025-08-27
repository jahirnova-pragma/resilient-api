package com.example.resilient_api.domain.model.gateways;

import com.example.resilient_api.domain.model.Branch;
import com.example.resilient_api.domain.model.Franchise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class FranchiseRepositoryTest {

    private static final String ID = "FR-01";
    private static final String NAME = "Franchise 1";
    private static final String BRANCH_ID = "SUC-01";
    private static final String BRANCH_NAME = "Sucursal 1";
    private static final List<String> PRODUCTOS = List.of("PROD-01", "PROD-02");

    private FranchiseRepository repository;

    @BeforeEach
    void init() {
        repository = Mockito.mock(FranchiseRepository.class);
    }

    @Test
    void shouldSaveFranchise() {
        Franchise franchise = buildFranchise();
        when(repository.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(repository.save(franchise))
                .expectNextMatches(saved -> saved.getId().equals(ID) && saved.getNombre().equals(NAME))
                .verifyComplete();
    }

    @Test
    void shouldFindFranchiseById() {
        Franchise franchise = buildFranchise();
        when(repository.findById(eq(ID))).thenReturn(Mono.just(franchise));

        StepVerifier.create(repository.findById(ID))
                .expectNextMatches(found -> found.getId().equals(ID) && found.getNombre().equals(NAME))
                .verifyComplete();
    }

    private Franchise buildFranchise() {
        return Franchise.builder()
                .id(ID)
                .nombre(NAME)
                .branchs(List.of(String.valueOf(buildBranch())))
                .build();
    }

    private Branch buildBranch() {
        return Branch.builder()
                .id(BRANCH_ID)
                .nombre(BRANCH_NAME)
                .productos(PRODUCTOS)
                .build();
    }
}
