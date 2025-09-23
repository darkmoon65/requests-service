package com.crediya.requests.model.solicitude.dto;

import java.math.BigDecimal;

public record LoanDetails(
    BigDecimal amount,
    Integer months
) {}