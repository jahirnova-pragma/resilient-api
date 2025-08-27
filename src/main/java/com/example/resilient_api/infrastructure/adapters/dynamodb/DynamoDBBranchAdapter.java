package com.example.resilient_api.infrastructure.adapters.dynamodb;


import com.example.resilient_api.domain.model.Branch;
import com.example.resilient_api.domain.model.gateways.BranchRepository;
import com.example.resilient_api.infrastructure.entrypoints.mapper.BranchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
@RequiredArgsConstructor
public class DynamoDBBranchAdapter implements BranchRepository {

    private final DynamoDbAsyncTable<BranchEntity> branchTable;

    @Override
    public Mono<Branch> findById(String id) {
        CompletableFuture<BranchEntity> future = branchTable.getItem(r -> r.key(Key.builder().partitionValue(id).build()));
        return Mono.fromFuture(future)
                .map(BranchMapper::toDomain);
    }

    @Override
    public Mono<Branch> save(Branch branch) {
        BranchEntity entity = BranchMapper.toEntity(branch);
        CompletableFuture<BranchEntity> future = branchTable.putItem(entity)
                .thenApply(v -> entity);
        return Mono.fromFuture(future)
                .map(BranchMapper::toDomain);
    }

    @Override
    public Flux<Branch> findByIds(List<String> branchIds) {
        return Flux.fromIterable(branchIds)
                .flatMap(id -> Mono.fromFuture(() ->
                        branchTable.getItem(r -> r.key(k -> k.partitionValue(id)))
                ))
                .filter(s -> s != null)
                .map(BranchMapper::toDomain);
    }

}
