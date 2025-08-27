package com.example.resilient_api.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    private static final String PRODUCT_ID = "PROD-01";
    private static final String PRODUCT_NAME = "Laptop";
    private static final int PRODUCT_STOCK = 50;
    private static final int UPDATED_STOCK = 100;
    private static final String UPDATED_NAME = "Monitor";

    @Test
    void shouldBuildProductCorrectly() {
        Product product = buildProduct(PRODUCT_ID, PRODUCT_NAME, PRODUCT_STOCK);

        assertThat(product.getId()).isEqualTo(PRODUCT_ID);
        assertThat(product.getName()).isEqualTo(PRODUCT_NAME);
        assertThat(product.getStock()).isEqualTo(PRODUCT_STOCK);
    }

    @Test
    void shouldUpdateProductWithToBuilder() {
        Product product = buildProduct(PRODUCT_ID, PRODUCT_NAME, PRODUCT_STOCK)
                .toBuilder()
                .stock(UPDATED_STOCK)
                .name(UPDATED_NAME)
                .build();

        assertThat(product.getStock()).isEqualTo(UPDATED_STOCK);
        assertThat(product.getName()).isEqualTo(UPDATED_NAME);
    }

    private Product buildProduct(String id, String name, int stock) {
        return Product.builder()
                .id(id)
                .name(name)
                .stock(stock)
                .build();
    }
}
