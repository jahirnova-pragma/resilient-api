package com.example.resilient_api.infrastructure.entrypoints.mapper;



import com.example.resilient_api.domain.model.Branch;
import com.example.resilient_api.infrastructure.adapters.dynamodb.BranchEntity;
import java.util.ArrayList;

public class BranchMapper {

    public static Branch toDomain(BranchEntity entity) {
        if (entity == null) return null;
        return Branch.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .productos(entity.getProductos() != null ? new ArrayList<>(entity.getProductos()) : new ArrayList<>())
                .build();
    }

    public static BranchEntity toEntity(Branch domain) {
        if (domain == null) return null;
        return BranchEntity.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .productos(domain.getProductos())
                .build();
    }
}
