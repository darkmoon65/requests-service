package com.crediya.requests.model.solicitude;
import lombok.*;
//import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Solicitude {
    private Integer id;
    private BigDecimal amount;
    private Integer term;
    private String email;
    private Integer stateId;
    private Integer loanTypeId;
}
