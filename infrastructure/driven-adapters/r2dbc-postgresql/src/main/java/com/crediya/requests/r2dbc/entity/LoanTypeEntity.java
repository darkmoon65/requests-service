package com.crediya.requests.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table("loan_type")
public class LoanTypeEntity {

    @Id
    private Integer id;

    @NonNull
    private String name;

    @NonNull
    @Column("min_amount")
    private BigDecimal minAmount;

    @NonNull
    @Column("max_amount")
    private BigDecimal maxAmount;

    @NonNull
    @Column("interest_rate")
    private BigDecimal interestRate;

    @NonNull
    @Column("auto_validation")
    private Boolean autoValidation;
}