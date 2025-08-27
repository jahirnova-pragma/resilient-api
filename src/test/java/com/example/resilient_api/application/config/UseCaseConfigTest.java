package com.example.resilient_api.application.config;

import com.example.resilient_api.domain.model.gateways.FranchiseRepository;
import com.example.resilient_api.domain.model.gateways.ProductRepository;
import com.example.resilient_api.domain.model.gateways.SucursalRepository;
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
    private SucursalRepository sucursalRepository;

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private CreateFranchiseUseCase createFranchiseUseCase;
    @Autowired
    private AddSucursalToFranchiseUseCase addSucursalToFranchiseUseCase;
    @Autowired
    private AddProductoToSucursalUseCase addProductoToSucursalUseCase;
    @Autowired
    private RemoveProductFromSucursalUseCase removeProductFromSucursalUseCase;
    @Autowired
    private UpdateProductStockUseCase updateProductStockUseCase;
    @Autowired
    private GetMaxStockPerBranchUseCase getMaxStockPerBranchUseCase;
    @Autowired
    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    @Autowired
    private UpdateSucursalNameUseCase updateSucursalNameUseCase;
    @Autowired
    private UpdateProductNameUseCase updateProductNameUseCase;

    @Test
    void shouldLoadAllUseCases() {
        assertThat(createFranchiseUseCase).isNotNull();
        assertThat(addSucursalToFranchiseUseCase).isNotNull();
        assertThat(addProductoToSucursalUseCase).isNotNull();
        assertThat(removeProductFromSucursalUseCase).isNotNull();
        assertThat(updateProductStockUseCase).isNotNull();
        assertThat(getMaxStockPerBranchUseCase).isNotNull();
        assertThat(updateFranchiseNameUseCase).isNotNull();
        assertThat(updateSucursalNameUseCase).isNotNull();
        assertThat(updateProductNameUseCase).isNotNull();
    }
}
