package com.example.resilient_api.infrastructure.entrypoints.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Estructura estándar de respuesta de la API")
public class APIResponse<T> {
    @Schema(description = "Código de respuesta", example = "200")
    private String code;

    @Schema(description = "Mensaje descriptivo", example = "Operación exitosa")
    private String message;

    @Schema(description = "Identificador del recurso relacionado", example = "123e4567-e89b-12d3-a456-426614174000")
    private String identifier;

    @Schema(description = "Fecha y hora de la respuesta", example = "2025-08-21T12:00:00")
    private String date;

    @Schema(description = "Datos devueltos por la API")
    private T data;

    @Schema(description = "Datos del Error")
    private List<ErrorDTO> errors;
}
