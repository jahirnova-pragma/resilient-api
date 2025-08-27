package com.example.resilient_api.infrastructure.entrypoints;

import com.example.resilient_api.domain.model.Franchise;
import com.example.resilient_api.infrastructure.entrypoints.dto.Branch;
import com.example.resilient_api.infrastructure.entrypoints.dto.UpdateFranchiseNameRequest;
import com.example.resilient_api.infrastructure.entrypoints.dto.UpdateProductNameRequest;
import com.example.resilient_api.infrastructure.entrypoints.dto.UpdateBranchNameRequest;
import com.example.resilient_api.infrastructure.entrypoints.handler.FranchiseHandlerImpl;
import com.example.resilient_api.infrastructure.entrypoints.handler.ProductHandlerImpl;
import com.example.resilient_api.infrastructure.entrypoints.handler.BranchHandlerImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
@Configuration
public class ApiRouter {

    @Bean
    @RouterOperations({

            @RouterOperation(
                    path = "/franchises",
                    beanClass = FranchiseHandlerImpl.class,
                    beanMethod = "createFranchise",
                    operation = @Operation(
                            summary = "Crear franquicia",
                            requestBody = @RequestBody(
                                    description = "Objeto Franchise",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = Franchise.class)
                                    )
                            )
                    )
            ),
            @RouterOperation(
                    path = "/franchises/{id}/branchs",
                    beanClass = FranchiseHandlerImpl.class,
                    beanMethod = "addBranchToFranchise",
                    operation = @Operation(
                            summary = "Agregar sucursal",
                            parameters = {
                                    @Parameter(name = "id", description = "ID de la franquicia", required = true)
                            },
                            requestBody = @RequestBody(
                                    description = "Objeto AddSucursalRequest",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = Branch.class)
                                    )
                            )
                    )
            ),
            @RouterOperation(
                    path = "/franchises/{id}/max-stock",
                    beanClass = FranchiseHandlerImpl.class,
                    beanMethod = "getMaxStockPerBranch",
                    operation = @Operation(
                            summary = "Obtener producto con mayor stock",
                            parameters = {
                                    @Parameter(name = "id", description = "ID de la franquicia", required = true)
                            }
                    )
            ),
            @RouterOperation(
                    path = "/franchises/{id}",
                    beanClass = FranchiseHandlerImpl.class,
                    beanMethod = "updateFranchiseName",
                    operation = @Operation(
                            summary = "Actualizar nombre franquicia",
                            parameters = {
                                    @Parameter(name = "id", description = "ID de la franquicia", required = true)
                            },
                            requestBody = @RequestBody(
                                    description = "Nuevo nombre de franquicia",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = UpdateFranchiseNameRequest.class)
                                    )
                            )
                    )
            ),
            @RouterOperation(
                    path = "/products/{productId}/stock/{stock}",
                    beanClass = ProductHandlerImpl.class,
                    beanMethod = "updateStock",
                    operation = @Operation(
                            summary = "Actualizar stock de un producto",
                            description = "Permite actualizar la cantidad de stock de un producto existente",
                            parameters = {
                                    @Parameter(
                                            name = "productId",
                                            description = "ID del producto",
                                            required = true

                                    ),
                                    @Parameter(
                                            name = "stock",
                                            description = "Nuevo stock del producto",
                                            required = true

                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/products/{id}",
                    beanClass = ProductHandlerImpl.class,
                    beanMethod = "updateProductName",
                    operation = @Operation(
                            summary = "Actualizar nombre de un producto",
                            description = "Permite cambiar el nombre de un producto existente",
                            parameters = {
                                    @Parameter(
                                            name = "id",
                                            description = "ID del producto",
                                            required = true

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
                            )
                    )
            ),
            @RouterOperation(
                    path = "/branchs/{branchId}/productos/{productoId}",
                    method = RequestMethod.POST,
                    beanClass = BranchHandlerImpl.class,
                    beanMethod = "addProducto",
                    operation = @Operation(
                            summary = "Agregar producto a una sucursal",
                            description = "Permite agregar un producto existente a una sucursal específica",
                            parameters = {
                                    @Parameter(
                                            name = "branchId",
                                            description = "ID de la sucursal",
                                            required = true
                                    ),
                                    @Parameter(
                                            name = "productoId",
                                            description = "ID del producto a agregar",
                                            required = true
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/branchs/{branchId}/productos/{productoId}",
                    method = RequestMethod.DELETE,
                    beanClass = BranchHandlerImpl.class,
                    beanMethod = "removeProduct",
                    operation = @Operation(
                            summary = "Eliminar producto de una sucursal",
                            description = "Permite remover un producto específico de una sucursal",
                            parameters = {
                                    @Parameter(
                                            name = "branchId",
                                            description = "ID de la sucursal",
                                            required = true
                                    ),
                                    @Parameter(
                                            name = "productoId",
                                            description = "ID del producto a eliminar",
                                            required = true
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/branchs/{id}",
                    beanClass = BranchHandlerImpl.class,
                    beanMethod = "updateBranchName",
                    operation = @Operation(
                            summary = "Actualizar nombre de una sucursal",
                            description = "Permite cambiar el nombre de una sucursal existente",
                            parameters = {
                                    @Parameter(
                                            name = "id",
                                            description = "ID de la sucursal",
                                            required = true
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Nuevo nombre de la sucursal",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = UpdateBranchNameRequest.class,
                                                    example = "{ \"name\": \"Sucursal ABC\" }")
                                    )
                            )
                    )
            )
    })
    public RouterFunction<ServerResponse> apiRoutes(
            FranchiseHandlerImpl franchiseHandler,
            ProductHandlerImpl productHandler,
            BranchHandlerImpl branchHandler) {

        return route(POST("/franchises").and(accept(MediaType.APPLICATION_JSON)), franchiseHandler::createFranchise)
                .andRoute(POST("/franchises/{id}/branchs").and(accept(MediaType.APPLICATION_JSON)), franchiseHandler::addBranchToFranchise)
                .andRoute(GET("/franchises/{id}/max-stock").and(accept(MediaType.APPLICATION_JSON)), franchiseHandler::getMaxStockPerBranch)
                .andRoute(PATCH("/franchises/{id}").and(accept(MediaType.APPLICATION_JSON)), franchiseHandler::updateFranchiseName)

                .andRoute(PUT("/products/{productId}/stock/{stock}").and(accept(MediaType.APPLICATION_JSON)), productHandler::updateStock)
                .andRoute(PATCH("/products/{id}").and(accept(MediaType.APPLICATION_JSON)), productHandler::updateProductName)

                .andRoute(POST("/branchs/{branchId}/products/{productoId}").and(accept(MediaType.APPLICATION_JSON)), branchHandler::addProducto)
                .andRoute(DELETE("/branchs/{branchId}/products/{productId}").and(accept(MediaType.APPLICATION_JSON)), branchHandler::removeProduct)
                .andRoute(PATCH("/branchs/{id}").and(accept(MediaType.APPLICATION_JSON)), branchHandler::updateBranchName);
    }
}
