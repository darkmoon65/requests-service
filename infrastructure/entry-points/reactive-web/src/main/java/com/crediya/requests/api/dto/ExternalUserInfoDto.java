package com.crediya.requests.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalUserInfoDto {
    private String name;
    private BigDecimal baseSalary;
}