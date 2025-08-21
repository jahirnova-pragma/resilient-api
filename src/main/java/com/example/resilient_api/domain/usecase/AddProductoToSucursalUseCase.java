package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Franchise;
import com.example.resilient_api.domain.model.Sucursal;
import com.example.resilient_api.domain.model.gateways.FranchiseRepository;
import com.example.resilient_api.domain.model.gateways.SucursalRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RequiredArgsConstructor
public class AddProductoToSucursalUseCase {

    private final SucursalRepository sucursalRepository;

    public Mono<Sucursal> execute(String sucursalId, String productoId) {
        return sucursalRepository.findById(sucursalId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Sucursal no encontrada con id: " + sucursalId)))
                .flatMap(sucursal -> {
                    if (sucursal.getProductos() == null) {
                        sucursal.setProductos(new ArrayList<>());
                    }

                    if (!sucursal.getProductos().contains(productoId)) {
                        sucursal.getProductos().add(productoId);
                    }

                    return sucursalRepository.save(sucursal);
                });
    }
}