package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Sucursal;
import com.example.resilient_api.domain.model.gateways.SucursalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class UpdateSucursalNameUseCaseTest {

    private static final String SUCURSAL_ID = "SUC-123";
    private static final String SUCURSAL_NAME = "Old Name";
    private static final String UPDATED_NAME = "New Name";
    private static final String ERROR_MESSAGE = "Sucursal not found";

    private SucursalRepository sucursalRepository;
    private UpdateSucursalNameUseCase useCase;

    @BeforeEach
    void setUp() {
        sucursalRepository = Mockito.mock(SucursalRepository.class);
        useCase = new UpdateSucursalNameUseCase(sucursalRepository);
    }

    @Test
    void shouldUpdateSucursalNameWhenExists() {
        Sucursal sucursal = buildSucursal(SUCURSAL_ID, SUCURSAL_NAME);
        when(sucursalRepository.findById(SUCURSAL_ID)).thenReturn(Mono.just(sucursal));
        when(sucursalRepository.save(any(Sucursal.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.execute(SUCURSAL_ID, UPDATED_NAME))
                .expectNextMatches(result -> result.getId().equals(SUCURSAL_ID) && result.getNombre().equals(UPDATED_NAME))
                .verifyComplete();

        verify(sucursalRepository).findById(SUCURSAL_ID);
        verify(sucursalRepository).save(any(Sucursal.class));
    }

    @Test
    void shouldReturnErrorWhenSucursalNotFound() {
        when(sucursalRepository.findById(SUCURSAL_ID)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(SUCURSAL_ID, UPDATED_NAME))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException
                        && throwable.getMessage().equals(ERROR_MESSAGE))
                .verify();

        verify(sucursalRepository).findById(SUCURSAL_ID);
        verify(sucursalRepository, never()).save(any(Sucursal.class));
    }

    private Sucursal buildSucursal(String id, String name) {
        Sucursal sucursal = new Sucursal();
        sucursal.setId(id);
        sucursal.setNombre(name);
        return sucursal;
    }
}
