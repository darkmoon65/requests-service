package com.crediya.requests.model.solicitude.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudeWithNamesDto {
    private Integer id;
    private BigDecimal amount;
    private Integer term;
    private String email;
    private String stateName;
    private String loanTypeName;
}
