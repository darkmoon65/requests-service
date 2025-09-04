package com.crediya.requests.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateSolicitudeDto {

    @NotBlank(message = "El documento de identidad es obligatorio")
    private String documentNumber;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    private BigDecimal amount;

    @Schema(description = "El plazo maximo es 360 dias", example = "360")
    @NotNull(message = "El plazo es obligatorio")
    @Positive(message = "El plazo debe ser mayor que 0")
    private Integer term;

    @Email(message = "El email no tiene un formato válido")
    private String email;

    @Schema(description = "Tipo de prestamo id (1,2,3)", example = "1")
    @Min(value = 1, message = "El loanTypeId mínimo permitido es 1")
    @Max(value = 3, message = "El loanTypeId máximo permitido es 3")
    @NotNull(message = "El tipo de préstamo es obligatorio")
    private Integer loanTypeId;
}
