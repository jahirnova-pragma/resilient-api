package com.example.resilient_api.domain.model.gateways;

import com.example.resilient_api.domain.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BranchRepository {
    Mono<Branch> findById(String id);
    Mono<Branch> save(Branch branch);
    Flux<Branch> findByIds(List<String> branchIds);
}