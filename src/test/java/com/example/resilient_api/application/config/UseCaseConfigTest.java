package com.example.resilient_api.application.config;

import com.example.resilient_api.domain.model.gateways.FranchiseRepository;
import com.example.resilient_api.domain.model.gateways.ProductRepository;
import com.example.resilient_api.domain.model.gateways.BranchRepository;
import com.example.resilient_api.domain.usecase.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = UseCaseConfig.class)
class UseCaseConfigTest {

    @MockBean
    private FranchiseRepository franchiseRepository;

    @MockBean
    private BranchRepository branchRepository;

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private CreateFranchiseUseCase createFranchiseUseCase;
    @Autowired
    private AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;
    @Autowired
    private AddProductToBranchUseCase addProductToBranchUseCase;
    @Autowired
    private RemoveProductFromBranchUseCase removeProductFromBranchUseCase;
    @Autowired
    private UpdateProductStockUseCase updateProductStockUseCase;
    @Autowired
    private GetMaxStockPerBranchUseCase getMaxStockPerBranchUseCase;
    @Autowired
    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    @Autowired
    private UpdateBranchNameUseCase updateBranchNameUseCase;
    @Autowired
    private UpdateProductNameUseCase updateProductNameUseCase;

    @Test
    void shouldLoadAllUseCases() {
        assertThat(createFranchiseUseCase).isNotNull();
        assertThat(addBranchToFranchiseUseCase).isNotNull();
        assertThat(addProductToBranchUseCase).isNotNull();
        assertThat(removeProductFromBranchUseCase).isNotNull();
        assertThat(updateProductStockUseCase).isNotNull();
        assertThat(getMaxStockPerBranchUseCase).isNotNull();
        assertThat(updateFranchiseNameUseCase).isNotNull();
        assertThat(updateBranchNameUseCase).isNotNull();
        assertThat(updateProductNameUseCase).isNotNull();
    }
}
