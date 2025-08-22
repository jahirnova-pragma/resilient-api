package com.example.resilient_api.infrastructure.entrypoints.handler;

import com.example.resilient_api.domain.usecase.AddProductoToSucursalUseCase;
import com.example.resilient_api.domain.model.Sucursal;
import com.example.resilient_api.domain.usecase.RemoveProductFromSucursalUseCase;
import com.example.resilient_api.domain.usecase.UpdateSucursalNameUseCase;
import com.example.resilient_api.infrastructure.entrypoints.dto.UpdateSucursalNameRequest;
import com.example.resilient_api.infrastructure.entrypoints.util.APIResponse;
import com.example.resilient_api.infrastructure.entrypoints.util.ErrorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    private final UpdateSucursalNameUseCase updateSucursalNameUseCase;

    @Operation(
            summary = "Agregar producto a una sucursal",
            description = "Permite agregar un producto existente a una sucursal específica",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "sucursalId",
                            description = "ID de la sucursal",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "SUC-01"
                    ),
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "productoId",
                            description = "ID del producto a agregar",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "PROD-01-01"
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto agregado correctamente",
                            content = @Content(schema = @Schema(implementation = APIResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                            content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno",
                            content = @Content)
            }
    )
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

    @Operation(
            summary = "Eliminar producto de una sucursal",
            description = "Permite remover un producto específico de una sucursal",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "sucursalId",
                            description = "ID de la sucursal",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "SUC-001"
                    ),
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "productoId",
                            description = "ID del producto a eliminar",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "PROD-001"
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto removido correctamente",
                            content = @Content(schema = @Schema(implementation = APIResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                            content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno",
                            content = @Content)
            }
    )
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

    @Operation(
            summary = "Actualizar nombre de una sucursal",
            description = "Permite cambiar el nombre de una sucursal existente",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "id",
                            description = "ID de la sucursal",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "SUC-01"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevo nombre de la sucursal",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateSucursalNameRequest.class,
                                    example = "{ \"name\": \"Sucursal ABC\" }")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nombre actualizado",
                            content = @Content(schema = @Schema(implementation = APIResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                            content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno",
                            content = @Content)
            }
    )
    public Mono<ServerResponse> updateSucursalName(ServerRequest request) {
        String sucursalId = request.pathVariable("id");

        return request.bodyToMono(UpdateSucursalNameRequest.class)
                .flatMap(req -> {
                    if (req.getName() == null || req.getName().isBlank()) {
                        return Mono.error(new IllegalArgumentException("The field 'name' is required"));
                    }
                    return updateSucursalNameUseCase.execute(sucursalId, req.getName());
                })
                .flatMap(sucursal -> {
                    APIResponse<Sucursal> response = APIResponse.<Sucursal>builder()
                            .code("200")
                            .message("Sucursal name updated successfully")
                            .identifier(sucursal.getId())
                            .date(LocalDateTime.now().toString())
                            .data(sucursal)
                            .build();
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                })
                .onErrorResume(IllegalArgumentException.class, e -> {
                    APIResponse<ErrorDTO> error = APIResponse.<ErrorDTO>builder()
                            .code("400")
                            .message(e.getMessage())
                            .date(LocalDateTime.now().toString())
                            .data(ErrorDTO.builder().code("UPDATE_SUCURSAL_NAME_ERROR").message(e.getMessage()).build())
                            .build();
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(error);
                })
                .onErrorResume(e -> {
                    APIResponse<ErrorDTO> error = APIResponse.<ErrorDTO>builder()
                            .code("500")
                            .message("Internal server error updating sucursal name")
                            .date(LocalDateTime.now().toString())
                            .data(ErrorDTO.builder().code("UPDATE_SUCURSAL_NAME_ERROR").message(e.getMessage()).build())
                            .build();
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(error);
                });
    }

}
