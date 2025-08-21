package com.example.resilient_api.infrastructure.entrypoints.handler;

import com.example.resilient_api.domain.model.Franchise;
import com.example.resilient_api.domain.usecase.CreateFranchiseUseCase;
import com.example.resilient_api.domain.usecase.AddSucursalToFranchiseUseCase;
import com.example.resilient_api.infrastructure.entrypoints.dto.AddSucursalRequest;
import com.example.resilient_api.infrastructure.entrypoints.util.APIResponse;
import com.example.resilient_api.infrastructure.entrypoints.util.ErrorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class FranchiseHandlerImpl {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final AddSucursalToFranchiseUseCase addSucursalToFranchiseUseCase;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(Franchise.class)
                .flatMap(createFranchiseUseCase::createFranchise)
                .flatMap(franchise -> {
                    APIResponse<Franchise> response = APIResponse.<Franchise>builder()
                            .code("201")
                            .message("Franchise created successfully")
                            .identifier(franchise.getId())
                            .date(LocalDateTime.now().toString())
                            .data(franchise)
                            .build();
                    return ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                })
                .onErrorResume(e -> {


                    ErrorDTO error = ErrorDTO.builder()
                            .code("ADD_SUCURSAL_ERROR")
                            .message(e.getMessage())
                            .build();

                    APIResponse<ErrorDTO> errorResponse = APIResponse.<ErrorDTO>builder()
                            .code("500")
                            .message("Error creating franchise")
                            .date(LocalDateTime.now().toString())
                            .data(error)
                            .build();
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(errorResponse);
                });
    }

    public Mono<ServerResponse> addSucursalToFranchise(ServerRequest request) {
        String franchiseId = request.pathVariable("id");

        return request.bodyToMono(AddSucursalRequest.class)
                .flatMap(req -> {
                    if (req.getSucursalId() == null || req.getSucursalId().isBlank()) {
                        return Mono.error(new IllegalArgumentException("El campo 'sucursalId' es obligatorio"));
                    }
                    return addSucursalToFranchiseUseCase.execute(franchiseId, req.getSucursalId());
                })
                .flatMap(franchise -> {
                    APIResponse<Franchise> ok = APIResponse.<Franchise>builder()
                            .code("200")
                            .message("Sucursal agregada correctamente")
                            .identifier(franchise.getId())
                            .date(LocalDateTime.now().toString())
                            .data(franchise)
                            .build();
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ok);
                })
                .onErrorResume(IllegalArgumentException.class, e -> {
                    HttpStatus status = e.getMessage() != null && e.getMessage().startsWith("Franquicia no encontrada")
                            ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;

                    ErrorDTO error = ErrorDTO.builder()
                            .code("ADD_SUCURSAL_ERROR")
                            .param(franchiseId)
                            .message(e.getMessage())
                            .build();

                    APIResponse<ErrorDTO> err = APIResponse.<ErrorDTO>builder()
                            .code(String.valueOf(status.value()))
                            .message(status == HttpStatus.NOT_FOUND ? "Franquicia no encontrada" : "Solicitud inválida")
                            .date(LocalDateTime.now().toString())
                            .data(error)
                            .build();
                    return ServerResponse.status(status)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(err);
                })
                .onErrorResume(e -> {
                    ErrorDTO error = ErrorDTO.builder()
                            .code("ADD_SUCURSAL_ERROR")
                            .param(franchiseId)
                            .message(e.getMessage())
                            .build();

                    APIResponse<ErrorDTO> err = APIResponse.<ErrorDTO>builder()
                            .code("500")
                            .message("Error agregando sucursal a la franquicia")
                            .date(LocalDateTime.now().toString())
                            .data(error)
                            .build();
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(err);
                });
    }
}
