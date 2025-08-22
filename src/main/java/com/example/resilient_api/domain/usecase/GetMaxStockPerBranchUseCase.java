package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Product;
import com.example.resilient_api.domain.model.gateways.FranchiseRepository;
import com.example.resilient_api.domain.model.gateways.ProductRepository;
import com.example.resilient_api.domain.model.gateways.SucursalRepository;
import com.example.resilient_api.infrastructure.entrypoints.dto.BranchWithMaxProductDTO;
import com.example.resilient_api.infrastructure.entrypoints.dto.FranchiseMaxStockDTO;
import com.example.resilient_api.infrastructure.entrypoints.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Comparator;
@Service
@RequiredArgsConstructor
public class GetMaxStockPerBranchUseCase {

    private final FranchiseRepository franchiseRepository;
    private final SucursalRepository sucursalRepository;
    private final ProductRepository productRepository;

    public Mono<FranchiseMaxStockDTO> execute(String franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .flatMap(franchise -> {
                    if (franchise.getSucursales() == null || franchise.getSucursales().isEmpty()) {
                        return Mono.just(FranchiseMaxStockDTO.builder()
                                .id(franchise.getId())
                                .name(franchise.getNombre())
                                .branches(Collections.emptyList())
                                .build());
                    }

                    return sucursalRepository.findByIds(franchise.getSucursales())
                            .flatMap(branch -> {
                                if (branch.getProductos() == null || branch.getProductos().isEmpty()) {
                                    return Mono.just(BranchWithMaxProductDTO.builder()
                                            .id(branch.getId())
                                            .name(branch.getNombre())
                                            .maxStockProduct(null)
                                            .build());
                                }

                                return productRepository.findByIds(branch.getProductos())
                                        .collectList()
                                        .map(products -> BranchWithMaxProductDTO.builder()
                                                .id(branch.getId())
                                                .name(branch.getNombre())
                                                .maxStockProduct(products.stream()
                                                        .max(Comparator.comparingInt(Product::getStock))
                                                        .map(maxProduct -> ProductDTO.builder()
                                                                .id(maxProduct.getId())
                                                                .name(maxProduct.getName())
                                                                .stock(maxProduct.getStock())
                                                                .build())
                                                        .orElse(null))
                                                .build());
                            })
                            .collectList()
                            .map(branches -> FranchiseMaxStockDTO.builder()
                                    .id(franchise.getId())
                                    .name(franchise.getNombre())
                                    .branches(branches)
                                    .build());
                });
    }

}
