package com.example.resilient_api.application.config;

import com.example.resilient_api.domain.model.gateways.ProductRepository;
import com.example.resilient_api.domain.model.gateways.SucursalRepository;
import com.example.resilient_api.domain.usecase.AddProductoToSucursalUseCase;
import com.example.resilient_api.domain.usecase.AddSucursalToFranchiseUseCase;
import com.example.resilient_api.domain.usecase.CreateFranchiseUseCase;
import com.example.resilient_api.domain.model.gateways.FranchiseRepository;
import com.example.resilient_api.domain.usecase.GetMaxStockPerBranchUseCase;
import com.example.resilient_api.domain.usecase.RemoveProductFromSucursalUseCase;
import com.example.resilient_api.domain.usecase.UpdateFranchiseNameUseCase;
import com.example.resilient_api.domain.usecase.UpdateProductNameUseCase;
import com.example.resilient_api.domain.usecase.UpdateProductStockUseCase;
import com.example.resilient_api.domain.usecase.UpdateSucursalNameUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateFranchiseUseCase createFranchiseUseCase(FranchiseRepository franchiseRepository) {
        return new CreateFranchiseUseCase(franchiseRepository);
    }

    @Bean
    public AddSucursalToFranchiseUseCase addSucursalToFranchiseUseCase(FranchiseRepository repository) {
        return new AddSucursalToFranchiseUseCase(repository);
    }

    @Bean
    public AddProductoToSucursalUseCase addProductToSucursalUseCase(SucursalRepository repository) {
        return new AddProductoToSucursalUseCase(repository);
    }

    @Bean
    public RemoveProductFromSucursalUseCase removeProductFromSucursalUseCase(SucursalRepository repository) {
        return new RemoveProductFromSucursalUseCase(repository);
    }

    @Bean
    public UpdateProductStockUseCase updateProductStockUseCase(ProductRepository repository) {
        return new UpdateProductStockUseCase(repository);
    }

    @Bean
    public GetMaxStockPerBranchUseCase getMaxStockPerBranchUseCase(FranchiseRepository franchiseRepository,
                                                                   SucursalRepository sucursalRepository,
                                                                   ProductRepository productRepository) {
        return new GetMaxStockPerBranchUseCase(franchiseRepository,sucursalRepository, productRepository);
    }

    @Bean
    public UpdateFranchiseNameUseCase updateFranchiseNameUseCase(FranchiseRepository repository) {
        return new UpdateFranchiseNameUseCase(repository);
    }

    @Bean
    public UpdateSucursalNameUseCase updateSucursalNameUseCase(SucursalRepository repository) {
        return new UpdateSucursalNameUseCase(repository);
    }

    @Bean
    public UpdateProductNameUseCase updateProductNameUseCase(ProductRepository repository) {
        return new UpdateProductNameUseCase(repository);
    }

}
