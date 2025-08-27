package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Franchise;
import com.example.resilient_api.domain.model.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RequiredArgsConstructor
public class AddBranchToFranchiseUseCase {

    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> execute(String franchiseId, String branchId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franquicia no encontrada con id: " + franchiseId)))
                .flatMap(franchise -> {
                    if (franchise.getBranchs() == null) {
                        franchise.setBranchs(new java.util.ArrayList<>());
                    } else {
                        franchise.setBranchs(new ArrayList<>(franchise.getBranchs()));
                    }

                    if (!franchise.getBranchs().contains(branchId)) {
                        franchise.getBranchs().add(branchId);
                    }

                    return franchiseRepository.save(franchise);
                });
    }
}