package com.crediya.requests.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateSolicitudeDto {

    @NotNull(message = "El id de prestamo es obligatorio")
    private Integer id;

    @Schema(description = "Estado de prestamo id (1,2,3)", example = "1")
    @Min(value = 1, message = "El stateId mínimo permitido es 1")
    @Max(value = 3, message = "El stateId máximo permitido es 3")
    @NotNull(message = "El id de estado de prestamo es obligatorio")
    private Integer stateId;
}
