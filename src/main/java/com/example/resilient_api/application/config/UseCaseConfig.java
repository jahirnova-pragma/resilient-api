package com.example.resilient_api.application.config;

import com.example.resilient_api.domain.usecase.AddSucursalToFranchiseUseCase;
import com.example.resilient_api.domain.usecase.CreateFranchiseUseCase;
import com.example.resilient_api.domain.model.gateways.FranchiseRepository;
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

}
