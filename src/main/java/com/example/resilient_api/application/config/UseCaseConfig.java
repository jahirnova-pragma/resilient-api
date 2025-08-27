package com.example.resilient_api.application.config;

import com.example.resilient_api.domain.model.gateways.ProductRepository;
import com.example.resilient_api.domain.model.gateways.BranchRepository;
import com.example.resilient_api.domain.usecase.AddProductToBranchUseCase;
import com.example.resilient_api.domain.usecase.AddBranchToFranchiseUseCase;
import com.example.resilient_api.domain.usecase.CreateFranchiseUseCase;
import com.example.resilient_api.domain.model.gateways.FranchiseRepository;
import com.example.resilient_api.domain.usecase.GetMaxStockPerBranchUseCase;
import com.example.resilient_api.domain.usecase.RemoveProductFromBranchUseCase;
import com.example.resilient_api.domain.usecase.UpdateFranchiseNameUseCase;
import com.example.resilient_api.domain.usecase.UpdateProductNameUseCase;
import com.example.resilient_api.domain.usecase.UpdateProductStockUseCase;
import com.example.resilient_api.domain.usecase.UpdateBranchNameUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateFranchiseUseCase createFranchiseUseCase(FranchiseRepository franchiseRepository) {
        return new CreateFranchiseUseCase(franchiseRepository);
    }

    @Bean
    public AddBranchToFranchiseUseCase addBranchToFranchiseUseCase(FranchiseRepository repository) {
        return new AddBranchToFranchiseUseCase(repository);
    }

    @Bean
    public AddProductToBranchUseCase addProductToBranchUseCase(BranchRepository repository) {
        return new AddProductToBranchUseCase(repository);
    }

    @Bean
    public RemoveProductFromBranchUseCase removeProductFromBranchUseCase(BranchRepository repository) {
        return new RemoveProductFromBranchUseCase(repository);
    }

    @Bean
    public UpdateProductStockUseCase updateProductStockUseCase(ProductRepository repository) {
        return new UpdateProductStockUseCase(repository);
    }

    @Bean
    public GetMaxStockPerBranchUseCase getMaxStockPerBranchUseCase(FranchiseRepository franchiseRepository,
                                                                   BranchRepository branchRepository,
                                                                   ProductRepository productRepository) {
        return new GetMaxStockPerBranchUseCase(franchiseRepository, branchRepository, productRepository);
    }

    @Bean
    public UpdateFranchiseNameUseCase updateFranchiseNameUseCase(FranchiseRepository repository) {
        return new UpdateFranchiseNameUseCase(repository);
    }

    @Bean
    public UpdateBranchNameUseCase updateBranchNameUseCase(BranchRepository repository) {
        return new UpdateBranchNameUseCase(repository);
    }

    @Bean
    public UpdateProductNameUseCase updateProductNameUseCase(ProductRepository repository) {
        return new UpdateProductNameUseCase(repository);
    }

}
