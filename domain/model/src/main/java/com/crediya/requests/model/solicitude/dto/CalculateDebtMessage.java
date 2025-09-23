package com.crediya.requests.model.solicitude.dto;

import java.math.BigDecimal;
import java.util.List;

public record CalculateDebtMessage (
    BigDecimal interestRate,
    UserConsumer userConsumer,
    List<LoanDetails> approvedLoans,
    LoanDetails newLoan
) {}
