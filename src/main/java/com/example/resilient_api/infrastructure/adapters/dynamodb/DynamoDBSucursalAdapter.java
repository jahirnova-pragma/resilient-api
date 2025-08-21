package com.example.resilient_api.infrastructure.adapters.dynamodb;


import com.example.resilient_api.domain.model.Sucursal;
import com.example.resilient_api.domain.model.gateways.SucursalRepository;
import com.example.resilient_api.infrastructure.entrypoints.mapper.SucursalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.concurrent.CompletableFuture;

@Repository
@RequiredArgsConstructor
public class DynamoDBSucursalAdapter implements SucursalRepository {

    private final DynamoDbAsyncTable<SucursalEntity> sucursalTable;

    @Override
    public Mono<Sucursal> findById(String id) {
        CompletableFuture<SucursalEntity> future = sucursalTable.getItem(r -> r.key(Key.builder().partitionValue(id).build()));
        return Mono.fromFuture(future)
                .map(SucursalMapper::toDomain);
    }

    @Override
    public Mono<Sucursal> save(Sucursal sucursal) {
        SucursalEntity entity = SucursalMapper.toEntity(sucursal);
        CompletableFuture<SucursalEntity> future = sucursalTable.putItem(entity)
                .thenApply(v -> entity);
        return Mono.fromFuture(future)
                .map(SucursalMapper::toDomain);
    }
}
