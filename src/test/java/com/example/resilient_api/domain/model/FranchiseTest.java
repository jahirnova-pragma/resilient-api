package com.example.resilient_api.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FranchiseTest {

    private static final String ID = "SUC-01";
    private static final String NOMBRE = "Franquicia Test";
    private static final List<String> BRANCHS = List.of("Sucursal1", "Sucursal2");
    private static final String NEW_NOMBRE = "Franquicia Actualizada";

    @Test
    void shouldBuildFranchiseWithValues() {
        Franchise franchise = buildFranchise(ID, NOMBRE, BRANCHS);

        assertThat(franchise.getId()).isEqualTo(ID);
        assertThat(franchise.getNombre()).isEqualTo(NOMBRE);
        assertThat(franchise.getBranchs()).containsExactlyElementsOf(BRANCHS);
    }

    @Test
    void shouldUseToBuilderToModifyValues() {
        Franchise updatedFranchise = buildFranchise(ID, NOMBRE, BRANCHS)
                .toBuilder()
                .nombre(NEW_NOMBRE)
                .build();

        assertThat(updatedFranchise.getNombre()).isEqualTo(NEW_NOMBRE);
        assertThat(updatedFranchise.getId()).isEqualTo(ID);
    }


    private Franchise buildFranchise(String id, String nombre, List<String> branchs) {
        return Franchise.builder()
                .id(id)
                .nombre(nombre)
                .branchs(branchs)
                .build();
    }
}
