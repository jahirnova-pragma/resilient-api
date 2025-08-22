package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Product;
import com.example.resilient_api.domain.model.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UpdateProductNameUseCase {

    private final ProductRepository productRepository;

    public Mono<Product> execute(String productId, String newName) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Product not found")))
                .flatMap(product -> {
                    product.setName(newName);
                    return productRepository.save(product);
                });
    }
}
