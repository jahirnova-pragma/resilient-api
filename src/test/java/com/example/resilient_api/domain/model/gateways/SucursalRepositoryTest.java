package com.example.resilient_api.domain.model.gateways;

import com.example.resilient_api.domain.model.Sucursal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

class SucursalRepositoryTest {

    private static final String SUCURSAL_ID = "SUC-001";
    private static final String SUCURSAL_NOMBRE = "Sucursal Central";
    private static final List<String> PRODUCTOS = List.of("PROD-001", "PROD-002");

    private SucursalRepository sucursalRepository;

    @BeforeEach
    void setUp() {
        sucursalRepository = new InMemorySucursalRepository();
    }

    @Test
    void shouldSaveSucursal() {
        StepVerifier.create(sucursalRepository.save(buildSucursal()))
                .expectNextMatches(sucursal -> sucursal.getId().equals(SUCURSAL_ID))
                .verifyComplete();
    }

    @Test
    void shouldFindSucursalById() {
        sucursalRepository.save(buildSucursal()).block();

        StepVerifier.create(sucursalRepository.findById(SUCURSAL_ID))
                .expectNextMatches(sucursal -> sucursal.getNombre().equals(SUCURSAL_NOMBRE))
                .verifyComplete();
    }

    @Test
    void shouldFindSucursalesByIds() {
        sucursalRepository.save(buildSucursal()).block();

        StepVerifier.create(sucursalRepository.findByIds(List.of(SUCURSAL_ID)))
                .expectNextMatches(sucursal -> sucursal.getProductos().equals(PRODUCTOS))
                .verifyComplete();
    }

    private Sucursal buildSucursal() {
        return Sucursal.builder()
                .id(SUCURSAL_ID)
                .nombre(SUCURSAL_NOMBRE)
                .productos(PRODUCTOS)
                .build();
    }

    private static class InMemorySucursalRepository implements SucursalRepository {
        private final java.util.Map<String, Sucursal> database = new java.util.HashMap<>();

        @Override
        public Mono<Sucursal> findById(String id) {
            return Mono.justOrEmpty(database.get(id));
        }

        @Override
        public Mono<Sucursal> save(Sucursal sucursal) {
            database.put(sucursal.getId(), sucursal);
            return Mono.just(sucursal);
        }

        @Override
        public Flux<Sucursal> findByIds(List<String> sucursalIds) {
            return Flux.fromIterable(sucursalIds)
                    .map(database::get)
                    .filter(sucursal -> sucursal != null);
        }
    }
}
