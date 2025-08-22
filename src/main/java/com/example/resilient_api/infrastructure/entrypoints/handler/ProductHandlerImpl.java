package com.example.resilient_api.infrastructure.entrypoints.handler;

import com.example.resilient_api.domain.model.Product;
import com.example.resilient_api.domain.usecase.UpdateProductNameUseCase;
import com.example.resilient_api.domain.usecase.UpdateProductStockUseCase;
import com.example.resilient_api.infrastructure.entrypoints.dto.UpdateProductNameRequest;
import com.example.resilient_api.infrastructure.entrypoints.util.APIResponse;
import com.example.resilient_api.infrastructure.entrypoints.util.ErrorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
public class ProductHandlerImpl {

    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;

    @Operation(
            summary = "Actualizar stock de un producto",
            description = "Permite actualizar la cantidad de stock de un producto existente",
            parameters = {
                    @Parameter(
                            name = "productId",
                            description = "ID del producto",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "PROD-01-01"
                    ),
                    @Parameter(
                            name = "stock",
                            description = "Nuevo stock del producto",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "50"
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stock actualizado",
                            content = @Content(schema = @Schema(implementation = APIResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                            content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno",
                            content = @Content)
            }
    )
    public Mono<ServerResponse> updateStock(ServerRequest request) {
        String productId = request.pathVariable("productId");
        int newStock = Integer.parseInt(request.pathVariable("stock")); // get stock from URL

        return updateProductStockUseCase.execute(productId, newStock)
                .flatMap(product -> {
                    APIResponse<?> response = APIResponse.builder()
                            .code("200")
                            .message("Stock updated successfully")
                            .identifier(product.getId())
                            .date(LocalDateTime.now().toString())
                            .data(product)
                            .build();
                    return ServerResponse.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                })
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(APIResponse.builder()
                                .code("400")
                                .message(e.getMessage())
                                .date(LocalDateTime.now().toString())
                                .build()));
    }

    @Operation(
            summary = "Actualizar nombre de un producto",
            description = "Permite cambiar el nombre de un producto existente",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID del producto",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "PROD-01-01"
                    )
            },
            requestBody = @RequestBody(
                    description = "Nombre nuevo del producto",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateProductNameRequest.class,
                                    example = "{ \"name\": \"Producto Nuevo\" }")
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
    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        String productId = request.pathVariable("id");

        return request.bodyToMono(UpdateProductNameRequest.class)
                .flatMap(req -> {
                    if (req.getName() == null || req.getName().isBlank()) {
                        return Mono.error(new IllegalArgumentException("The field 'name' is required"));
                    }
                    return updateProductNameUseCase.execute(productId, req.getName());
                })
                .flatMap(product -> {
                    APIResponse<Product> response = APIResponse.<Product>builder()
                            .code("200")
                            .message("Product name updated successfully")
                            .identifier(product.getId())
                            .date(LocalDateTime.now().toString())
                            .data(product)
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
                            .data(ErrorDTO.builder().code("UPDATE_PRODUCT_NAME_ERROR").message(e.getMessage()).build())
                            .build();
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(error);
                })
                .onErrorResume(e -> {
                    APIResponse<ErrorDTO> error = APIResponse.<ErrorDTO>builder()
                            .code("500")
                            .message("Internal server error updating product name")
                            .date(LocalDateTime.now().toString())
                            .data(ErrorDTO.builder().code("UPDATE_PRODUCT_NAME_ERROR").message(e.getMessage()).build())
                            .build();
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(error);
                });
    }
}
