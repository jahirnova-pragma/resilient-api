package com.example.resilient_api.infrastructure.adapters.dynamodb;

import com.example.resilient_api.domain.model.Franchise;
import com.example.resilient_api.domain.model.Sucursal;
import com.example.resilient_api.domain.model.gateways.FranchiseRepository;
import com.example.resilient_api.infrastructure.entrypoints.mapper.FranchiseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
@RequiredArgsConstructor
public class DynamoDBFranchiseAdapter implements FranchiseRepository {

    private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    private DynamoDbAsyncTable<FranchiseEntity> getTable() {
        return dynamoDbEnhancedAsyncClient.table("Franchise", TableSchema.fromBean(FranchiseEntity.class));
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        FranchiseEntity entity = FranchiseEntity.builder()
                .id(franchise.getId())
                .nombre(franchise.getNombre())
                .sucursales(franchise.getSucursales())
                .build();

        return Mono.fromFuture(() -> getTable().putItem(entity))
                .thenReturn(franchise)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return Mono.fromFuture(() -> getTable().getItem(r -> r.key(k -> k.partitionValue(id))))
                .map(FranchiseMapper::toDomain);
    }

}
