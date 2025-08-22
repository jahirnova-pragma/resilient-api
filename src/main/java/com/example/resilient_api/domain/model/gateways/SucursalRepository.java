package com.example.resilient_api.domain.model.gateways;

import com.example.resilient_api.domain.model.Sucursal;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SucursalRepository {
    Mono<Sucursal> findById(String id);
    Mono<Sucursal> save(Sucursal sucursal);
    Flux<Sucursal> findByIds(List<String> sucursalIds);
}