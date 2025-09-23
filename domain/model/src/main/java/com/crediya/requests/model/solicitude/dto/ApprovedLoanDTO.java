package com.crediya.requests.model.solicitude.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovedLoanDTO {
    private BigDecimal amount;
    private BigDecimal monthlyInterest;
    private Integer months;
}
