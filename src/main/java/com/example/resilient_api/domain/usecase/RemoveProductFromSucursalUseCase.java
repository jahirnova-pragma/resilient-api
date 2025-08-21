package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Sucursal;
import com.example.resilient_api.domain.model.gateways.SucursalRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RemoveProductFromSucursalUseCase {

    private final SucursalRepository sucursalRepository;

    public Mono<Sucursal> execute(String sucursalId, String productId) {
        return sucursalRepository.findById(sucursalId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Sucursal no encontrada con id: " + sucursalId)))
                .flatMap(sucursal -> {
                    if (sucursal.getProductos() != null && sucursal.getProductos().contains(productId)) {
                        sucursal.getProductos().remove(productId);
                        return sucursalRepository.save(sucursal);
                    }
                    return Mono.just(sucursal);
                });
    }
}
