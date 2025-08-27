package com.example.resilient_api.domain.usecase;
import com.example.resilient_api.domain.model.Product;
import com.example.resilient_api.domain.model.gateways.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class UpdateProductNameUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpdateProductNameUseCase updateProductNameUseCase;

    private static final String PRODUCT_ID = "PROD-01";
    private static final String OLD_NAME = "Old Product";
    private static final String NEW_NAME = "Updated Product";

    private Product existingProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        existingProduct = Product.builder()
                .id(PRODUCT_ID)
                .name(OLD_NAME)
                .stock(10)
                .build();
    }

    @Test
    void shouldUpdateProductNameSuccessfully() {
        // Arrange
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Mono.just(existingProduct));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act & Assert
        StepVerifier.create(updateProductNameUseCase.execute(PRODUCT_ID, NEW_NAME))
                .expectNextMatches(product ->
                        product.getId().equals(PRODUCT_ID) &&
                                product.getName().equals(NEW_NAME) &&
                                product.getStock() == 10
                )
                .verifyComplete();

        verify(productRepository).findById(PRODUCT_ID);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldReturnErrorWhenProductNotFound() {
        // Arrange
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(updateProductNameUseCase.execute(PRODUCT_ID, NEW_NAME))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Product not found")
                )
                .verify();

        verify(productRepository).findById(PRODUCT_ID);
        verify(productRepository, never()).save(any(Product.class));
    }
}

