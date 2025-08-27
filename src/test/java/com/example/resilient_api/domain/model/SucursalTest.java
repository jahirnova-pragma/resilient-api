package com.example.resilient_api.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SucursalTest {

    private static final String ID = "SUC-001";
    private static final String NOMBRE = "Sucursal Central";
    private static final List<String> PRODUCTOS = List.of("PROD-001", "PROD-002");

    @Test
    void shouldBuildSucursalCorrectly() {
        Sucursal sucursal = buildSucursal();

        assertThat(sucursal.getId()).isEqualTo(ID);
        assertThat(sucursal.getNombre()).isEqualTo(NOMBRE);
        assertThat(sucursal.getProductos()).containsExactlyElementsOf(PRODUCTOS);
    }

    @Test
    void shouldModifySucursalWithToBuilder() {
        Sucursal sucursal = buildSucursal().toBuilder()
                .nombre("Sucursal Norte")
                .build();

        assertThat(sucursal.getId()).isEqualTo(ID);
        assertThat(sucursal.getNombre()).isEqualTo("Sucursal Norte");
    }

    private Sucursal buildSucursal() {
        return Sucursal.builder()
                .id(ID)
                .nombre(NOMBRE)
                .productos(PRODUCTOS)
                .build();
    }
}
