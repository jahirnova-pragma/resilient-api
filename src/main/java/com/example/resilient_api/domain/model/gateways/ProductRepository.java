package com.example.resilient_api.domain.model.gateways;

import com.example.resilient_api.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductRepository {
    Mono<Product> findById(String id);
    Mono<Product> save(Product product);
    Flux<Product> findByIds(List<String> productIds);
}
