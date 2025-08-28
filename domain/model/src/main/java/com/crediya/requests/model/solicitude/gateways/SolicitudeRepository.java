package com.crediya.requests.model.solicitude.gateways;

import com.crediya.requests.model.solicitude.Solicitude;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SolicitudeRepository {
    Mono<Solicitude> saveSolicitude(Solicitude solicitude);

    Flux<Solicitude> getAllSolicitudes();

}
