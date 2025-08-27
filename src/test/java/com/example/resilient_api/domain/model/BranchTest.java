package com.example.resilient_api.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BranchTest {

    private static final String ID = "SUC-001";
    private static final String NOMBRE = "Sucursal Central";
    private static final List<String> PRODUCTOS = List.of("PROD-001", "PROD-002");

    @Test
    void shouldBuildBranchCorrectly() {
        Branch branch = buildBranch();

        assertThat(branch.getId()).isEqualTo(ID);
        assertThat(branch.getNombre()).isEqualTo(NOMBRE);
        assertThat(branch.getProductos()).containsExactlyElementsOf(PRODUCTOS);
    }

    @Test
    void shouldModifyBranchWithToBuilder() {
        Branch branch = buildBranch().toBuilder()
                .nombre("Sucursal Norte")
                .build();

        assertThat(branch.getId()).isEqualTo(ID);
        assertThat(branch.getNombre()).isEqualTo("Sucursal Norte");
    }

    private Branch buildBranch() {
        return Branch.builder()
                .id(ID)
                .nombre(NOMBRE)
                .productos(PRODUCTOS)
                .build();
    }
}
