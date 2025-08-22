package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Franchise;
import com.example.resilient_api.domain.model.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RequiredArgsConstructor
public class AddSucursalToFranchiseUseCase {

    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> execute(String franchiseId, String sucursalId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franquicia no encontrada con id: " + franchiseId)))
                .flatMap(franchise -> {
                    if (franchise.getSucursales() == null) {
                        franchise.setSucursales(new java.util.ArrayList<>());
                    } else {
                        franchise.setSucursales(new ArrayList<>(franchise.getSucursales()));
                    }

                    if (!franchise.getSucursales().contains(sucursalId)) {
                        franchise.getSucursales().add(sucursalId);
                    }

                    return franchiseRepository.save(franchise);
                });
    }
}