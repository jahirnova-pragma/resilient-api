package com.example.resilient_api.domain.model.gateways;

import com.example.resilient_api.domain.model.Franchise;
import com.example.resilient_api.domain.model.Sucursal;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface FranchiseRepository {
    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(String id);
}
