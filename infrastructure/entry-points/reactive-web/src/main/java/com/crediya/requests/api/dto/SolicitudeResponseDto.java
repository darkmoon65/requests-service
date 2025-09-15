package com.crediya.requests.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudeResponseDto {
    private Integer id;
    private BigDecimal amount;
    private Integer term;
    private String email;
    private String stateName;
    private String loanTypeName;
    private String nameUser;
    private BigDecimal baseSalary;
}
