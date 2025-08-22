package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Product;
import com.example.resilient_api.domain.model.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductStockUseCase {

    private final ProductRepository productRepository;

    public Mono<Product> execute(String productId, int newStock) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Product not found with id: " + productId)))
                .flatMap(product -> {
                    product.setStock(newStock);
                    return productRepository.save(product);
                });
    }
}
