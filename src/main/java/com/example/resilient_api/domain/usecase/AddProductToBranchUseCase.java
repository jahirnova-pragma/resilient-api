package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Branch;
import com.example.resilient_api.domain.model.gateways.BranchRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RequiredArgsConstructor
public class AddProductToBranchUseCase {

    private final BranchRepository branchRepository;

    public Mono<Branch> execute(String branchId, String productoId) {
        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Branch no encontrada con id: " + branchId)))
                .flatMap(branch -> {
                    if (branch.getProductos() == null) {
                        branch.setProductos(new ArrayList<>());
                    }

                    if (!branch.getProductos().contains(productoId)) {
                        branch.getProductos().add(productoId);
                    }

                    return branchRepository.save(branch);
                });
    }
}