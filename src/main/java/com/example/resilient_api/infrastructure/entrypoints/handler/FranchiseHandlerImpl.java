package com.example.resilient_api.infrastructure.entrypoints.handler;

import com.example.resilient_api.domain.model.Franchise;
import com.example.resilient_api.domain.usecase.CreateFranchiseUseCase;
import com.example.resilient_api.domain.usecase.AddBranchToFranchiseUseCase;
import com.example.resilient_api.domain.usecase.GetMaxStockPerBranchUseCase;
import com.example.resilient_api.domain.usecase.UpdateFranchiseNameUseCase;
import com.example.resilient_api.infrastructure.entrypoints.dto.Branch;
import com.example.resilient_api.infrastructure.entrypoints.dto.FranchiseMaxStockDTO;
import com.example.resilient_api.infrastructure.entrypoints.dto.UpdateFranchiseNameRequest;
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
public class FranchiseHandlerImpl {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;
    private final GetMaxStockPerBranchUseCase getMaxStockPerBranchUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

    @Operation(
            summary = "Crear una nueva franquicia",
            description = "Permite crear una franquicia con su información básica",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la franquicia a crear",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Franchise.class, example = "{ \"nombre\": \"Franquicia XYZ\"}")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Franquicia creada",
                            content = @Content(schema = @Schema(implementation = APIResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Error al crear la franquicia",
                            content = @Content)
            }
    )
    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(Franchise.class)
                .flatMap(createFranchiseUseCase::execute)
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

    @Operation(
            summary = "Agregar sucursal a una franquicia",
            description = "Asocia una sucursal existente a la franquicia",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "id",
                            description = "ID de la franquicia",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "FRANQ-001"
                    )
            },

            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Sucursal a agregar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Branch.class,
                                    example = "{ \"branchId\": \"Sucursal1\" }")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucursal agregada",
                            content = @Content(schema = @Schema(implementation = APIResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                            content = @Content),
                    @ApiResponse(responseCode = "404", description = "Franquicia no encontrada",
                            content = @Content)
            }
    )
    public Mono<ServerResponse> addBranchToFranchise(ServerRequest request) {
        String franchiseId = request.pathVariable("id");

        return request.bodyToMono(Branch.class)
                .flatMap(req -> {
                    if (req.getBranchId() == null || req.getBranchId().isBlank()) {
                        return Mono.error(new IllegalArgumentException("El campo 'branchId' es obligatorio"));
                    }
                    return addBranchToFranchiseUseCase.execute(franchiseId, req.getBranchId());
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

    @Operation(
            summary = "Obtener producto con mayor stock por sucursal",
            description = "Devuelve para cada sucursal de la franquicia el producto con mayor stock",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "id",
                            description = "ID de la franquicia",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "FRANQ-001"
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = APIResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Franquicia no encontrada",
                            content = @Content)
            }
    )
    public Mono<ServerResponse> getMaxStockPerBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("id");

        return getMaxStockPerBranchUseCase.execute(franchiseId)
                .flatMap(franchiseMaxStock -> {
                    APIResponse<FranchiseMaxStockDTO> response = APIResponse.<FranchiseMaxStockDTO>builder()
                            .code("200")
                            .message("Max stock per branch retrieved successfully")
                            .identifier(franchiseId)
                            .date(LocalDateTime.now().toString())
                            .data(franchiseMaxStock)
                            .build();

                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    APIResponse<Void> notFoundResponse = APIResponse.<Void>builder()
                            .code("404")
                            .message("Franchise not found")
                            .identifier(franchiseId)
                            .date(LocalDateTime.now().toString())
                            .build();
                    return ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(notFoundResponse);
                }))
                .onErrorResume(e -> {
                    APIResponse<Void> errorResponse = APIResponse.<Void>builder()
                            .code("500")
                            .message("Error retrieving max stock per branch : "+ e.getMessage())
                            .identifier(franchiseId)
                            .date(LocalDateTime.now().toString())
                            .build();
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(errorResponse);
                });
    }

    @Operation(
            summary = "Actualizar nombre de una franquicia",
            description = "Permite cambiar el nombre de la franquicia",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "id",
                            description = "ID de la franquicia",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "FRANQ-001"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevo nombre de la franquicia",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateFranchiseNameRequest.class,
                                    example = "{ \"name\": \"Franquicia ABC\" }")
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
    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String franchiseId = request.pathVariable("id");

        return request.bodyToMono(UpdateFranchiseNameRequest.class)
                .flatMap(req -> {
                    if (req.getName() == null || req.getName().isBlank()) {
                        return Mono.error(new IllegalArgumentException("The field 'name' is required"));
                    }
                    return updateFranchiseNameUseCase.execute(franchiseId, req.getName());
                })
                .flatMap(franchise -> {
                    APIResponse<Franchise> response = APIResponse.<Franchise>builder()
                            .code("200")
                            .message("Franchise name updated successfully")
                            .identifier(franchise.getId())
                            .date(LocalDateTime.now().toString())
                            .data(franchise)
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
                            .data(ErrorDTO.builder().code("UPDATE_FRANCHISE_NAME_ERROR").message(e.getMessage()).build())
                            .build();
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(error);
                })
                .onErrorResume(e -> {
                    APIResponse<ErrorDTO> error = APIResponse.<ErrorDTO>builder()
                            .code("500")
                            .message("Internal server error updating franchise name")
                            .date(LocalDateTime.now().toString())
                            .data(ErrorDTO.builder().code("UPDATE_FRANCHISE_NAME_ERROR").message(e.getMessage()).build())
                            .build();
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(error);
                });
    }

}
