package com.crediya.requests.api.dto;

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

    @NotNull(message = "El plazo es obligatorio")
    @Positive(message = "El plazo debe ser mayor que 0")
    private Integer term;

    @Email(message = "El email no tiene un formato válido")
    private String email;

    @NotNull(message = "El tipo de préstamo es obligatorio")
    private Integer loanTypeId;
}
