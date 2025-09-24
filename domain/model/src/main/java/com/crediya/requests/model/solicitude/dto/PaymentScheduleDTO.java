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
public class PaymentScheduleDTO {
    private Integer month;
    private BigDecimal monthlyPayment;
    private BigDecimal interestPayment;
    private BigDecimal capitalPayment;
    private BigDecimal remainingBalance;
}