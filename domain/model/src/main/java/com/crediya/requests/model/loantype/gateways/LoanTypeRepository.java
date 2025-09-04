package com.crediya.requests.model.loantype.gateways;

import reactor.core.publisher.Mono;

public interface LoanTypeRepository {
    Mono<Boolean> existsById(Integer loanTypeId);
}
