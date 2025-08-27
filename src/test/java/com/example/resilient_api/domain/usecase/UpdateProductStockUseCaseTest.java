package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Product;
import com.example.resilient_api.domain.model.gateways.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UpdateProductStockUseCaseTest {

    private static final String PRODUCT_ID = "PROD-001";
    private static final String UNKNOWN_PRODUCT_ID = "UNKNOWN";
    private static final int INITIAL_STOCK = 10;
    private static final int UPDATED_STOCK = 25;
    private static final String ERROR_MESSAGE = "Product not found with id: " + UNKNOWN_PRODUCT_ID;

    private ProductRepository productRepository;
    private UpdateProductStockUseCase updateProductStockUseCase;

    @BeforeEach
    void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);
        updateProductStockUseCase = new UpdateProductStockUseCase(productRepository);
    }

    @Test
    void shouldUpdateStockWhenProductExists() {
        Product existingProduct = buildProduct(PRODUCT_ID, INITIAL_STOCK);
        Product updatedProduct = buildProduct(PRODUCT_ID, UPDATED_STOCK);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Mono.just(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(updateProductStockUseCase.execute(PRODUCT_ID, UPDATED_STOCK))
                .assertNext(product -> {
                    assertThat(product.getId()).isEqualTo(PRODUCT_ID);
                    assertThat(product.getStock()).isEqualTo(UPDATED_STOCK);
                })
                .verifyComplete();

        verify(productRepository).findById(PRODUCT_ID);
        verify(productRepository).save(existingProduct);
    }

    @Test
    void shouldReturnErrorWhenProductDoesNotExist() {
        when(productRepository.findById(UNKNOWN_PRODUCT_ID)).thenReturn(Mono.empty());

        StepVerifier.create(updateProductStockUseCase.execute(UNKNOWN_PRODUCT_ID, UPDATED_STOCK))
                .expectErrorSatisfies(error -> assertThat(error)
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage(ERROR_MESSAGE))
                .verify();

        verify(productRepository).findById(UNKNOWN_PRODUCT_ID);
        verify(productRepository, never()).save(any());
    }

    private Product buildProduct(String id, int stock) {
        Product product = new Product();
        product.setId(id);
        product.setStock(stock);
        return product;
    }
}
