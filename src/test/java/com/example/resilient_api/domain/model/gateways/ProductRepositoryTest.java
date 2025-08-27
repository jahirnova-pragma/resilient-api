package com.example.resilient_api.domain.model.gateways;

import com.example.resilient_api.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ProductRepositoryTest {

    private static final String PRODUCT_ID = "PROD-01";
    private static final String PRODUCT_NAME = "Test Product";
    private static final int PRODUCT_STOCK = 10;
    private static final List<String> PRODUCT_IDS = List.of(PRODUCT_ID, "PROD-02");

    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);
    }

    @Test
    void shouldFindById() {
        Product product = buildProduct(PRODUCT_ID, PRODUCT_NAME, PRODUCT_STOCK);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Mono.just(product));

        StepVerifier.create(productRepository.findById(PRODUCT_ID))
                .assertNext(found -> assertThat(found).isEqualTo(product))
                .verifyComplete();
    }

    @Test
    void shouldSaveProduct() {
        Product product = buildProduct(PRODUCT_ID, PRODUCT_NAME, PRODUCT_STOCK);
        when(productRepository.save(product)).thenReturn(Mono.just(product));

        StepVerifier.create(productRepository.save(product))
                .assertNext(saved -> assertThat(saved).isEqualTo(product))
                .verifyComplete();
    }

    @Test
    void shouldFindByIds() {
        Product product1 = buildProduct(PRODUCT_ID, PRODUCT_NAME, PRODUCT_STOCK);
        Product product2 = buildProduct("PROD-02", "Another Product", 5);
        when(productRepository.findByIds(PRODUCT_IDS)).thenReturn(Flux.just(product1, product2));

        StepVerifier.create(productRepository.findByIds(PRODUCT_IDS))
                .expectNext(product1, product2)
                .verifyComplete();
    }

    private Product buildProduct(String id, String name, int stock) {
        return Product.builder()
                .id(id)
                .name(name)
                .stock(stock)
                .build();
    }
}
