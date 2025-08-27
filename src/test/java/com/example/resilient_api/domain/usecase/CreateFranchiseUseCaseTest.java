package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Franchise;
import com.example.resilient_api.domain.model.gateways.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CreateFranchiseUseCaseTest {

    private static final String FRANCHISE_NAME = "Test Franchise";

    private FranchiseRepository franchiseRepository;
    private CreateFranchiseUseCase useCase;

    @BeforeEach
    void setUp() {
        franchiseRepository = mock(FranchiseRepository.class);
        useCase = new CreateFranchiseUseCase(franchiseRepository);
    }

    @Test
    void shouldCreateFranchiseWithGeneratedId() {
        Franchise franchise = buildFranchiseWithoutId();
        when(franchiseRepository.save(any(Franchise.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<Franchise> result = useCase.execute(franchise);

        StepVerifier.create(result)
                .assertNext(savedFranchise -> {
                    assertThat(savedFranchise.getId()).isNotNull();
                    assertThat(savedFranchise.getNombre()).isEqualTo(FRANCHISE_NAME);
                })
                .verifyComplete();

        ArgumentCaptor<Franchise> captor = ArgumentCaptor.forClass(Franchise.class);
        verify(franchiseRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isNotNull();
        assertThat(captor.getValue().getNombre()).isEqualTo(FRANCHISE_NAME);
    }

    private Franchise buildFranchiseWithoutId() {
        return Franchise.builder()
                .nombre(FRANCHISE_NAME)
                .build();
    }
}
