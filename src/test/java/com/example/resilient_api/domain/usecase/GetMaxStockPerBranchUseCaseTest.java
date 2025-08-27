package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Franchise;
import com.example.resilient_api.domain.model.Product;
import com.example.resilient_api.domain.model.Sucursal;
import com.example.resilient_api.domain.model.gateways.FranchiseRepository;
import com.example.resilient_api.domain.model.gateways.ProductRepository;
import com.example.resilient_api.domain.model.gateways.SucursalRepository;
import com.example.resilient_api.infrastructure.entrypoints.dto.FranchiseMaxStockDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GetMaxStockPerBranchUseCaseTest {

    private static final String FRANCHISE_ID = "franchise-1";
    private static final String FRANCHISE_NAME = "Franchise Test";
    private static final String BRANCH_ID = "branch-1";
    private static final String BRANCH_NAME = "Branch Test";
    private static final String PRODUCT_ID_1 = "product-1";
    private static final String PRODUCT_NAME_1 = "Product 1";
    private static final int PRODUCT_STOCK_1 = 10;
    private static final String PRODUCT_ID_2 = "product-2";
    private static final String PRODUCT_NAME_2 = "Product 2";
    private static final int PRODUCT_STOCK_2 = 20;

    private FranchiseRepository franchiseRepository;
    private SucursalRepository sucursalRepository;
    private ProductRepository productRepository;
    private GetMaxStockPerBranchUseCase useCase;

    @BeforeEach
    void setUp() {
        franchiseRepository = mock(FranchiseRepository.class);
        sucursalRepository = mock(SucursalRepository.class);
        productRepository = mock(ProductRepository.class);
        useCase = new GetMaxStockPerBranchUseCase(franchiseRepository, sucursalRepository, productRepository);
    }

    @Test
    void shouldReturnFranchiseWithBranchAndMaxStockProduct() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(buildFranchise()));
        when(sucursalRepository.findByIds(List.of(BRANCH_ID))).thenReturn(Flux.just(buildBranch()));
        when(productRepository.findByIds(List.of(PRODUCT_ID_1, PRODUCT_ID_2)))
                .thenReturn(Flux.just(buildProduct(PRODUCT_ID_1, PRODUCT_NAME_1, PRODUCT_STOCK_1),
                        buildProduct(PRODUCT_ID_2, PRODUCT_NAME_2, PRODUCT_STOCK_2)));

        Mono<FranchiseMaxStockDTO> result = useCase.execute(FRANCHISE_ID);

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(FRANCHISE_ID);
                    assertThat(dto.getName()).isEqualTo(FRANCHISE_NAME);
                    assertThat(dto.getBranches()).hasSize(1);
                    assertThat(dto.getBranches().get(0).getId()).isEqualTo(BRANCH_ID);
                    assertThat(dto.getBranches().get(0).getName()).isEqualTo(BRANCH_NAME);
                    assertThat(dto.getBranches().get(0).getMaxStockProduct().getId()).isEqualTo(PRODUCT_ID_2);
                    assertThat(dto.getBranches().get(0).getMaxStockProduct().getStock()).isEqualTo(PRODUCT_STOCK_2);
                })
                .verifyComplete();
    }

    private Franchise buildFranchise() {
        return Franchise.builder()
                .id(FRANCHISE_ID)
                .nombre(FRANCHISE_NAME)
                .sucursales(List.of(BRANCH_ID))
                .build();
    }

    private Sucursal buildBranch() {
        return Sucursal.builder()
                .id(BRANCH_ID)
                .nombre(BRANCH_NAME)
                .productos(List.of(PRODUCT_ID_1, PRODUCT_ID_2))
                .build();
    }

    private Product buildProduct(String id, String name, int stock) {
        return Product.builder()
                .id(id)
                .name(name)
                .stock(stock)
                .build();
    }
}
