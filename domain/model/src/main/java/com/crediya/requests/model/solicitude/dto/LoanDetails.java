package com.crediya.requests.model.solicitude.dto;

import java.math.BigDecimal;

public record LoanDetails(
    Integer solicitudeId,
    BigDecimal amount,
    Integer months
) {}