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
@Table("solicitudes")
public class SolicitudeEntity {

    @Id
    @Column("solicitude_id")
    private String id;

    @NonNull
    private BigDecimal amount;

    @NonNull
    private Integer term;

    @NonNull
    private String email;

    @NonNull
    @Column("state_id")
    private Integer stateId;

    @NonNull
    @Column("loan_type_id")
    private Integer loanTypeId;
}