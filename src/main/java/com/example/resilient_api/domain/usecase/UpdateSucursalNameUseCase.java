package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Sucursal;
import com.example.resilient_api.domain.model.gateways.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UpdateSucursalNameUseCase {

    private final SucursalRepository sucursalRepository;

    public Mono<Sucursal> execute(String sucursalId, String newName) {
        return sucursalRepository.findById(sucursalId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Sucursal not found")))
                .flatMap(sucursal -> {
                    sucursal.setNombre(newName);
                    return sucursalRepository.save(sucursal);
                });
    }
}
