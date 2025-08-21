package com.example.resilient_api.infrastructure.entrypoints.handler;

import com.example.resilient_api.domain.usecase.AddProductoToSucursalUseCase;
import com.example.resilient_api.domain.model.Sucursal;
import com.example.resilient_api.domain.usecase.RemoveProductFromSucursalUseCase;
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
public class SucursalHandlerImpl {

    private final AddProductoToSucursalUseCase addProductoToSucursalUseCase;
    private final RemoveProductFromSucursalUseCase removeProductFromSucursalUseCase;

    public Mono<ServerResponse> addProducto(ServerRequest request) {
        String sucursalId = request.pathVariable("sucursalId");
        String productoId = request.pathVariable("productoId");

        return addProductoToSucursalUseCase.execute(sucursalId, productoId)
                .flatMap(sucursal -> {
                    APIResponse<Sucursal> response = APIResponse.<Sucursal>builder()
                            .code("200")
                            .message("Producto agregado correctamente")
                            .identifier(sucursal.getId())
                            .date(LocalDateTime.now().toString())
                            .data(sucursal)
                            .build();
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                })
                .onErrorResume(e ->
                        ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(APIResponse.builder()
                                        .code("500")
                                        .message(e.getMessage())
                                        .date(LocalDateTime.now().toString())
                                        .build())
                );
    }

    public Mono<ServerResponse> removeProduct(ServerRequest request) {
        String sucursalId = request.pathVariable("sucursalId");
        String productId = request.pathVariable("productId");

        return removeProductFromSucursalUseCase.execute(sucursalId, productId)
                .flatMap(sucursal -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(sucursal))
                .onErrorResume(e -> ServerResponse.status(400)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ErrorDTO.builder()
                                .code("400")
                                .message(e.getMessage())
                                .build()));
    }

}
