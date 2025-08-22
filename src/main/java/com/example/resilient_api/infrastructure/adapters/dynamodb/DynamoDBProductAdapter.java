package com.example.resilient_api.infrastructure.adapters.dynamodb;

import com.example.resilient_api.domain.model.Product;
import com.example.resilient_api.domain.model.gateways.ProductRepository;
import com.example.resilient_api.infrastructure.entrypoints.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
@RequiredArgsConstructor
public class DynamoDBProductAdapter implements ProductRepository {

    private final DynamoDbAsyncTable<ProductEntity> productTable;

    @Override
    public Mono<Product> findById(String id) {
        return Mono.fromFuture(() ->
                productTable.getItem(r -> r.key(k -> k.partitionValue(id)))
        ).map(ProductMapper::toDomain);
    }

    @Override
    public Mono<Product> save(Product product) {
        ProductEntity entity = ProductMapper.toEntity(product);
        CompletableFuture<ProductEntity> future = productTable.putItem(entity)
                .thenApply(ignored -> entity);
        return Mono.fromFuture(future)
                .map(ProductMapper::toDomain);
    }

    @Override
    public Flux<Product> findByIds(List<String> productIds) {
        return Flux.fromIterable(productIds)
                .flatMap(id -> Mono.fromFuture(() ->
                        productTable.getItem(r -> r.key(k -> k.partitionValue(id)))
                ))
                .filter(p -> p != null)
                .map(p -> Product.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .stock(p.getStock())
                        .build());
    }

}
