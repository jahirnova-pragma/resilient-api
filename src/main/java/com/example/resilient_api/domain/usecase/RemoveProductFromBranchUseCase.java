package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Branch;
import com.example.resilient_api.domain.model.gateways.BranchRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RemoveProductFromBranchUseCase {

    private final BranchRepository branchRepository;

    public Mono<Branch> execute(String branchId, String productId) {
        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Sucursal no encontrada con id: " + branchId)))
                .flatMap(branch -> {
                    if (branch.getProductos() != null && branch.getProductos().contains(productId)) {
                        branch.getProductos().remove(productId);
                        return branchRepository.save(branch);
                    }
                    return Mono.just(branch);
                });
    }
}
