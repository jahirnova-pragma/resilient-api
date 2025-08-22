package com.example.resilient_api.infrastructure.entrypoints.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Detalle de un error en la API")
public class ErrorDTO {
    @Schema(description = "Código interno del error", example = "UPDATE_FRANCHISE_NAME_ERROR")
    private String code;
    @Schema(description = "Parámetro relacionado con el error", example = "id de la franquicia")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String param;
    @Schema(description = "Mensaje explicativo del error", example = "El campo 'name' es obligatorio")
    private String message;
}
