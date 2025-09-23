package com.crediya.requests.model.loantype.gateways;

import com.crediya.requests.model.loantype.LoanType;
import reactor.core.publisher.Mono;

public interface LoanTypeRepository {
    Mono<Boolean> existsById(Integer loanTypeId);
    Mono<Boolean> hasAutomaticValidation(Integer loanTypeId);
    Mono<LoanType> findInterestRateById(Integer loanTypeId);
}
