package com.crediya.requests.usecase.solicitude;

import com.crediya.requests.model.loantype.gateways.LoanTypeRepository;
import com.crediya.requests.model.solicitude.Solicitude;
import com.crediya.requests.model.solicitude.gateways.SolicitudeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class SolicitudeUseCase {
    private final SolicitudeRepository solicitudeRepository;
    private final LoanTypeRepository loanTypeRepository;

    public Mono<Solicitude> saveSolicitude(Solicitude solicitude) {
        return loanTypeRepository.existsById(solicitude.getLoanTypeId())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("Id tipo de prestamo invalido: "
                                + solicitude.getLoanTypeId()));
                    }
                    solicitude.setStateId(1);
                    return solicitudeRepository.saveSolicitude(solicitude);
                });
    }
}
