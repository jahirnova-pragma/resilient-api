package com.example.resilient_api.domain.model.gateways;

import com.example.resilient_api.domain.model.Branch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

class BranchRepositoryTest {

    private static final String BRANCH_ID = "SUC-001";
    private static final String BRANCH_NOMBRE = "Sucursal Central";
    private static final List<String> PRODUCTOS = List.of("PROD-001", "PROD-002");

    private BranchRepository branchRepository;

    @BeforeEach
    void setUp() {
        branchRepository = new InMemoryBranchRepository();
    }

    @Test
    void shouldSaveBranch() {
        StepVerifier.create(branchRepository.save(buildBranch()))
                .expectNextMatches(branch -> branch.getId().equals(BRANCH_ID))
                .verifyComplete();
    }

    @Test
    void shouldFindBranchById() {
        branchRepository.save(buildBranch()).block();

        StepVerifier.create(branchRepository.findById(BRANCH_ID))
                .expectNextMatches(branch -> branch.getNombre().equals(BRANCH_NOMBRE))
                .verifyComplete();
    }

    @Test
    void shouldFindBranchByIds() {
        branchRepository.save(buildBranch()).block();

        StepVerifier.create(branchRepository.findByIds(List.of(BRANCH_ID)))
                .expectNextMatches(branch -> branch.getProductos().equals(PRODUCTOS))
                .verifyComplete();
    }

    private Branch buildBranch() {
        return Branch.builder()
                .id(BRANCH_ID)
                .nombre(BRANCH_NOMBRE)
                .productos(PRODUCTOS)
                .build();
    }

    private static class InMemoryBranchRepository implements BranchRepository {
        private final java.util.Map<String, Branch> database = new java.util.HashMap<>();

        @Override
        public Mono<Branch> findById(String id) {
            return Mono.justOrEmpty(database.get(id));
        }

        @Override
        public Mono<Branch> save(Branch branch) {
            database.put(branch.getId(), branch);
            return Mono.just(branch);
        }

        @Override
        public Flux<Branch> findByIds(List<String> branchIds) {
            return Flux.fromIterable(branchIds)
                    .map(database::get)
                    .filter(branch -> branch != null);
        }
    }
}
