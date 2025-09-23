package com.crediya.requests.model.solicitude.gateways;

import com.crediya.requests.model.solicitude.Solicitude;
import com.crediya.requests.model.solicitude.dto.SolicitudeWithNamesDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SolicitudeRepository {
    Mono<Solicitude> saveSolicitude(Solicitude solicitude);

    Flux<SolicitudeWithNamesDto> findPendingSolicitudes(int loanTypeId, int stateId, int limit, int offset);

    Mono<Long> countByStateId(int loanTypeId, int stateId);

    Mono<Solicitude> getById(Integer id);

    Flux<Solicitude> findAllApprovedLoansByEmail(String email);
}
