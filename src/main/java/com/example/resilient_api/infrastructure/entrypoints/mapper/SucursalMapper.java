package com.example.resilient_api.infrastructure.entrypoints.mapper;



import com.example.resilient_api.domain.model.Sucursal;
import com.example.resilient_api.infrastructure.adapters.dynamodb.SucursalEntity;
import java.util.ArrayList;
import java.util.List;

public class SucursalMapper {

    public static Sucursal toDomain(SucursalEntity entity) {
        if (entity == null) return null;
        return Sucursal.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .productos(entity.getProductos() != null ? new ArrayList<>(entity.getProductos()) : new ArrayList<>())
                .build();
    }

    public static SucursalEntity toEntity(Sucursal domain) {
        if (domain == null) return null;
        return SucursalEntity.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .productos(domain.getProductos())
                .build();
    }
}
