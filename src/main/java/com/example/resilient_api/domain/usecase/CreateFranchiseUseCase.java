package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.util.UUID;
import com.example.resilient_api.domain.model.Franchise;

@RequiredArgsConstructor
public class CreateFranchiseUseCase {

    private final FranchiseRepository  franchiseRepository;

    public Mono<Franchise> execute(Franchise franchise) {
        Franchise newFranchise = franchise.toBuilder()
                .nombre(franchise.getNombre())
                .id(UUID.randomUUID().toString())
                .build();

        return franchiseRepository.save(newFranchise);
    }
}
