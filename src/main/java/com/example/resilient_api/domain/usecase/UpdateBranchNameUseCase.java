package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Branch;
import com.example.resilient_api.domain.model.gateways.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UpdateBranchNameUseCase {

    private final BranchRepository branchRepository;

    public Mono<Branch> execute(String branchId, String newName) {
        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Sucursal not found")))
                .flatMap(branch -> {
                    branch.setNombre(newName);
                    return branchRepository.save(branch);
                });
    }
}
