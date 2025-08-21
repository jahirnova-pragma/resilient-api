package com.example.resilient_api.infrastructure.entrypoints.mapper;

import com.example.resilient_api.domain.model.Franchise;
import com.example.resilient_api.infrastructure.adapters.dynamodb.FranchiseEntity;

public class FranchiseMapper {

    public static Franchise toDomain(FranchiseEntity entity) {
        if (entity == null) {
            return null;
        }
        return Franchise.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .sucursales(entity.getSucursales())
                .build();
    }

    public static FranchiseEntity toEntity(Franchise franchise) {
        if (franchise == null) {
            return null;
        }
        return FranchiseEntity.builder()
                .id(franchise.getId())
                .nombre(franchise.getNombre())
                .sucursales(franchise.getSucursales())
                .build();
    }
}
